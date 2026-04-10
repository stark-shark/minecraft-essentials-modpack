# Essentials Mod Changelog

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
