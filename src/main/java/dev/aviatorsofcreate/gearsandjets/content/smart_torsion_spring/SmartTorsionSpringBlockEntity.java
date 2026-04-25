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

    public double getTargetAngle() {
        if (level == null) {
            return 0.0D;
        }

        int positiveSignal = getControlSignal(SmartTorsionSpringBlock.getPositiveSignalDirection(this.getBlockState()));
        int negativeSignal = getControlSignal(SmartTorsionSpringBlock.getNegativeSignalDirection(this.getBlockState()));
        double normalized = (positiveSignal - negativeSignal) / 15.0D;
        return getMaxAngle() * Mth.clamp(normalized, -1.0D, 1.0D);
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
        private float lastInputSpeed = 0;
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

            this.generatedSpeed = this.queuedSpeed;
            super.tick();

            this.oldAngle = this.angle;
            if (this.rotationDurationTicks >= 0 && this.rotationProgressTicks <= this.rotationDurationTicks) {
                this.rotationProgressTicks++;

                float angularSpeed = KineticBlockEntity.convertToAngular(this.speed);
                if (this.parent.sequencedAngleLimit >= 0) {
                    angularSpeed = (float) Mth.clamp(angularSpeed, -this.parent.sequencedAngleLimit, this.parent.sequencedAngleLimit);
                }

                if (this.sequencedAngleLimit >= 0) {
                    this.sequencedAngleLimit = Math.max(0, this.sequencedAngleLimit - Math.abs(angularSpeed));
                }

                this.angle += angularSpeed;

                if (this.rotationProgressTicks == this.rotationDurationTicks) {
                    // Clamp to exact target to prevent overshoot-induced oscillation
                    this.angle = this.targetAngle;

                    this.sequenceContext = null;
                    this.rotationProgressTicks = -1;
                    this.rotationDurationTicks = -1;
                    this.queuedSpeed = 0;

                    this.reActivateSource = true;
                    this.updateSpeed = true;
                    this.currentState = State.STOPPED;
                }
            }

            boolean parentStopped = Math.abs(this.parent.getSpeed()) < EPSILON;
            double desiredTarget = this.parent.getTargetAngle();

            if (this.currentState == State.TURNING && parentStopped) {
                if (Math.abs(this.targetAngle) > EPSILON || Math.abs(desiredTarget) > EPSILON) {
                    this.stopTurning();
                }
            } else if (this.currentState == State.STOPPED && parentStopped) {
                if (Math.abs(desiredTarget) < EPSILON && Math.abs(this.angle) > EPSILON) {
                    this.beginTurnTo(0.0D);
                }
            } else if (this.currentState == State.TURNING) {
                if (Math.abs(this.targetAngle - desiredTarget) > EPSILON
                        || Math.abs(this.lastSpringSpeed - this.generatedSpeed) > EPSILON) {
                    this.stopTurning();
                }
            } else if (!parentStopped && this.currentState == State.STOPPED) {
                // Only start turning if we're not already at the desired target
                if (Math.abs(this.angle - desiredTarget) > EPSILON) {
                    this.beginTurnTo(desiredTarget);
                }
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
                this.lastInputSpeed = newParentSpeed;
            } else if (previousSpeed != 0) {
                this.lastInputSpeed = previousSpeed;
            }
        }

        private void onTargetChanged() {
            if (this.level == null || this.level.isClientSide) {
                return;
            }

            this.setChanged();
            if (this.currentState == State.TURNING) {
                this.stopTurning();
            }
        }

        private void stopTurning() {
            this.sequenceContext = null;
            this.rotationProgressTicks = -1;
            this.rotationDurationTicks = -1;
            this.sequencedAngleLimit = -1;
            this.targetAngle = Double.MAX_VALUE;

            this.reActivateSource = true;
            this.updateSpeed = true;
            this.queuedSpeed = 0;
            this.generatedSpeed = 0;

            this.currentState = State.STOPPED;
        }

        private void beginTurnTo(double targetAngle) {
            double relativeAngle = targetAngle - this.angle;

            if (Math.abs(relativeAngle) < EPSILON) {
                return;
            }

            if (this.currentState == State.TURNING && Math.abs(this.targetAngle - targetAngle) < EPSILON) {
                return;
            }

            float availableInputSpeed = Math.abs(this.lastInputSpeed) >= EPSILON ? this.lastInputSpeed : this.parent.getSpeed();

            if (Math.abs(availableInputSpeed) < EPSILON) {
                availableInputSpeed = this.generatedSpeed;
            }

            if (Math.abs(availableInputSpeed) < EPSILON) {
                return;
            }

            this.lastSpringSpeed = (float) (Math.abs(availableInputSpeed) * Math.signum(relativeAngle));

            if (this.parent.sequencedAngleLimit >= 0) {
                relativeAngle = Mth.clamp(relativeAngle, -this.parent.sequencedAngleLimit, this.parent.sequencedAngleLimit);
            }

            this.detachKinetics();
            this.targetAngle = targetAngle;
            this.sequenceContext = new SequencedGearshiftBlockEntity.SequenceContext(
                    SequencerInstructions.TURN_ANGLE,
                    relativeAngle / this.lastSpringSpeed
            );

            double degreesPerTick = KineticBlockEntity.convertToAngular(Math.abs(this.lastSpringSpeed));
            this.rotationDurationTicks = (int) Math.ceil(Math.abs(relativeAngle) / degreesPerTick);
            this.rotationProgressTicks = 0;
            this.sequencedAngleLimit = this.sequenceContext.getEffectiveValue(this.lastSpringSpeed);
            this.currentState = State.TURNING;
            this.queuedSpeed = this.lastSpringSpeed;
            this.generatedSpeed = this.queuedSpeed;
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
            compound.putFloat("LastInputSpeed", this.lastInputSpeed);
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
            this.lastInputSpeed = compound.getFloat("LastInputSpeed");
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
