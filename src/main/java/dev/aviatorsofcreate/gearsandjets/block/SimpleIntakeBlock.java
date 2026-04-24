package dev.aviatorsofcreate.gearsandjets.block;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SimpleIntakeBlock extends IntakeBlock {
    private static final Map<Direction, VoxelShape> SHAPES = BlockShapeHelper.horizontalShapes(
            Direction.SOUTH,
            BlockShapeHelper.or(
                    BlockShapeHelper.box(1.25D, 1.0D, 0.0D, 14.75D, 2.0D, 6.0D),
                    BlockShapeHelper.box(1.25D, 14.0D, 0.0D, 14.75D, 15.0D, 6.0D),
                    BlockShapeHelper.box(14.0D, 1.25D, 0.0D, 15.0D, 14.75D, 6.0D),
                    BlockShapeHelper.box(1.0D, 1.25D, 0.0D, 2.0D, 14.75D, 6.0D)
            )
    );

    public SimpleIntakeBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }
}
