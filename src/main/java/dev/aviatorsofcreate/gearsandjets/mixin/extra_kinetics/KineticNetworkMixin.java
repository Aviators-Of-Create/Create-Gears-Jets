package dev.aviatorsofcreate.gearsandjets.mixin.extra_kinetics;

import com.simibubi.create.content.kinetics.KineticNetwork;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraBlockPos;
import dev.aviatorsofcreate.gearsandjets.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KineticNetwork.class)
public class KineticNetworkMixin {
    @Redirect(method = "calculateCapacity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    public BlockEntity simulated$extraKineticsCapacity(Level instance, BlockPos blockPos) {
        BlockEntity be = instance.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics ek && blockPos instanceof ExtraBlockPos) {
            return ek.getExtraKinetics();
        }
        return be;
    }

    @Redirect(method = "calculateStress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    public BlockEntity simulated$extraKineticsStress(Level instance, BlockPos blockPos) {
        BlockEntity be = instance.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics ek && blockPos instanceof ExtraBlockPos) {
            return ek.getExtraKinetics();
        }
        return be;
    }
}
