package dev.aviatorsofcreate.gearsandjets.mixin.extra_kinetics;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.aviatorsofcreate.gearsandjets.mixin_interface.extra_kinetics.KineticBlockEntityExtension;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraBlockPos;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KineticBlockEntity.class)
public abstract class KineticBlockEntityMixin extends SmartBlockEntity implements KineticBlockEntityExtension {
    @Shadow
    public abstract boolean hasSource();

    @Shadow
    private int validationCountdown;

    @Unique
    private boolean simulated$extraKineticsConnected = false;

    public KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void simulated$setConnectedToExtraKinetics(boolean connectedToExtraKinetics) {
        this.simulated$extraKineticsConnected = connectedToExtraKinetics;
    }

    @Override
    public boolean simulated$getConnectedToExtraKinetics() {
        return this.simulated$extraKineticsConnected;
    }

    @Inject(method = "switchToBlockState", at = @At("TAIL"))
    private static void switchExtraKinetics(Level world, BlockPos pos, BlockState state, CallbackInfo ci,
                                            @Local BlockEntity be) {
        if (be instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null && extraKinetics.hasNetwork()) {
                extraKinetics.getOrCreateNetwork().remove(extraKinetics);
                extraKinetics.detachKinetics();
                extraKinetics.removeSource();
                if (extraKinetics instanceof GeneratingKineticBlockEntity gbe) {
                    gbe.reActivateSource = true;
                }
            }
        }
    }

    @Redirect(method = "validateKinetics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    public BlockEntity simulated$useProperSource(Level instance, BlockPos blockPos) {
        BlockEntity be = instance.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics ek && this.simulated$extraKineticsConnected) {
            be = ek.getExtraKinetics();
        }
        return be;
    }

    @Redirect(method = "setSource", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    public BlockEntity simulated$useProperSource2(Level instance, BlockPos blockPos) {
        BlockEntity be = instance.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics ek && blockPos instanceof ExtraBlockPos) {
            this.simulated$extraKineticsConnected = true;
            be = ek.getExtraKinetics();
        }
        return be;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (this instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                extraKinetics.setLevel(level);
            }
        }
    }

    @Override
    public void setBlockState(BlockState blockState) {
        super.setBlockState(blockState);
        if (this instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                extraKinetics.setBlockState(blockState);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                extraKinetics.invalidate();
            }
        }
    }

    @Inject(method = "remove", at = @At("TAIL"), remap = false)
    public void injectRemove(CallbackInfo ci) {
        if (this instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                extraKinetics.remove();
            }
        }
    }

    @Inject(method = "removeSource", at = @At("TAIL"), remap = false)
    public void simulated$removeConnected(CallbackInfo ci) {
        this.simulated$extraKineticsConnected = false;
    }

    @Inject(method = "write", at = @At("TAIL"), remap = false)
    public void simulated$saveConnected(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (this instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                CompoundTag internalTag = new CompoundTag();
                if (clientPacket) {
                    extraKinetics.writeClient(internalTag, registries);
                } else {
                    extraKinetics.saveAdditional(internalTag, registries);
                }
                compound.put(ek.getExtraKineticsSaveName(), internalTag);
            }
        }

        if (this.hasSource()) {
            compound.putBoolean("ConnectedToExtraKinetics", this.simulated$extraKineticsConnected);
        }
    }

    @Inject(method = "read", at = @At("TAIL"), remap = false)
    public void simulated$readConnected(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (this instanceof ExtraKinetics ek) {
            KineticBlockEntity extraKinetics = ek.getExtraKinetics();
            if (extraKinetics != null) {
                CompoundTag extraKineticsTag = compound.getCompound(ek.getExtraKineticsSaveName());
                if (clientPacket) {
                    extraKinetics.readClient(extraKineticsTag, registries);
                } else {
                    extraKinetics.loadCustomOnly(extraKineticsTag, registries);
                }
            }
        }

        if (compound.contains("ConnectedToExtraKinetics")) {
            this.simulated$extraKineticsConnected = compound.getBoolean("ConnectedToExtraKinetics");
        }
    }

    @Override
    public void simulated$setValidationCountdown(int validationCountdown) {
        this.validationCountdown = validationCountdown;
    }
}
