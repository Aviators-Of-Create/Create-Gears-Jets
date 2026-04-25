package dev.aviatorsofcreate.gearsandjets.content.block;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SimpleExhaustBlock extends ExhaustBlock {
    private static final Map<Direction, VoxelShape> SHAPES = BlockShapeHelper.horizontalShapes(
            Direction.SOUTH,
            BlockShapeHelper.or(
                    BlockShapeHelper.box(4.0D, 4.0D, 2.0D, 12.0D, 12.0D, 5.0D),
                    BlockShapeHelper.box(3.0D, 3.0D, 11.0D, 13.0D, 13.0D, 14.0D),
                    BlockShapeHelper.box(4.0D, 3.0D, 0.0D, 12.0D, 4.0D, 5.0D),
                    BlockShapeHelper.box(4.0D, 12.0D, 0.0D, 12.0D, 13.0D, 5.0D),
                    BlockShapeHelper.box(3.0D, 3.0D, 0.0D, 4.0D, 13.0D, 5.0D),
                    BlockShapeHelper.box(12.0D, 3.0D, 0.0D, 13.0D, 13.0D, 5.0D),
                    BlockShapeHelper.box(2.5D, 3.0D, 4.5D, 3.75D, 13.5D, 16.0D),
                    BlockShapeHelper.box(12.25D, 3.0D, 4.5D, 13.5D, 13.5D, 16.0D),
                    BlockShapeHelper.box(3.0D, 2.75D, 4.5D, 13.0D, 3.75D, 16.0D),
                    BlockShapeHelper.box(3.0D, 12.25D, 4.5D, 13.0D, 13.5D, 16.0D)
            )
    );

    public SimpleExhaustBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
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
