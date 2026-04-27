package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.special.exhaust.afterburning;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dev.aviatorsofcreate.gearsandjets.content.interfaces.IEngine;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlockEntity;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class AfterburningExhaustBlockEntity extends ExhaustBlockEntity implements IEngine {

    private SmartFluidTankBehaviour tank;
    private int signal = 0;
    private float afterburnerMultiplier = 0;

    public AfterburningExhaustBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 500);
        behaviours.add(tank);
    }

    @Override
    public double getThrust() {
        return super.getThrust() * this.afterburnerMultiplier;
    }

    @Override
    public int getRemainingTicks() {
        return 0;
    }

    @Override
    public SmartBlockEntity self() {
        return this;
    }

    @Override
    public FluidTank getTank() {
        return this.tank.getPrimaryHandler();
    }

    @Override
    public void tick() {
        Level level = this.getLevel();
        if (level != null) {
            this.signal = level.getBestNeighborSignal(this.getBlockPos());
        }

        this.jetComponents = getJetComponents();
        BlockPos chamberPos = this.jetComponents != null ? getAttachedCombustionChamber() : null;

        int fuelConsumed = 0;
        if (chamberPos != null && level != null && level.getBlockState(chamberPos).getBlock() instanceof BasicCombustionChamberBlock) {
            CombustionChamberBlockEntity chamberEntity = level.getBlockEntity(chamberPos, ModBlockEntityTypes.BASIC_COMBUSTION_CHAMBER.get()).get();
            this.thrust = chamberEntity.getThrustFromExhaust() * this.afterburnerMultiplier * this.signal / 15;
            this.active = chamberEntity.getActive();
            fuelConsumed = chamberEntity.getFuelBurned();
        }

        if (level instanceof ServerLevel serverLevel) {
            emitExhaustParticles(serverLevel, fuelConsumed);
        }
        super.tick();
    }
}
