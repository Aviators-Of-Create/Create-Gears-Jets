package dev.aviatorsofcreate.gearsandjets.mixin.extra_kinetics;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KineticBlock.class)
public class KineticBlockMixin {
    @Inject(method = "updateIndirectNeighbourShapes", at = @At("TAIL"))
    public void extraKineticsReset(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count, CallbackInfo ci,
                                   @Local BlockEntity be) {
        if (be instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                extraKinetics.warnOfMovement();
                extraKinetics.clearKineticInformation();
                extraKinetics.updateSpeed = true;
            }
        }
    }
}
