package dev.aviatorsofcreate.gearsandjets.content.smart_torsion_spring;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.mixin_interface.extra_kinetics.KineticBlockEntityExtension;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SmartTorsionSpringBlockEntity extends KineticBlockEntity implements ExtraKinetics {
    private static final double EPSILON = 0.001D;

    private final Output springOutput;
    public ScrollValueBehaviour angleInput;
    protected double sequencedAngleLimit;

    public SmartTorsionSpringBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.springOutput = new Output(blockEntityType, new ExtraBlockPos(blockPos), blockState, this);
        this.sequencedAngleLimit = -1;
    }

    public boolean isSpringStatic() {
        return this.springOutput.angle == this.springOutput.oldAngle;
    }

    public float interpolatedSpring(float pt) {
        return (float) (this.springOutput.oldAngle + (this.springOutput.angle - this.springOutput.oldAngle) * pt);
    }

    public float getAngle() {
        return (float) this.springOutput.angle;
    }

    public float getMaxAngle() {
        return Math.abs(this.angleInput.getValue());
    }

    public void onSignalChanged() {
        this.springOutput.onTargetChanged();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(this.angleInput = new SmartTorsionSpringScrollValueBehaviour(this).between(0, 360));
        this.angleInput.onlyActiveWhen(() -> true);
        this.angleInput.setValue(90);
    }

    @Override
    public void tick() {
        super.tick();
        this.springOutput.tick();
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        this.sequencedAngleLimit = -1;

        if (this.sequenceContext != null && this.sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE) {
            this.sequencedAngleLimit = this.sequenceContext.getEffectiveValue(this.getTheoreticalSpeed());
        }

        this.springOutput.updateParentSpeed(previousSpeed, this.getSpeed());
    }

    public double getTargetAngle(float driveSpeed) {
        if (level == null) {
            return 0.0D;
        }

        if (Math.abs(driveSpeed) < EPSILON) {
            return 0.0D;
        }

        if (driveSpeed > 0) {
            int positiveSignal = getControlSignal(SmartTorsionSpringBlock.getPositiveSignalDirection(this.getBlockState()));
            return getMaxAngle() * Mth.clamp(positiveSignal / 15.0D, 0.0D, 1.0D);
        }

        int negativeSignal = getControlSignal(SmartTorsionSpringBlock.getNegativeSignalDirection(this.getBlockState()));
        return -getMaxAngle() * Mth.clamp(negativeSignal / 15.0D, 0.0D, 1.0D);
    }

    private int getControlSignal(Direction side) {
        BlockPos signalPos = this.worldPosition.relative(side);
        return Math.max(
                this.level.getSignal(signalPos, side),
                this.level.getSignal(signalPos, side.getOpposite())
        );
    }

    @Override
    public float calculateAddedStressCapacity() {
        return 0;
    }

    @Override
    public String getExtraKineticsSaveName() {
        return "SmartTorsionSpringOutput";
    }

    @Override
    public KineticBlockEntity getExtraKinetics() {
        return this.springOutput;
    }

    @Override
    public boolean shouldConnectExtraKinetics() {
        return false;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (this.sequencedAngleLimit >= 0) {
            compound.putDouble("SequencedAngleLimit", this.sequencedAngleLimit);
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.sequencedAngleLimit = compound.contains("SequencedAngleLimit") ? compound.getDouble("SequencedAngleLimit") : -1;
    }

    public static class Output extends GeneratingKineticBlockEntity implements ExtraKinetics.ExtraKineticsBlockEntity {
        public static final IRotate CONFIG = new IRotate() {
            @Override
            public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
                return face == state.getValue(SmartTorsionSpringBlock.FACING);
            }

            @Override
            public Direction.Axis getRotationAxis(BlockState state) {
                return state.getValue(SmartTorsionSpringBlock.FACING).getAxis();
            }
        };

        private final SmartTorsionSpringBlockEntity parent;

        protected double oldAngle = 0.0F;
        protected double angle = 0.0F;

        private int rotationDurationTicks = 0;
        private int rotationProgressTicks = 0;
        private double sequencedAngleLimit = -1;

        private float lastSpringSpeed = 0;
        private float generatedSpeed;
        private double targetAngle = 0;
        private State currentState = State.STOPPED;
        private float queuedSpeed;
        private int customValidationCountdown;

        public Output(BlockEntityType<?> type, ExtraBlockPos pos, BlockState state, SmartTorsionSpringBlockEntity parentBlockEntity) {
            super(type, pos, state);
            this.parent = parentBlockEntity;
        }

        @Override
        public void initialize() {
            super.initialize();
            this.reActivateSource = true;
            this.updateSpeed = true;
        }

        @Override
        public void tick() {
            ((KineticBlockEntityExtension) this).simulated$setValidationCountdown(Integer.MAX_VALUE);

            if (this.customValidationCountdown-- <= 0) {
                this.customValidationCountdown = AllConfigs.server().kinetics.kineticValidationFrequency.get();
                this.customValidateKinetics();
            }

            boolean parentDriven = Math.abs(this.parent.getSpeed()) >= EPSILON;
            if (parentDriven) {
                this.lastSpringSpeed = this.parent.getSpeed();
            }

            float driveSpeed = parentDriven ? this.parent.getSpeed() : this.lastSpringSpeed;
            float availableSpeed = Math.abs(driveSpeed);
            double activeTarget = parentDriven ? this.parent.getTargetAngle(driveSpeed) : 0.0D;
            double angleError = activeTarget - this.angle;
            float desiredGeneratedSpeed = 0.0F;
            double settleWindow = EPSILON;

            if (availableSpeed >= EPSILON) {
                double fullStep = KineticBlockEntity.convertToAngular(availableSpeed);
                if (this.parent.sequencedAngleLimit >= 0) {
                    fullStep = Math.min(fullStep, this.parent.sequencedAngleLimit);
                }

                settleWindow = Math.max(fullStep * 0.35D, 0.25D);
                if (parentDriven) {
                    if (Math.signum(angleError) == Math.signum(driveSpeed) && Math.abs(angleError) > settleWindow) {
                        double slowDownWindow = Math.max(fullStep * 4.0D, 1.0D);
                        float scaledSpeed = (float) (availableSpeed * Mth.clamp(Math.abs(angleError) / slowDownWindow, 0.0D, 1.0D));
                        float minimumSpeed = Math.min(availableSpeed, 4.0F);
                        desiredGeneratedSpeed = Math.copySign(Math.max(minimumSpeed, scaledSpeed), driveSpeed);
                    }
                } else if (Math.abs(this.angle) > settleWindow) {
                    double slowDownWindow = Math.max(fullStep * 4.0D, 1.0D);
                    float scaledSpeed = (float) (availableSpeed * Mth.clamp(Math.abs(this.angle) / slowDownWindow, 0.0D, 1.0D));
                    float minimumSpeed = Math.min(availableSpeed, 4.0F);
                    desiredGeneratedSpeed = -Math.copySign(Math.max(minimumSpeed, scaledSpeed), (float) this.angle);
                }
            }

            this.setGeneratedSpeed(desiredGeneratedSpeed, activeTarget);
            super.tick();

            this.oldAngle = this.angle;
            float appliedSpeed = Math.abs(this.getTheoreticalSpeed()) >= EPSILON ? this.getTheoreticalSpeed() : desiredGeneratedSpeed;
            if (Math.abs(appliedSpeed) >= EPSILON) {
                double step = KineticBlockEntity.convertToAngular(Math.abs(appliedSpeed));
                if (this.parent.sequencedAngleLimit >= 0) {
                    step = Math.min(step, this.parent.sequencedAngleLimit);
                }

                double remaining = Math.abs(activeTarget - this.angle);
                double applied = Math.min(remaining, step);
                this.angle += Math.signum(appliedSpeed) * applied;
            }

            if (Math.abs(activeTarget - this.angle) <= settleWindow) {
                this.angle = activeTarget;
            }
        }

        private void customValidateKinetics() {
            if (this.hasSource()) {
                if (!this.hasNetwork()) {
                    this.removeSource();
                    return;
                }

                if (!this.level.isLoaded(this.source)) {
                    return;
                }

                BlockEntity blockEntity = this.level.getBlockEntity(this.source);
                if (blockEntity instanceof ExtraKinetics ek && ((KineticBlockEntityExtension) this).simulated$getConnectedToExtraKinetics()) {
                    blockEntity = ek.getExtraKinetics();
                }

                KineticBlockEntity sourceBE = blockEntity instanceof KineticBlockEntity kinetic ? kinetic : null;
                if (sourceBE == null || sourceBE.getTheoreticalSpeed() == 0) {
                    this.removeSource();
                    this.detachKinetics();
                }
            }
        }

        private void updateParentSpeed(float previousSpeed, float newParentSpeed) {
            if (newParentSpeed != 0) {
                this.lastSpringSpeed = newParentSpeed;
            } else if (previousSpeed != 0) {
                this.lastSpringSpeed = previousSpeed;
            }
        }

        private void onTargetChanged() {
            if (this.level == null || this.level.isClientSide) {
                return;
            }

            this.reActivateSource = true;
            this.updateSpeed = true;
            this.setChanged();
        }

        private void setGeneratedSpeed(float desiredSpeed, double activeTarget) {
            desiredSpeed = Math.round(desiredSpeed * 2.0F) / 2.0F;
            boolean sameSpeed = Math.abs(this.queuedSpeed - desiredSpeed) < 0.25F;
            this.targetAngle = activeTarget;
            this.currentState = Math.abs(desiredSpeed) < EPSILON ? State.STOPPED : State.TURNING;
            this.generatedSpeed = desiredSpeed;
            this.queuedSpeed = desiredSpeed;

            if (sameSpeed) {
                return;
            }

            this.sequenceContext = null;
            this.rotationProgressTicks = -1;
            this.rotationDurationTicks = -1;
            this.sequencedAngleLimit = -1;
            this.reActivateSource = true;
            this.updateSpeed = true;
        }

        @Override
        public float getGeneratedSpeed() {
            return this.generatedSpeed;
        }

        @Override
        public float calculateStressApplied() {
            return 0;
        }

        @Override
        protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
            super.write(compound, registries, clientPacket);
            compound.putDouble("OldAngle", this.oldAngle);
            compound.putDouble("Angle", this.angle);
            compound.putDouble("TargetAngle", this.targetAngle);
            compound.putFloat("LastSpringSpeed", this.lastSpringSpeed);
            compound.putInt("CurrentState", this.currentState.ordinal());
            compound.putInt("RotationProgressTicks", this.rotationProgressTicks);
            compound.putInt("RotationDurationTicks", this.rotationDurationTicks);
            compound.putFloat("GeneratedSpeed", this.generatedSpeed);
            compound.putFloat("QueuedSpeed", this.queuedSpeed);

            if (this.sequencedAngleLimit >= 0) {
                compound.putDouble("SequencedAngleLimit", this.sequencedAngleLimit);
            }
        }

        @Override
        protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
            super.read(compound, registries, clientPacket);
            this.oldAngle = compound.getDouble("OldAngle");
            this.angle = compound.getDouble("Angle");
            this.targetAngle = compound.getDouble("TargetAngle");
            this.lastSpringSpeed = compound.getFloat("LastSpringSpeed");
            this.sequencedAngleLimit = compound.contains("SequencedAngleLimit") ? compound.getDouble("SequencedAngleLimit") : -1;
            this.rotationProgressTicks = compound.getInt("RotationProgressTicks");
            this.rotationDurationTicks = compound.getInt("RotationDurationTicks");
            this.generatedSpeed = compound.getFloat("GeneratedSpeed");
            this.queuedSpeed = compound.getFloat("QueuedSpeed");

            if (compound.contains("CurrentState")) {
                this.currentState = State.values()[compound.getInt("CurrentState")];
            }
        }

        @Override
        public KineticBlockEntity getParentBlockEntity() {
            return this.parent;
        }

        private enum State {
            STOPPED,
            TURNING
        }
    }

    public static class SmartTorsionSpringScrollValueBehaviour extends ScrollValueBehaviour {
        public SmartTorsionSpringScrollValueBehaviour(SmartBlockEntity be) {
            super(Component.translatable("gearsandjets.smart_torsion_spring.angle_limit"), be, new SmartTorsionSpringValueBox());
            this.withFormatter(v -> Math.abs(v) + CreateLang.translateDirect("generic.unit.degrees").getString());
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(
                    this.label,
                    360,
                    45,
                    ImmutableList.of(Component.literal("<>").withStyle(ChatFormatting.BOLD)),
                    new ValueSettingsFormatter(this::formatValue)
            );
        }

        public MutableComponent formatValue(ValueSettings settings) {
            return Component.literal(Integer.toString(Math.abs(settings.value())))
                    .append(CreateLang.translateDirect("generic.unit.degrees"));
        }
    }

    public static class SmartTorsionSpringValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.getLocalOffset(level, pos, state)
                    .add(Vec3.atLowerCornerOf(state.getValue(SmartTorsionSpringBlock.FACING).getNormal()).scale(-5 / 16f));
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            if (!this.getSide().getAxis().isHorizontal()) {
                TransformStack.of(ms)
                        .rotateY((AngleHelper.horizontalAngle(state.getValue(SmartTorsionSpringBlock.FACING)) + 180) * Mth.DEG_TO_RAD);
            }
            super.rotate(level, pos, state, ms);
        }

        @Override
        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            return offset != null && localHit.distanceTo(offset) < this.scale / 1.5f;
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() != state.getValue(SmartTorsionSpringBlock.FACING).getAxis();
        }
    }
}
