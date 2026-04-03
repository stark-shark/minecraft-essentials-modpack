package com.essentials.hud;

import com.essentials.EssentialsMod;
import com.essentials.config.EssentialsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.Vec3;

/**
 * Logs death coordinates to chat when the player dies.
 * The coordinates are clickable (copies to clipboard).
 */
public class DeathWaypoint {

    private static Vec3 lastDeathPos = null;
    private static String lastDeathDimension = null;
    private static boolean wasDead = false;

    public static void tick(Minecraft mc) {
        if (!EssentialsConfig.deathWaypointEnabled) return;
        if (mc.player == null) {
            wasDead = false;
            return;
        }

        boolean isDead = mc.player.isDeadOrDying();

        // Detect transition to dead
        if (isDead && !wasDead) {
            lastDeathPos = mc.player.position();
            lastDeathDimension = mc.level != null ?
                    cleanDimensionName(mc.level.dimension().identifier().toString()) : "Unknown";

            EssentialsMod.LOGGER.info("[Essentials] Death at {} in {}",
                    String.format("%.0f, %.0f, %.0f", lastDeathPos.x, lastDeathPos.y, lastDeathPos.z),
                    lastDeathDimension);
        }

        // When player respawns (was dead, now alive), show the message
        if (!isDead && wasDead && lastDeathPos != null) {
            showDeathMessage(mc);
        }

        wasDead = isDead;
    }

    private static void showDeathMessage(Minecraft mc) {
        BlockPos pos = BlockPos.containing(lastDeathPos);
        String coordStr = pos.getX() + ", " + pos.getY() + ", " + pos.getZ();

        MutableComponent msg = Component.literal("[Essentials] ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("You died at ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(coordStr).withStyle(Style.EMPTY
                        .withColor(ChatFormatting.AQUA)
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent.CopyToClipboard(coordStr))))
                .append(Component.literal(" in ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(lastDeathDimension).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" (click to copy)").withStyle(ChatFormatting.GRAY));

        mc.player.sendSystemMessage(msg);
    }

    public static Vec3 getLastDeathPos() { return lastDeathPos; }
    public static String getLastDeathDimension() { return lastDeathDimension; }

    private static String cleanDimensionName(String id) {
        // "minecraft:overworld" -> "Overworld", "minecraft:the_nether" -> "The Nether"
        String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
        // "overworld" -> "the_overworld", "the_nether" stays, "the_end" stays
        if (path.equals("overworld")) path = "the_overworld";
        String[] words = path.replace('_', ' ').split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }
        return sb.toString();
    }
}
