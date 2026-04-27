package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust;

import dev.aviatorsofcreate.gearsandjets.content.BlockShapeHelper;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlockEntity;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BasicExhaustBlock extends ExhaustBlock {

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicExhaustBlockEntity(ModBlockEntityTypes.BASIC_EXHAUST.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntityTypes.BASIC_EXHAUST.get()) {
            return null;
        }

        return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
            if (blockEntity instanceof BasicExhaustBlockEntity component) {
                component.tick();
            }
        };
    }

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

    private final SableBlockWeight sableBlockWeight;

    public BasicExhaustBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
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
