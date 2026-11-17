package com.tpw.carpetaddition;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Core wet sponge propagation logic triggered from WetSpongeBlock random ticks.
 */
public final class WetSpongePropagation {
    public static final int LOCAL_WET_SPONGE_CAP = 8;
    private static final int MIN_ADJACENT_WATER_FACES = 5;

    private WetSpongePropagation() {
    }

    public static void onRandomTick(BlockState state, ServerLevel level, BlockPos seedPos, RandomSource random) {
        if (!TpwCarpetSettings.wetSpongePropagation) {
            return;
        }
        if (!state.is(Blocks.WET_SPONGE)) {
            return;
        }
        if (countAdjacentWaterFaces(level, seedPos) < MIN_ADJACENT_WATER_FACES) {
            return;
        }

        Structure monument = getMonumentStructure(level);
        if (monument == null || !isInsideMonument(level, seedPos, monument)) {
            return;
        }

        int threshold = TpwCarpetSettings.wetSpongePrismarineThreshold;
        long seedCounts = countAreaBlocks(level, seedPos);
        if (unpackPrismarine(seedCounts) < threshold || unpackWetSponges(seedCounts) >= LOCAL_WET_SPONGE_CAP) {
            return;
        }

        double chance = computeFinalChance(level, seedPos);
        if (chance <= 0.0 || (chance < 1.0 && random.nextDouble() >= chance)) {
            return;
        }

        List<BlockPos> candidates = collectCandidates(level, seedPos, monument, threshold);
        if (candidates.isEmpty()) {
            return;
        }

        int spawnBudget = TpwCarpetSettings.wetSpongeMaxSpawn <= 1 ? 1 : rollOneToThree(random);
        Util.shuffle(candidates, random);

        int spawned = 0;
        for (BlockPos targetPos : candidates) {
            if (spawned >= spawnBudget) {
                break;
            }
            if (spawned > 0) {
                // Re-check both seed and target neighborhoods against live block state after prior placements.
                if (unpackWetSponges(countAreaBlocks(level, seedPos)) >= LOCAL_WET_SPONGE_CAP) {
                    break;
                }
                if (!isTargetEligible(level, targetPos, monument, threshold)) {
                    continue;
                }
            }
            placeWetSponge(level, targetPos, random);
            spawned++;
        }
    }

