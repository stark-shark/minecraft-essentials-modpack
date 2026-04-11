# Essentials Mod Changelog

## v2.0.1 — 2026-04-11

Bug fixes and polish for v2.0.0.

### Fixes
- **Ender chest cache on realms/servers** — rewrote capture to use screen title detection instead of server-side `PlayerEnderChestContainer` methods that never fire on the client
- **Sort button tracks recipe book** — repositions via `containerTick` instead of `extractSlots` (which is overridden by `AbstractRecipeBookScreen`)
- **Container buttons filtered** — only appear on storage containers (chests, barrels, hoppers, shulker boxes, dispensers/droppers), not crafting tables/furnaces/anvils/etc
- **Enchantment lookup fixed** — reads directly from item's `ENCHANTMENTS` data component instead of level registry (which failed on realms)
- **Vanilla "When in/on" stripping** — now removes ALL equipment slot sections in one pass

### Enhanced Tooltips — Expanded
- **Armor stats** — armor points, toughness, knockback resistance, damage reduction with base → enchanted display for Protection/Fire/Blast/Projectile Protection
- **Ranged weapon stats** — bow arrow damage with Power bonus, knockback from Punch; crossbow damage, charge time from Quick Charge, piercing level
- **Weapon durability** — always shown in the weapon section for weapon-primary items (sword, bow, crossbow, mace, trident)
- **Durability placement** — follows item identity: tools show in tool section, weapons in weapon section
- **Sharpness/Smite/Bane** — attack damage shows base → enchanted, conditional bonuses for Smite/Bane
- **Unbreaking** — all items show effective durability (base → ~effective)

### Installer
- Auto-resolves latest mod versions from Modrinth API (no hardcoded URLs)
- Uses temp `.ps1` file for reliable PowerShell JSON parsing
- Falls back through 26.1.2 → 26.1.1 → 26.1 for compatibility
- `clean build` prevents stale jar pickup
- Updated for MC 26.1.2

---

## v2.0.0 — 2026-04-10

Major feature update with container tooltips, inventory management, and enhanced item info.

### New Features

#### Container Tooltips
- **Shulker Box Preview** — hover over a shulker box to see a 9x3 item grid with color-matched border
- **Ender Chest Preview** — cached per world/server and persists across sessions (saved to disk)
- **Map Preview** — 128x128 pixel-accurate map rendering in tooltip
- **Vanilla text list suppressed** — visual grid replaces redundant item name list

#### Inventory Management
- **Hotbar Auto-Refill** — automatically replaces consumed hotbar items from inventory
  - Per-category toggles: Blocks, Tools, Weapons, Food, Containers, Potions, Projectiles, Other
  - Refill on Drop toggle (off by default) — controls Q/Ctrl+Q behavior
  - Match Type Only toggle — loose (any pickaxe) vs strict (exact same item) matching
  - 3-tick drop key detection window and 5-tick per-slot cooldown for reliability
- **Inventory Sort** — sorts by category (Weapons > Tools > Armor > Blocks > Food > Materials > Other), then alphabetically
- **Container Buttons** (in chest/barrel/shulker screens):
  - Sort Container, Move Matching Items, Move All (inventory to container)
  - Sort Inventory, Take Matching Items, Take All (container to inventory)
  - Highlight Matching Items — red border on slots with items in both container and inventory
  - Highlights auto-clear when items are moved or container is closed

#### Enhanced Item Tooltips
- **Tool Stats** — mining speed (with Efficiency enchant: base → enchanted), durability with percentage
- **Weapon Stats** — attack damage, attack speed, DPS calculation
- **Food Stats** — hunger bar visualization, saturation value with quality rating (Poor/Low/Good/Excellent)
- **Separator line** between tool and weapon sections on dual-purpose items (axes)
- **Per-item-type stat config** — choose Tool/Weapon/Both for each type (Pickaxe, Axe, Shovel, Hoe, Sword, Mace)
- **Vanilla "When in Hand" section removed** for items with enhanced stats

### UX Improvements
- **Settings tooltips** — every setting now has a descriptive hover tooltip (MC-style purple border)
- **Tooltip positioning** — appears below mouse, flips when near screen edges
- **Mod compatibility detection** — auto-disables container tooltips if Easy Shulker Boxes or ShulkerBoxTooltip detected
- **Config screen** — new Inventory and Tooltips categories with full toggle control

### Technical
- 8 new source files (tooltip system, inventory management, container actions, match highlighting)
- 7 new mixins (Item tooltips, container text suppression, ender chest capture, container sort button, highlight rendering, slot change detection)
- Fabric `ClientTooltipComponentCallback` for tooltip registration (no mixin on ClientTooltipComponent)
- Fabric `ItemTooltipCallback` for enhanced tooltips (fires after enchantments)
- Fabric `ClientPlayConnectionEvents` for ender chest cache lifecycle
- NBT codec serialization for persistent ender chest cache

---

## v0.1.0 — Initial Release

Core QoL features: emissive textures, zoom, fullbright, fog control, dynamic FPS, borderless fullscreen, health bars, pickup notifier, status effect bars, coords HUD, durability display, death waypoint, auto-clicker.
