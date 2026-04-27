package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.aviatorsofcreate.gearsandjets.content.interfaces.IEngine;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

import static dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlock.MACHINE_STATE;
import static dev.aviatorsofcreate.gearsandjets.enums.MachineState.*;

public abstract class CombustionChamberBlockEntity extends JetComponentBlockEntity implements IEngine {

    private SmartFluidTankBehaviour tank;
    private int signal = 0;
    private double intakeThrust = 0;
    private double exhaustThrust = 0;
    private boolean active = false;
    int remainingTicks = 0;
    private int fuelConsumed = 0;


    public CombustionChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getSignal() {
        return this.signal;
    }

    public boolean isPowered() {
        return this.signal > 0;
    }

    public int getFuelBurned() {
        return this.fuelConsumed;
    }

    protected int updateFuel(int redstone) {
        FluidTank tank = getTank();
        int fluidConsumed = Math.min(getMaxBurnRate() * redstone / 15, tank.getFluidAmount());
        if (fluidConsumed == 0) {
            return 0;
        }
        tank.drain(fluidConsumed, IFluidHandler.FluidAction.EXECUTE);
        return fluidConsumed;
    }

    protected void calculateThrustCurveIdle(float multiplier) {
        this.exhaustThrust = multiplier * this.signal / ( 15 * 3.156925 );
        this.intakeThrust = multiplier * this.signal / 225;
    }

    protected void calculateThrustCurveRunning(float multiplier) {
        this.exhaustThrust = multiplier * Math.pow((double) this.signal / 15, 3.5);
        this.intakeThrust = multiplier * this.signal / 500;
    }

    protected void updateThrust() {
        float multiplier = getThrustMultiplier();
        switch (this.getBlockState().getValue(MACHINE_STATE)) {
            case OFF:
                this.active = false;
                break;
            case IDLING:
                this.active = true;
                calculateThrustCurveIdle(multiplier);
                break;
            case RUNNING:
                this.active = true;
                calculateThrustCurveRunning(multiplier);
                break;
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 500);
        behaviours.add(tank);
    }

    public Direction getBlockDirection() {
        return this.getBlockState().getValue(CombustionChamberBlock.FACING).getOpposite();
    }

    public double getThrustFromExhaust() {
        return this.exhaustThrust;
    }


    public double getThrustFromIntake() {
        return this.intakeThrust;
    }

    public double getTotalThrust() {
        return this.intakeThrust + this.exhaustThrust;
    }

    public boolean getActive() {
        return this.active;
    }

    @Override
    public void tick() {
        Level level = this.getLevel();
        if (level != null) {
            this.signal = level.getBestNeighborSignal(this.getBlockPos());
        }

        updateThrust();

        if (level != null && !level.isClientSide) {
            this.fuelConsumed = updateFuel(this.signal);

            BlockState state = this.getBlockState();
            boolean powered = this.signal > 0;
            boolean running = this.signal > 3;

            if (!powered) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, OFF));
            } else if (!running) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, IDLING));
            } else {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, RUNNING));
            }

            if (state.hasProperty(BasicCombustionChamberBlock.POWERED) && state.getValue(BasicCombustionChamberBlock.POWERED) != powered) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(BasicCombustionChamberBlock.POWERED, powered));
            }
        }

        super.tick();
    }

    @Override
    public int getRemainingTicks() {
        return this.remainingTicks;
    }

    @Override
    public SmartBlockEntity self() {
        return this;
    }

    @Override
    public SmartFluidTank getTank() {
        return this.tank.getPrimaryHandler();
    }
}
