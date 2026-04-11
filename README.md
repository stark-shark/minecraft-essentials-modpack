# Essentials Modpack

A curated Fabric modpack for Minecraft 26.1.x focused on performance, quality-of-life, and visual enhancements. Client-side only — no server installation needed.

Created by [stark-shark](https://github.com/stark-shark) | [Modrinth](https://modrinth.com/project/mc-essentials-modpack)

[![Support on Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/stark_shark)

## Quick Start

### One-Click Install (Windows)
1. Install [Java 25 JDK](https://adoptium.net/) and [Fabric Loader](https://fabricmc.net/use/installer/) for your MC version
2. Download or clone this repo
3. Double-click **`install-modpack.bat`**
4. Launch Minecraft with your Fabric Loader profile

The installer builds the Essentials mod from source, auto-resolves the latest compatible mods from Modrinth, and copies everything to your `.minecraft/mods/` folder.

To uninstall, double-click **`uninstall-modpack.bat`**.

## Supported Versions

| Minecraft | Essentials | Status |
|-----------|-----------|--------|
| **26.1.x** (26.1, 26.1.1, 26.1.2) | v2.0.0 | Current |

See [`versions/`](versions/) for per-version compatibility notes.

## Requirements

- Minecraft Java Edition 26.1.x
- Java 25 JDK
- Fabric Loader 0.18.6+

## Included Mods (11)

### Custom
| Mod | Features |
|---|---|
| **Essentials** v2.0.0 | Emissive textures, zoom, fullbright, fog control, dynamic FPS, borderless fullscreen, health bars, pickup notifier, status effect bars, coords HUD, durability display, death waypoint, auto-clicker, container tooltips (shulker/ender chest/map), hotbar auto-refill, inventory sort, container management buttons, enhanced item tooltips, match highlighting |

### Dependencies
| Mod | Source |
|---|---|
| [Fabric API](https://modrinth.com/mod/fabric-api) | [GitHub](https://github.com/FabricMC/fabric) |
| [Cloth Config](https://modrinth.com/mod/cloth-config) | [GitHub](https://github.com/shedaniel/cloth-config) |
| [Mod Menu](https://modrinth.com/mod/modmenu) | [GitHub](https://github.com/TerraformersMC/ModMenu) |

### Performance
| Mod | Source |
|---|---|
| [Sodium](https://modrinth.com/mod/sodium) | [GitHub](https://github.com/CaffeineMC/sodium) |
| [Lithium](https://modrinth.com/mod/lithium) | [GitHub](https://github.com/CaffeineMC/lithium-fabric) |

### Visual
| Mod | Source |
|---|---|
| [Iris Shaders](https://modrinth.com/mod/iris) | [GitHub](https://github.com/IrisShaders/Iris) |
| [LambDynamicLights](https://modrinth.com/mod/lambdynamiclights) | [GitHub](https://github.com/LambdAurora/LambDynamicLights) |
| [Chat Heads](https://modrinth.com/mod/chat-heads) | [GitHub](https://github.com/dzwdz/chat_heads) |

### Map
| Mod | Source |
|---|---|
| [JourneyMap](https://modrinth.com/mod/journeymap) | [GitHub](https://github.com/TeamJM/journeymap) |

### Quality of Life
| Mod | Source |
|---|---|
| [Mouse Tweaks](https://modrinth.com/mod/mouse-tweaks) | [GitHub](https://github.com/YaLTeR/MouseTweaks) |

## Essentials Mod — Keybinds

| Key | Action |
|---|---|
| **C** (hold) | Zoom (scroll to adjust) |
| **H** | Toggle fullbright |
| **F11** | Borderless fullscreen |
| **` (backtick)** | Toggle auto-click/hold |

All keybinds are configurable in Options > Controls > Essentials.

## Essentials Mod — Features

**Visuals**
- **Emissive Texture Overlays** — Resource pack `_e.png` textures glow at full brightness
- **Fullbright** — Toggle max brightness with one key
- **Fog Control** — Disable void, water, lava, and distance fog independently

**HUD**
- **Mob & Player Health Bars** — Colored indicators above entities with separate mob/player toggles
- **Pickup Notifier** — Item icon + count when picking up items, scalable
- **Status Effect Bars** — Duration progress bars on potion effect icons
- **Coordinates HUD** — Always-visible XYZ + facing direction, scalable
- **Armor Durability Display** — Item icons with colored durability bars, scalable
- **Death Waypoint** — Clickable death coordinates in chat on respawn

**Gameplay**
- **Zoom** — Smooth zoom with scroll-to-adjust, configurable FOV and sensitivity
- **Auto-Clicker** — Toggle hold or interval clicking with CPS slider, left/right click

**Container Tooltips** *(v2.0)*
- **Shulker Box Preview** — Visual 9x3 item grid with color-matched border
- **Ender Chest Preview** — Cached per world/server, persists across sessions
- **Map Preview** — 128x128 pixel-accurate map rendering in tooltip

**Inventory Management** *(v2.0)*
- **Hotbar Auto-Refill** — Replaces consumed hotbar items from inventory with per-category toggles
- **Inventory Sort** — Sort button in inventory, tracks recipe book position
- **Container Buttons** — Sort, Move All, Move Matching, Take All, Take Matching, Highlight Matching
- **Match Highlighting** — Red borders on items shared between container and inventory

**Enhanced Item Tooltips** *(v2.0)*
- **Tool Stats** — Mining speed with Efficiency enchant bonus (base → enchanted)
- **Weapon Stats** — Attack damage with Sharpness/Smite/Bane, attack speed, DPS
- **Ranged Stats** — Bow/crossbow arrow damage with Power, knockback, charge time, piercing
- **Armor Stats** — Armor points, toughness, damage reduction with Protection enchant bonuses
- **Food Stats** — Hunger bar visualization, saturation with quality rating
- **Durability** — Color-coded with Unbreaking effective durability
- **Per-item config** — Choose Tool/Weapon/Both per item type (Pickaxe, Axe, Sword, etc.)
- **Vanilla attribute stripping** — Removes redundant "When in Hand" section

**Performance**
- **Dynamic FPS** — Throttles FPS when unfocused (15) or minimized (1)
- **Borderless Fullscreen** — Fast alt-tab, replaces exclusive fullscreen

**Config**
- **Config Screen** — Sidebar layout with global search, sliders, grouped sections, reset defaults
- **Settings Tooltips** — Every setting has a hover tooltip (MC-style purple border)
- **Config Persistence** — All settings saved to `config/essentials.properties`
- **Mod Compatibility** — Auto-disables overlapping features when other mods detected

## Compatibility Notes

- **Sodium + Emissive Overlays** — May need testing; Sodium replaces the block renderer
- **Iris + Fullbright** — Shader packs may override fullbright
- **LambDynamicLights** — Provides handheld light glow (not built into Essentials)
- **Auto-defers** — Essentials disables its own features if conflicting mods are installed

## License

Essentials mod: MIT License. Other mods: see individual licenses.

## Support

If you find this modpack useful, consider supporting development:

[![Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/stark_shark)
