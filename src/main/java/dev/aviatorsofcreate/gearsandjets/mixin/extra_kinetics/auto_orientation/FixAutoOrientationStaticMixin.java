package dev.aviatorsofcreate.gearsandjets.mixin.extra_kinetics.auto_orientation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {HorizontalAxisKineticBlock.class, RotatedPillarKineticBlock.class})
public class FixAutoOrientationStaticMixin {
    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/IRotate;hasShaftTowards(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", remap = false))
    private static boolean test(IRotate instance, LevelReader world, BlockPos pos, BlockState state, Direction incomingDir,
                                Operation<Boolean> originalWrap) {
        boolean original = originalWrap.call(instance, world, pos, state, incomingDir);
        if (!original) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ExtraKinetics ek) {
                KineticBlockEntity extraKinetics = ek.getExtraKinetics();
                if (extraKinetics != null && extraKinetics.getBlockState().getBlock() instanceof ExtraKinetics.ExtraKineticsBlock ekb) {
                    original = ekb.getExtraKineticsRotationConfiguration().hasShaftTowards(world, pos, state, incomingDir);
                }
            }
        }
        return original;
    }
}
