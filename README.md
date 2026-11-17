# TPW Carpet Addition

Minecraft **1.20.4** Fabric + Carpet Extension mod implementing a sustainable **Wet Sponge** propagation mechanic inside Ocean Monuments.

## Requirements
- Minecraft `1.20.4`
- Fabric Loader `>= 0.15.0`
- Fabric API
- Fabric Carpet `>= 1.4.128`

Compatible with **Lithium** (`BlockState` random-tick cache initialized cleanly with hot-toggle check at `randomTick` entry) and existing Carpet additions (`carpet-extra`, `carpet-tis-addition`, `carpet-ams-addition`, `carpet-igny-addition`, `gugle-carpet-addition`, `CrystalCarpetAddition`).

## Mechanics Overview
- **Random-tick driven, no cooldown**: Wet sponges do not store cooldowns or schedule ticks.
- **Seed eligibility** (all required):
  1. Block is `minecraft:wet_sponge`.
  2. Inside `minecraft:monument` structure bounds.
  3. At least **5 of 6** adjacent faces touch `minecraft:water` (source or flowing).
  4. In the `5×5×5` region centered on the seed, prismarine-family blocks (`prismarine`, `prismarine_bricks`, `dark_prismarine`, `sea_lantern`) $\ge N$.
  5. In the `5×5×5` region centered on the seed, wet sponges $< 8$.
- **Hit chance**:
  - `finalChance = (1 / denominator) * conduitMultiplier * biomeMultiplier`
  - **Conduit**: `×3.0` if an active `ConduitBlockEntity` is found within `wetSpongeConduitRadius` (queried strictly via loaded chunk `getBlockEntities()` maps).
  - **Biome**: `×1.5` in `deep_frozen_ocean`, `×1.2` in `deep_cold_ocean`, `×1.0` otherwise.
- **Target eligibility & multi-spawn**:
  - Candidates in the seed's `5×5×5` must be `minecraft:water`, inside `minecraft:monument`, and satisfy prismarine $\ge N$ and wet sponges $< 8$ in their own `5×5×5`.
  - In `1–3` mode (`50% / 30% / 20%`), each subsequent placement re-validates the `< 8` wet sponge cap and target eligibility against live block state.

## `/carpet` Rules
| Rule | Default | Description |
| --- | --- | --- |
| `wetSpongePropagation` | `false` | Master toggle (hot-toggleable in singleplayer/server) |
| `wetSpongePrismarineThreshold` | `100` | Minimum prismarine-family blocks in `5×5×5` (1–124; default `100` prevents growth in unmodified vanilla monuments) |
| `wetSpongeChanceDenominator` | `60` | Base hit probability denominator (`1 / N`) |
| `wetSpongeConduitRadius` | `32` | Query radius for active Conduit BlockEntity (`0` disables bonus) |
| `wetSpongeMaxSpawn` | `3` | `1` = fixed 1 per hit; `3` = weighted 1–3 (`50% / 30% / 20%`) |

## Build
```bash
./gradlew build
```
Output jar: `build/libs/tpw-carpet-addition-1.0.0+1.20.4.jar`.
