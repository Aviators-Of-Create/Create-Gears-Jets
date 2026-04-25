package dev.aviatorsofcreate.gearsandjets.content.block.smart_torsion_spring;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import dev.aviatorsofcreate.gearsandjets.content.block.BlockShapeHelper;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class SmartTorsionSpringBlock extends DirectionalKineticBlock
        implements IBE<SmartTorsionSpringBlockEntity>, ExtraKinetics.ExtraKineticsBlock {
    private static final VoxelShape BASE_SHAPE = BlockShapeHelper.or(
            BlockShapeHelper.box(0, 0, 0, 16, 6, 16),
            BlockShapeHelper.box(2, 0, 2, 14, 16, 14)
    );
    private static final Map<Direction, VoxelShape> SHAPES = BlockShapeHelper.directionalShapesUp(BASE_SHAPE);

    public SmartTorsionSpringBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getOpposite() == state.getValue(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES.get(blockState.getValue(FACING));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        SmartTorsionSpringBlockEntity be = this.getBlockEntity(level, blockPos);
        if (be == null) {
            return 0;
        }

        float maxAngle = be.getMaxAngle();
        if (maxAngle <= 0.0F) {
            return 0;
        }

        float fraction = Mth.clamp(be.getAngle() / maxAngle, -1.0F, 1.0F);
        if (Math.abs(be.getAngle()) < 0.99F) {
            return 0;
        }

        return Mth.clamp(Math.round(Math.abs(fraction) * 15.0F), 0, 15);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    public static Direction getPositiveSignalDirection(BlockState state) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis().isVertical()) {
            return Direction.EAST;
        }
        return facing.getClockWise();
    }

    public static Direction getNegativeSignalDirection(BlockState state) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis().isVertical()) {
            return Direction.WEST;
        }
        return facing.getCounterClockWise();
    }

    @Override
    public Class<SmartTorsionSpringBlockEntity> getBlockEntityClass() {
        return SmartTorsionSpringBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmartTorsionSpringBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SMART_TORSION_SPRING.get();
    }

    @Override
    public IRotate getExtraKineticsRotationConfiguration() {
        return SmartTorsionSpringBlockEntity.Output.CONFIG;
    }
}
