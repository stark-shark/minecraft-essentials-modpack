package com.essentials.hud;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;

public class CoordsHud {

    public static void render(GuiGraphicsExtractor graphics, Minecraft mc) {
        if (!EssentialsConfig.coordsHudEnabled) return;
        if (mc.player == null || mc.level == null) return;
        if (mc.options.hideGui) return;

        Entity camera = mc.getCameraEntity();
        if (camera == null) return;

        Font font = mc.font;
        Vec3 pos = camera.position();
        Direction facing = camera.getDirection();
        String facingName = facing.getName().substring(0, 1).toUpperCase() + facing.getName().substring(1);
        String text = String.format("%.0f, %.0f, %.0f  %s", pos.x, pos.y, pos.z, facingName);

        float scale = EssentialsConfig.coordsHudScale;
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(scale, scale);

        int scaledX = (int)(4 / scale);
        int scaledY = (int)(4 / scale);
        int textWidth = font.width(text);

        graphics.fill(scaledX - 2, scaledY - 2, scaledX + textWidth + 2, scaledY + 11, 0x60000000);
        graphics.text(font, text, scaledX, scaledY, 0xFFFFFFFF, true);

        pose.popMatrix();
    }
}
