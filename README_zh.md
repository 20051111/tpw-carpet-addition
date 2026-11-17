# TPW Carpet Addition

[English](README.md) | [简体中文](README_zh.md)

面向 Minecraft **1.20.4** 的 Fabric + Carpet Extension 模组，为原版（Vanilla）Minecraft 添加实用的功能，不新增任何方块、物品或实体。

> **注意：所有配置默认关闭**，未开启前保持原版表现。请通过 `/carpet` 按需启用（使用 `/carpet setDefault <规则名> <值>` 可写入存档配置实现持久化）。

---

## 运行环境

- **Minecraft**：`1.20.4`
- **Fabric Loader**：`>= 0.15.0`
- **Fabric API**：`1.20.4`
- **Fabric Carpet**：`>= 1.4.128`

---

## `/carpet` 配置条目

内置中英文规则说明（`en_us` 与 `zh_cn`）。

| 规则名 | 默认值 | 可选范围 / 预设 | 说明 |
| --- | --- | --- | --- |
| `renewableSpongeInMonument` | `false` | `true` / `false` | 海底神殿（`minecraft:monument`）内湿海绵可再生增殖总开关 |
| `renewableSpongePrismarineThreshold` | `100` | `1–124`（预设 `80, 96, 100, 105, 110`） | 种源与目标各自 `5×5×5` 范围内所需的最少海晶类方块数（海晶石、海晶石砖、暗海晶石、海晶灯） |
| `renewableSpongeChanceDenominator` | `60` | `>= 1`（预设 `30, 50, 60, 80, 100`） | 每次随机刻的基础命中率分母（`1 / 分母`） |
| `renewableSpongeConduitRadius` | `32` | `0–128`（预设 `16, 32, 48, 64`） | 激活潮涌核心方块实体的查询半径（触发 `×3.0` 概率加成，`0` 关闭加成） |
| `renewableSpongeMaxSpawn` | `3` | `1` 或 `3`（严格限制） | 单次命中最大生成数：`1` = 固定 1 个；`3` = 按 `50% / 30% / 20%` 随机生成 1–3 个 |

### 常用指令
```mcfunction
/carpet renewableSpongeInMonument true
/carpet setDefault renewableSpongeInMonument true
```

调整增殖节奏与海晶门槛示例：
```mcfunction
/carpet renewableSpongePrismarineThreshold 100
/carpet renewableSpongeChanceDenominator 60
/carpet renewableSpongeConduitRadius 32
/carpet renewableSpongeMaxSpawn 3
```

---

## 从源码构建

需要 **JDK 17+**（已在 OpenJDK 21 上实测通过）。

```bash
./gradlew build
```

构建重映射后的成品 jar 位于 `build/libs/tpw-carpet-addition-<version>.jar`。
