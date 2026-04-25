package dev.aviatorsofcreate.gearsandjets.mixin.extra_kinetics;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraBlockPos;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(RotationPropagator.class)
public abstract class RotationPropagatorMixin {
    @Redirect(method = {"handleRemoved", "propagateMissingSource", "findConnectedNeighbour"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private static BlockEntity simulated$accountForExtraKinetics(Level level, BlockPos pos) {
        return simulated$getBlockEntityAccountingExtraKinetics(level, pos);
    }

    @WrapOperation(method = "getRotationSpeedModifier", at = {
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0),
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 3)
    })
    private static boolean testSmallCogFrom1(BlockState state, Operation<Boolean> original,
                                             @Local(argsOnly = true, ordinal = 0) KineticBlockEntity fromBE) {
        return simulated$checkCogStateSmall(original.call(state), fromBE);
    }

    @WrapOperation(method = "getRotationSpeedModifier", at = {
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1),
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2)
    })
    private static boolean testSmallCogTo(BlockState state, Operation<Boolean> original,
                                          @Local(argsOnly = true, ordinal = 1) KineticBlockEntity toBE) {
        return simulated$checkCogStateSmall(original.call(state), toBE);
    }

    @WrapOperation(method = "getRotationSpeedModifier", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isLargeCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private static boolean testLargeCogFrom(BlockState state, Operation<Boolean> original,
                                            @Local(argsOnly = true, ordinal = 0) KineticBlockEntity fromBE) {
        return simulated$checkCogStateLarge(original.call(state), fromBE);
    }

    @WrapOperation(method = "getRotationSpeedModifier", at = {
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isLargeCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1),
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isLargeCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2)
    })
    private static boolean testLargeCogTo(BlockState state, Operation<Boolean> original,
                                          @Local(argsOnly = true, ordinal = 1) KineticBlockEntity toBE) {
        return simulated$checkCogStateLarge(original.call(state), toBE);
    }

    @ModifyReceiver(method = "getRotationSpeedModifier", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/IRotate;hasShaftTowards(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", ordinal = 0))
    private static IRotate changeIRotateShaftFrom(IRotate instance, LevelReader levelReader, BlockPos blockPos, BlockState state,
                                                  Direction direction, @Local(argsOnly = true, ordinal = 0) KineticBlockEntity fromBE) {
        return simulated$getNewIRotate(instance, fromBE);
    }

    @ModifyReceiver(method = "getRotationSpeedModifier", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/IRotate;hasShaftTowards(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", ordinal = 1))
    private static IRotate changeIRotateShaftTo(IRotate instance, LevelReader levelReader, BlockPos blockPos, BlockState state,
                                                Direction direction, @Local(argsOnly = true, ordinal = 1) KineticBlockEntity toBe) {
        return simulated$getNewIRotate(instance, toBe);
    }

    @ModifyReceiver(method = "getRotationSpeedModifier", at = {
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/IRotate;getRotationAxis(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction$Axis;", ordinal = 0),
            @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/IRotate;getRotationAxis(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction$Axis;", ordinal = 1)
    })
    private static IRotate changeIRotateAxisFrom(IRotate instance, BlockState state,
                                                 @Local(argsOnly = true, ordinal = 0) KineticBlockEntity fromBE) {
        return simulated$getNewIRotate(instance, fromBE);
    }

    @ModifyReceiver(method = "getRotationSpeedModifier", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/IRotate;getRotationAxis(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction$Axis;", ordinal = 2))
    private static IRotate changeIRotateAxisTo(IRotate instance, BlockState state,
                                               @Local(argsOnly = true, ordinal = 1) KineticBlockEntity toBe) {
        return simulated$getNewIRotate(instance, toBe);
    }

    @Inject(method = "getConnectedNeighbours", at = @At("TAIL"), remap = false)
    private static void simulated$addExtraKineticsBlockEntities(KineticBlockEntity be,
                                                                CallbackInfoReturnable<List<KineticBlockEntity>> cir) {
        List<KineticBlockEntity> list = cir.getReturnValue();
        if (be instanceof ExtraKinetics.ExtraKineticsBlockEntity ekbe) {
            KineticBlockEntity parent = ekbe.getParentBlockEntity();
            if (parent != null && ((ExtraKinetics) parent).shouldConnectExtraKinetics()) {
                list.add(parent);
            }
        } else if (be instanceof ExtraKinetics ek && ek.shouldConnectExtraKinetics()) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                list.add(extraKinetics);
            }
        }
    }

    @Inject(method = "getPotentialNeighbourLocations", at = @At("TAIL"), remap = false)
    private static void simulated$getExtraKineticsBlockPositions(KineticBlockEntity be,
                                                                 CallbackInfoReturnable<List<BlockPos>> cir) {
        List<BlockPos> list = cir.getReturnValue();
        List<BlockPos> extraKinetics = new ArrayList<>();
        Level level = be.getLevel();

        for (BlockPos pos : list) {
            Block block = level.getBlockState(pos).getBlock();
            if (block instanceof ExtraKinetics.ExtraKineticsBlock) {
                extraKinetics.add(new ExtraBlockPos(pos));
            }
        }

        list.addAll(extraKinetics);
    }

    @Unique
    private static @Nullable BlockEntity simulated$getBlockEntityAccountingExtraKinetics(Level level, BlockPos blockPos) {
        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics ek && blockPos instanceof ExtraBlockPos) {
            return ek.getExtraKinetics();
        }
        return be;
    }

    @Unique
    private static boolean simulated$checkCogStateSmall(boolean original, KineticBlockEntity be) {
        if (original) {
            return true;
        }
        if (be.getBlockPos() instanceof ExtraBlockPos && be.getBlockState().getBlock() instanceof ExtraKinetics.ExtraKineticsBlock ekb) {
            return ekb.getExtraKineticsRotationConfiguration() instanceof ICogWheel ic && ic.isSmallCog();
        }
        return false;
    }

    @Unique
    private static boolean simulated$checkCogStateLarge(boolean original, KineticBlockEntity be) {
        if (original) {
            return true;
        }
        if (be.getBlockPos() instanceof ExtraBlockPos && be.getBlockState().getBlock() instanceof ExtraKinetics.ExtraKineticsBlock ekb) {
            return ekb.getExtraKineticsRotationConfiguration() instanceof ICogWheel ic && ic.isLargeCog();
        }
        return false;
    }

    @Unique
    private static IRotate simulated$getNewIRotate(IRotate currentRotate, KineticBlockEntity be) {
        if (be.getBlockPos() instanceof ExtraBlockPos && be.getBlockState().getBlock() instanceof ExtraKinetics.ExtraKineticsBlock ekb) {
            return ekb.getExtraKineticsRotationConfiguration();
        }
        return currentRotate;
    }
}
