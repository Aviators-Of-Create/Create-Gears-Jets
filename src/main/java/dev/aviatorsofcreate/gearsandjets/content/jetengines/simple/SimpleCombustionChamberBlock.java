package dev.aviatorsofcreate.gearsandjets.content.jetengines.simple;

import dev.aviatorsofcreate.gearsandjets.Config;
import java.util.Map;

import dev.aviatorsofcreate.gearsandjets.content.BlockShapeHelper;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SimpleCombustionChamberBlock extends BasicCombustionChamberBlock {
    private static final Map<Direction, VoxelShape> SHAPES = BlockShapeHelper.horizontalShapes(
            Direction.NORTH,
            BlockShapeHelper.or(
                    BlockShapeHelper.box(14.0D, 2.0D, 0.0D, 15.0D, 14.0D, 4.0D),
                    BlockShapeHelper.box(2.0D, 14.0D, 0.0D, 14.0D, 15.0D, 4.0D),
                    BlockShapeHelper.box(2.0D, 1.0D, 0.0D, 14.0D, 2.0D, 4.0D),
                    BlockShapeHelper.box(1.0D, 2.0D, 0.0D, 2.0D, 14.0D, 4.0D),
                    BlockShapeHelper.box(2.0D, 13.0D, 4.0D, 14.0D, 14.0D, 16.0D),
                    BlockShapeHelper.box(13.0D, 3.0D, 4.0D, 14.0D, 13.0D, 16.0D),
                    BlockShapeHelper.box(2.0D, 2.0D, 4.0D, 14.0D, 3.0D, 16.0D),
                    BlockShapeHelper.box(2.0D, 3.0D, 4.0D, 3.0D, 13.0D, 16.0D),
                    BlockShapeHelper.box(2.25D, 2.25D, 2.0D, 13.75D, 13.75D, 15.25D),
                    BlockShapeHelper.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 9.0D),
                    BlockShapeHelper.box(3.0D, 0.0D, 2.0D, 6.0D, 2.0D, 9.0D),
                    BlockShapeHelper.box(10.0D, 0.0D, 2.0D, 13.0D, 2.0D, 9.0D),
                    BlockShapeHelper.box(1.0D, 1.0D, 4.0D, 4.0D, 6.0D, 15.0D),
                    BlockShapeHelper.box(12.0D, 1.0D, 4.0D, 15.0D, 6.0D, 15.0D)
            )
    );

    public SimpleCombustionChamberBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
    }

    @Override
    public int getTankCapacity() {
        return Config.SIMPLE_COMBUSTION_CHAMBER_CAPACITY.getAsInt();
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
