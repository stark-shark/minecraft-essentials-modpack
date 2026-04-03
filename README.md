# Essentials Modpack

A curated Fabric modpack for Minecraft 26.1 focused on performance, quality-of-life, and visual enhancements. Client-side only.

Created by [stark-shark](https://github.com/stark-shark) | [Modrinth](https://modrinth.com/project/mc-essentials-modpack)

[![Support on Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/stark_shark)

## Quick Start

### One-Click Install (Windows)
1. Install [Java 25 JDK](https://adoptium.net/) and [Fabric Loader](https://fabricmc.net/use/installer/) for MC 26.1
2. Download or clone this repo
3. Double-click **`install-modpack.bat`**
4. Launch Minecraft with the `fabric-loader-26.1` profile

That's it. The installer builds the Essentials mod, downloads all 15 mods from Modrinth, and copies everything to your `.minecraft/mods/` folder.

### Manual Install
```bash
cd essentials && gradlew.bat build && cd ..
.\download-mods.bat
.\install.bat
```

## Requirements

- Minecraft Java Edition 26.1
- Java 25 JDK
- Fabric Loader 0.18.6+

## Included Mods (11)

### Custom
| Mod | Features |
|---|---|
| **Essentials** | Emissive textures, zoom, fullbright, fog control, dynamic FPS, borderless fullscreen, mob/player health bars, pickup notifier, status effect bars, coords HUD, armor durability, death waypoint, auto-clicker, config screen |

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

### Planned — Waiting for 26.1 Update
These mods will be added to the modpack once they release builds for MC 26.1:

| Mod | Source | What It Does |
|---|---|---|
| [Detail Armor Bar](https://modrinth.com/mod/detail-armor-bar) | [GitHub](https://github.com/RedLime/DetailArmorBar) | Enchantment-colored armor bar icons |
| [Pick Up Notifier](https://modrinth.com/mod/pick-up-notifier) | [GitHub](https://github.com/Fuzss/pickupnotifier) | Item pickup display (we built our own in Essentials) |
| [Easy Shulker Boxes](https://modrinth.com/mod/easy-shulker-boxes) | [GitHub](https://github.com/Fuzss/easyshulkerboxes) | Preview/interact with shulker boxes in inventory |
| [REI (Roughly Enough Items)](https://modrinth.com/mod/rei) | [GitHub](https://github.com/shedaniel/RoughlyEnoughItems) | Recipe viewer |

## Essentials Mod — Keybinds

| Key | Action |
|---|---|
| **C** (hold) | Zoom (scroll to adjust) |
| **H** | Toggle fullbright |
| **F11** | Borderless fullscreen |
| **` (backtick)** | Toggle auto-click/hold |

All keybinds are configurable in Options > Controls > Essentials. Conflicts show in red.

## Essentials Mod — Features

1. **Emissive Texture Overlays** — Resource pack `_e.png` textures glow at full brightness
2. **Zoom** — Smooth zoom with scroll-to-adjust, configurable FOV and sensitivity
3. **Fullbright** — Toggle max brightness with one key
4. **Fog Control** — Disable void, water, lava, and distance fog independently
5. **Dynamic FPS** — Throttles FPS when unfocused (15) or minimized (1)
6. **Borderless Fullscreen** — Fast alt-tab, replaces exclusive fullscreen
7. **Mob & Player Health Bars** — Colored block indicators above entities, separate mob/player toggles
8. **Pickup Notifier** — Item icon + count when picking up items, scalable size
9. **Status Effect Bars** — Duration progress bars on effect icons
10. **Coordinates HUD** — Always-visible XYZ + facing, scalable
11. **Armor Durability Display** — Item icons with colored durability bars, scalable
12. **Death Waypoint** — Clickable death coordinates in chat on respawn
13. **Auto-Clicker** — Toggle hold or interval clicking with CPS slider
14. **Config Screen** — Sidebar layout with global search, sliders, grouped sections, reset defaults
15. **Config Persistence** — All settings saved to `config/essentials.properties`
16. **Mod Compatibility** — Auto-disables overlapping features when other mods detected

## Compatibility Notes

- **Sodium + Emissive Overlays**: May need testing — Sodium replaces the block renderer
- **Iris + Fullbright**: Shader packs may override fullbright
- **LambDynamicLights**: Provides handheld light glow (not built into Essentials)
- **Auto-defers**: Essentials disables its own zoom/dynamic FPS if Zoomify/Dynamic FPS mods are installed

## License

Essentials mod: MIT License. Other mods: see individual licenses.

## Support

If you find this modpack useful, consider supporting development:

[![Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/stark_shark)
