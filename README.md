# Essentials Modpack

A curated Fabric modpack for Minecraft 26.1 focused on performance, quality-of-life, and visual enhancements. Client-side only.

Created by [stark-shark](https://github.com/stark-shark)

## Quick Start

```bash
# 1. Build the custom Essentials mod
cd essentials
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.2
.\gradlew.bat build
cd ..

# 2. Download all mods from Modrinth
.\download-mods.bat

# 3. Install to .minecraft/mods
.\install.bat
```

## Requirements

- Minecraft Java Edition 26.1
- Java 25 JDK
- Fabric Loader 0.18.6+

## Included Mods (15)

### Custom
| Mod | Features |
|---|---|
| **Essentials** | Emissive textures, zoom, fullbright, fog control, dynamic FPS, borderless fullscreen, BetterF3 debug HUD, mob health bars, auto-clicker, pickup notifier, config screen |

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
| [Entity Culling](https://modrinth.com/mod/entityculling) | [GitHub](https://github.com/tr7zw/EntityCulling) |

### Visual
| Mod | Source |
|---|---|
| [Iris Shaders](https://modrinth.com/mod/iris) | [GitHub](https://github.com/IrisShaders/Iris) |
| [LambDynamicLights](https://modrinth.com/mod/lambdynamiclights) | [GitHub](https://github.com/LambdAurora/LambDynamicLights) |
| [Status Effect Bars](https://modrinth.com/mod/status-effect-bars) | [GitHub](https://github.com/A5b84/status-effect-bars) |
| [Chat Heads](https://modrinth.com/mod/chat-heads) | [GitHub](https://github.com/dzwdz/chat_heads) |

### Map
| Mod | Source |
|---|---|
| [JourneyMap](https://modrinth.com/mod/journeymap) | [GitHub](https://github.com/TeamJM/journeymap) |

### Quality of Life
| Mod | Source |
|---|---|
| [Mouse Tweaks](https://modrinth.com/mod/mouse-tweaks) | [GitHub](https://github.com/YaLTeR/MouseTweaks) |
| [Controlling](https://modrinth.com/mod/controlling) | [GitHub](https://github.com/jaredlll08/Controlling) |
| [No Chat Reports](https://modrinth.com/mod/no-chat-reports) | [GitHub](https://github.com/Aizistral-Studios/No-Chat-Reports) |

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
| **F3** | Custom debug HUD |
| **F3+M** | Open Essentials config |
| **` (backtick)** | Toggle auto-attack |

All keybinds are configurable in Options > Controls > Essentials. Conflicts show in red.

## Essentials Mod — Features

1. **Emissive Texture Overlays** — Resource pack `_e.png` textures glow at full brightness
2. **Zoom** — Smooth zoom with scroll-to-adjust, configurable FOV and sensitivity
3. **Fullbright** — Toggle max brightness with one key
4. **Fog Control** — Disable void, water, lava, and distance fog independently
5. **Dynamic FPS** — Throttles FPS when unfocused (15) or minimized (1)
6. **Borderless Fullscreen** — Fast alt-tab, replaces exclusive fullscreen
7. **Better Debug HUD** — 12 color-coded modules with per-line toggles, reordering, spacers, heightmap, slime chunks, moon phase, entity categories, block/fluid tags
8. **Mob Health Bars** — Colored block indicators above damaged entities
9. **Pickup Notifier** — Shows item name and total count when picking up items, stacks duplicates, fades after 5 seconds
10. **Auto-Clicker** — Toggle hold or interval clicking for mining/AFK
11. **Config Screen** — Sidebar layout with search, master toggles, greyed-out children, F3+M shortcut
12. **Config Persistence** — All settings saved to `config/essentials.properties`
13. **Mod Compatibility** — Auto-disables overlapping features when other mods detected

## Compatibility Notes

- **Sodium + Emissive Overlays**: May need testing — Sodium replaces the block renderer
- **Iris + Fullbright**: Shader packs may override fullbright
- **LambDynamicLights**: Provides handheld light glow (not built into Essentials)
- **Auto-defers**: Essentials disables its own zoom/debug HUD/dynamic FPS if Zoomify/BetterF3/Dynamic FPS mods are installed

## License

Essentials mod: MIT License. Other mods: see individual licenses.

## Support

If you find this modpack useful, consider supporting development:

[![Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/stark_shark)
