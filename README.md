# TPW Carpet Addition

[English](README.md) | [简体中文](README_zh.md)

A Minecraft **1.20.4** Fabric + Carpet Extension mod that adds practical features to vanilla Minecraft without introducing any new blocks, items, or entities.

> **Note:** **All options are disabled by default** to preserve vanilla behavior. Enable the features you want via `/carpet` (or `/carpet setDefault <rule> <value>` to persist across restarts).

---

## Requirements

- **Minecraft**: `1.20.4`
- **Fabric Loader**: `>= 0.15.0`
- **Fabric API**: `1.20.4`
- **Fabric Carpet**: `>= 1.4.128`

---

## `/carpet` Rules

Includes built-in `en_us` and `zh_cn` descriptions for `/carpet`.

| Rule | Default | Valid Range / Options | Description |
| --- | --- | --- | --- |
| `renewableSpongeInMonument` | `false` | `true` / `false` | Master toggle for sustainable wet sponge propagation inside Ocean Monuments (`minecraft:monument`) |
| `renewableSpongePrismarineThreshold` | `100` | `1–124` (suggestions: `80, 96, 100, 105, 110`) | Minimum prismarine-family blocks (`prismarine`, `prismarine_bricks`, `dark_prismarine`, `sea_lantern`) in `5×5×5` for both seed and target |
| `renewableSpongeChanceDenominator` | `60` | `>= 1` (suggestions: `30, 50, 60, 80, 100`) | Base hit probability denominator (`1 / value`) per random tick |
| `renewableSpongeConduitRadius` | `32` | `0–128` (suggestions: `16, 32, 48, 64`) | Active Conduit `BlockEntity` query radius in blocks for the `×3.0` rate bonus (`0` disables bonus) |
| `renewableSpongeMaxSpawn` | `3` | `1` or `3` (strict) | Max spawns per hit: `1` = fixed 1; `3` = weighted 1–3 (`50% / 30% / 20%`) |

### Quick Commands
```mcfunction
/carpet renewableSpongeInMonument true
/carpet setDefault renewableSpongeInMonument true
```

To adjust pacing or chamber requirements:
```mcfunction
/carpet renewableSpongePrismarineThreshold 100
/carpet renewableSpongeChanceDenominator 60
/carpet renewableSpongeConduitRadius 32
/carpet renewableSpongeMaxSpawn 3
```

---

## Building from Source

Requires **JDK 17+** (tested with OpenJDK 21).

```bash
./gradlew build
```

The remapped mod jar is generated at `build/libs/tpw-carpet-addition-<version>.jar`.
