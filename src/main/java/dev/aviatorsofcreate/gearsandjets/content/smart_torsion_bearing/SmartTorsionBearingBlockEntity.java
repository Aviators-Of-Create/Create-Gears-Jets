package dev.aviatorsofcreate.gearsandjets.content.smart_torsion_bearing;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SmartTorsionBearingBlockEntity extends MechanicalBearingBlockEntity {
    private static final float EPSILON = 0.001F;

    public ScrollValueBehaviour angleInput;

    public SmartTorsionBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(this.angleInput = new SmartTorsionBearingScrollValueBehaviour(this).between(0, 180));
        this.angleInput.onlyActiveWhen(() -> true);
        this.angleInput.setValue(90);
    }

    public void onSignalChanged() {
        setChanged();
    }

    public void toggleAssembly() {
        if (this.running) {
            this.disassemble();
            return;
        }
        this.assembleNextTick = true;
    }

    public float getMaxAngle() {
        return Math.abs(this.angleInput.getValue());
    }

    public float getDisplayedAngle() {
        return normalizeAngle(this.angle);
    }

    public float getTargetAngle() {
        if (level == null) {
            return 0.0F;
        }

        int positiveSignal = getControlSignal(SmartTorsionBearingBlock.getPositiveSignalDirection(this.getBlockState()));
        int negativeSignal = getControlSignal(SmartTorsionBearingBlock.getNegativeSignalDirection(this.getBlockState()));
        float normalized = Mth.clamp((positiveSignal - negativeSignal) / 15.0F, -1.0F, 1.0F);
        return normalized * getMaxAngle();
    }

    @Override
    public float getAngularSpeed() {
        float baseSpeed = Math.abs(super.getAngularSpeed());
        if (baseSpeed < EPSILON || !running) {
            return 0.0F;
        }

        float diff = getWrappedTargetDelta();
        if (Math.abs(diff) < EPSILON) {
            this.angle = getTargetAngle();
            return 0.0F;
        }

        return Math.copySign(Math.min(baseSpeed, Math.abs(diff)), diff);
    }

    @Override
    public boolean isNearInitialAngle() {
        return Math.abs(getDisplayedAngle()) < 22.5F;
    }

    private int getControlSignal(Direction side) {
        BlockPos signalPos = this.worldPosition.relative(side);
        return Math.max(
                this.level.getSignal(signalPos, side),
                this.level.getSignal(signalPos, side.getOpposite())
        );
    }

    private float getWrappedTargetDelta() {
        return normalizeAngle(getTargetAngle() - getDisplayedAngle());
    }

    private static float normalizeAngle(float angle) {
        angle %= 360.0F;
        if (angle > 180.0F) {
            angle -= 360.0F;
        }
        if (angle <= -180.0F) {
            angle += 360.0F;
        }
        return angle;
    }

    public static class SmartTorsionBearingScrollValueBehaviour extends ScrollValueBehaviour {
        public SmartTorsionBearingScrollValueBehaviour(SmartBlockEntity be) {
            super(Component.translatable("gearsandjets.smart_torsion_bearing.angle_limit"), be, new SmartTorsionBearingValueBox());
            this.withFormatter(v -> Math.abs(v) + CreateLang.translateDirect("generic.unit.degrees").getString());
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(
                    this.label,
                    180,
                    15,
                    ImmutableList.of(Component.literal("<>").withStyle(ChatFormatting.BOLD)),
                    new ValueSettingsFormatter(this::formatValue)
            );
        }

        public MutableComponent formatValue(ValueSettings settings) {
            return Component.literal(Integer.toString(Math.abs(settings.value())))
                    .append(CreateLang.translateDirect("generic.unit.degrees"));
        }
    }

    public static class SmartTorsionBearingValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.getLocalOffset(level, pos, state)
                    .add(Vec3.atLowerCornerOf(state.getValue(BearingBlock.FACING).getNormal()).scale(-5 / 16f));
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            if (!this.getSide().getAxis().isHorizontal()) {
                TransformStack.of(ms)
                        .rotateY((AngleHelper.horizontalAngle(state.getValue(BearingBlock.FACING)) + 180) * Mth.DEG_TO_RAD);
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
            return direction.getAxis() != state.getValue(BearingBlock.FACING).getAxis();
        }
    }
}
