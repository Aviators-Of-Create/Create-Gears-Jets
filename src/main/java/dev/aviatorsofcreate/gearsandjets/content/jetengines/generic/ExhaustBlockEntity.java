package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlockEntity;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlock.FACING;

public abstract class ExhaustBlockEntity extends JetComponentBlockEntity implements BlockEntityPropeller, BlockEntitySubLevelPropellerActor {

    protected double thrust = 0;
    protected boolean active = false;

    public ExhaustBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Direction getBlockDirection() {
        return this.getBlockState().getValue(FACING);
    }

    @Override
    public double getAirflow() {
        return 0;
    }

    @Override
    public double getThrust() {
        return this.thrust;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public BlockEntityPropeller getPropeller() {
        return this;
    }

    protected BlockPos getAttachedCombustionChamber() {
        for (BlockPos pos : this.jetComponents) {
            if (this.getLevel() != null && this.getLevel().getBlockState(pos).getBlock() instanceof CombustionChamberBlock) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public void tick() {

        this.jetComponents = getJetComponents();
        BlockPos chamberPos = this.jetComponents != null ? getAttachedCombustionChamber() : null;

        int fuelConsumed = 0;
        if (chamberPos != null && level != null && level.getBlockState(chamberPos).getBlock() instanceof BasicCombustionChamberBlock) {
            CombustionChamberBlockEntity chamberEntity = level.getBlockEntity(chamberPos, ModBlockEntityTypes.BASIC_COMBUSTION_CHAMBER.get()).get();
            this.thrust = chamberEntity.getThrustFromExhaust();
            this.active = chamberEntity.getActive();
            fuelConsumed = chamberEntity.getFuelBurned();
        }

        if (level instanceof ServerLevel serverLevel) {
            emitExhaustParticles(serverLevel, fuelConsumed);
        }

        super.tick();
    }

    protected void emitExhaustParticles(ServerLevel level, int fuelConsumed) {
        if (fuelConsumed <= 0) {
            return;
        }

        BlockPos exhaustData = this.getBlockPos();
        Direction exhaustFacing = this.getBlockState().getValue(FACING);
        double dirX = exhaustFacing.getStepX();
        double dirZ = exhaustFacing.getStepZ();
        double baseX = exhaustData.getX() + 0.5D + dirX * 0.45D;
        double baseY = exhaustData.getY() + 0.55D;
        double baseZ = exhaustData.getZ() + 0.5D + dirZ * 0.45D;
        int segments = Math.clamp(fuelConsumed, 1, 4);
        int largeSmokeCount = Math.clamp(fuelConsumed / 2, 1, 4);
        int signalSmokeCount = Math.clamp(fuelConsumed / 5, 1, 2);
        double spreadScale = 0.05D + Math.min(fuelConsumed, 10) * 0.01D;
        double speedScale = 0.006D + Math.min(fuelConsumed, 10) * 0.0008D;

        for (int i = 0; i < Math.min(2, segments); i++) {
            double distance = i * 0.2D;
            double x = baseX + dirX * distance;
            double y = baseY + 0.02D + i * 0.01D;
            double z = baseZ + dirZ * distance;
            level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, signalSmokeCount, spreadScale * 0.3D, 0.02D, spreadScale * 0.3D, speedScale);
        }

        level.sendParticles(ParticleTypes.LARGE_SMOKE, baseX, baseY, baseZ, largeSmokeCount + 1, spreadScale + 0.02D, 0.06D, spreadScale + 0.02D, speedScale + 0.001D);
        level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, baseX, baseY, baseZ, signalSmokeCount, spreadScale * 0.4D, 0.025D, spreadScale * 0.4D, speedScale);
    }
}