    public static int countAdjacentWaterFaces(ServerLevel level, BlockPos pos) {
        int waterFaces = 0;
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            neighbor.setWithOffset(pos, dir);
            if (level.isInWorldBounds(neighbor) && level.isLoaded(neighbor) && level.getBlockState(neighbor).is(Blocks.WATER)) {
                waterFaces++;
            }
        }
        return waterFaces;
    }

    public static Structure getMonumentStructure(ServerLevel level) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return registry.get(BuiltinStructures.OCEAN_MONUMENT);
    }

    public static boolean isInsideMonument(ServerLevel level, BlockPos pos, Structure monument) {
        return level.structureManager().getStructureAt(pos, monument).isValid();
    }

    public static boolean isPrismarineFamily(BlockState state) {
        return state.is(Blocks.PRISMARINE)
            || state.is(Blocks.PRISMARINE_BRICKS)
            || state.is(Blocks.DARK_PRISMARINE)
            || state.is(Blocks.SEA_LANTERN);
    }

    /**
     * Scans the 5x5x5 cube centered on {@code center} and packs (prismarine, wetSponge) into a long.
     */
    public static long countAreaBlocks(ServerLevel level, BlockPos center) {
        int prismarine = 0;
        int wetSponges = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int y = cy + dy;
                if (y < minY || y >= maxY) {
                    continue;
                }
                for (int dz = -2; dz <= 2; dz++) {
                    cursor.set(cx + dx, y, cz + dz);
                    if (!level.isLoaded(cursor)) {
                        continue;
                    }
                    BlockState bs = level.getBlockState(cursor);
                    if (isPrismarineFamily(bs)) {
                        prismarine++;
                    } else if (bs.is(Blocks.WET_SPONGE)) {
                        wetSponges++;
                    }
                }
            }
        }
        return ((long) prismarine << 32) | (wetSponges & 0xFFFFFFFFL);
    }

    public static int unpackPrismarine(long packed) {
        return (int) (packed >>> 32);
    }

    public static int unpackWetSponges(long packed) {
        return (int) packed;
    }

    public static double computeFinalChance(ServerLevel level, BlockPos seedPos) {
        int denominator = Math.max(1, TpwCarpetSettings.wetSpongeChanceDenominator);
        double baseChance = 1.0 / denominator;
        double biomeMult = getBiomeMultiplier(level, seedPos);
        double conduitMult = hasActiveConduitNearby(level, seedPos, TpwCarpetSettings.wetSpongeConduitRadius) ? 3.0 : 1.0;
        return baseChance * biomeMult * conduitMult;
    }

    public static double getBiomeMultiplier(ServerLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        if (biome.is(Biomes.DEEP_FROZEN_OCEAN)) {
            return 1.5;
        }
        if (biome.is(Biomes.DEEP_COLD_OCEAN)) {
            return 1.2;
        }
        return 1.0;
    }

    /**
     * Queries only loaded chunk BlockEntity maps within radius, avoiding any block-by-block scanning.
     */
    public static boolean hasActiveConduitNearby(ServerLevel level, BlockPos seedPos, int radius) {
        if (radius <= 0) {
            return false;
        }
        int minChunkX = SectionPos.blockToSectionCoord(seedPos.getX() - radius);
        int maxChunkX = SectionPos.blockToSectionCoord(seedPos.getX() + radius);
        int minChunkZ = SectionPos.blockToSectionCoord(seedPos.getZ() - radius);
        int maxChunkZ = SectionPos.blockToSectionCoord(seedPos.getZ() + radius);
        double maxDistSqr = (double) radius * (double) radius;
        ServerChunkCache chunkSource = level.getChunkSource();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                LevelChunk chunk = chunkSource.getChunkNow(cx, cz);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof ConduitBlockEntity conduit && conduit.isActive()) {
                        if (be.getBlockPos().distSqr(seedPos) <= maxDistSqr) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static List<BlockPos> collectCandidates(ServerLevel level, BlockPos seedPos, Structure monument, int threshold) {
        List<BlockPos> candidates = new ArrayList<>();
        int cx = seedPos.getX();
        int cy = seedPos.getY();
        int cz = seedPos.getZ();
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int y = cy + dy;
                if (y < minY || y >= maxY) {
                    continue;
                }
                for (int dz = -2; dz <= 2; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(cx + dx, y, cz + dz);
                    if (isTargetEligible(level, pos, monument, threshold)) {
                        candidates.add(pos);
                    }
                }
            }
        }
        return candidates;
    }

    public static boolean isTargetEligible(ServerLevel level, BlockPos targetPos, Structure monument, int threshold) {
        if (!level.isInWorldBounds(targetPos) || !level.isLoaded(targetPos)) {
            return false;
        }
        if (!level.getBlockState(targetPos).is(Blocks.WATER)) {
            return false;
        }
        if (!isInsideMonument(level, targetPos, monument)) {
            return false;
        }
        long counts = countAreaBlocks(level, targetPos);
        return unpackPrismarine(counts) >= threshold && unpackWetSponges(counts) < LOCAL_WET_SPONGE_CAP;
    }

    private static int rollOneToThree(RandomSource random) {
        int roll = random.nextInt(10);
        if (roll < 5) {
            return 1;
        }
        return roll < 8 ? 2 : 3;
    }

    private static void placeWetSponge(ServerLevel level, BlockPos targetPos, RandomSource random) {
        level.setBlock(targetPos, Blocks.WET_SPONGE.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(
            (Player) null,
            targetPos,
            SoundEvents.WET_SPONGE_PLACE,
            SoundSource.BLOCKS,
            0.85F,
            0.9F + random.nextFloat() * 0.2F
        );
        double px = targetPos.getX() + 0.5;
        double py = targetPos.getY() + 0.5;
        double pz = targetPos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.BUBBLE, px, py, pz, 8, 0.35, 0.35, 0.35, 0.04);
        level.sendParticles(ParticleTypes.UNDERWATER, px, py, pz, 6, 0.35, 0.35, 0.35, 0.02);
    }
}
