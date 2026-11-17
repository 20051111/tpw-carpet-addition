package com.tpw.carpetaddition.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ensures WetSpongeBlock reports random-tick eligibility at BlockState cache initialization
 * so hot-toggling works seamlessly alongside Lithium's BlockStateFlags cache.
 */
@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
    private void tpwCarpetAddition$alwaysTickWetSponge(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof WetSpongeBlock) {
            cir.setReturnValue(true);
        }
    }
}
