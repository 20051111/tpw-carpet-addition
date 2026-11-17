package com.tpw.carpetaddition.mixin;

import com.tpw.carpetaddition.WetSpongePropagation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks randomTick for WetSpongeBlock; returns immediately when the Carpet rule is off.
 */
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @Inject(method = "randomTick", at = @At("HEAD"))
    private void tpwCarpetAddition$onRandomTick(
        BlockState state,
        ServerLevel level,
        BlockPos pos,
        RandomSource random,
        CallbackInfo ci
    ) {
        if ((Object) this instanceof WetSpongeBlock) {
            WetSpongePropagation.onRandomTick(state, level, pos, random);
        }
    }
}
