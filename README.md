# Essentials Modpack

A curated Fabric modpack for Minecraft 26.1+ focused on performance, quality-of-life, and visual enhancements. Client-side only — no server installation needed.

Created by [stark-shark](https://github.com/stark-shark) | [Modrinth](https://modrinth.com/project/mc-essentials-modpack)

[![Support on Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/stark_shark)

## Quick Start

### One-Click Install (Windows)
1. Install [Java 25 JDK](https://adoptium.net/) and [Fabric Loader](https://fabricmc.net/use/installer/) for MC 26.1
2. Download or clone this repo
3. Double-click **`install-modpack.bat`**
4. Launch Minecraft with the `fabric-loader-26.1` profile

The installer builds the Essentials mod, downloads all mods from Modrinth, and copies everything to your `.minecraft/mods/` folder. If you have existing mods, you'll be warned before anything is touched.

To uninstall, double-click **`uninstall-modpack.bat`**.

## Requirements

- Minecraft Java Edition 26.1+
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

## Essentials Mod — Keybinds

| Key | Action |
|---|---|
| **C** (hold) | Zoom (scroll to adjust) |
| **H** | Toggle fullbright |
| **F11** | Borderless fullscreen |
| **` (backtick)** | Toggle auto-click/hold |

All keybinds are configurable in Options > Controls > Essentials. Conflicts show in red.

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

**Performance**
- **Dynamic FPS** — Throttles FPS when unfocused (15) or minimized (1)
- **Borderless Fullscreen** — Fast alt-tab, replaces exclusive fullscreen

**Config**
- **Config Screen** — Sidebar layout with global search, sliders, grouped sections, reset defaults
- **Config Persistence** — All settings saved to `config/essentials.properties`
- **Mod Compatibility** — Auto-disables overlapping features when other mods detected

## Compatibility Notes

- **Sodium + Emissive Overlays** — May need testing; Sodium replaces the block renderer
- **Iris + Fullbright** — Shader packs may override fullbright
- **LambDynamicLights** — Provides handheld light glow (not built into Essentials)
- **Auto-defers** — Essentials disables its own zoom/dynamic FPS if Zoomify/Dynamic FPS mods are installed

## License

Essentials mod: MIT License. Other mods: see individual licenses.

## Support

If you find this modpack useful, consider supporting development:

[![Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/stark_shark)
