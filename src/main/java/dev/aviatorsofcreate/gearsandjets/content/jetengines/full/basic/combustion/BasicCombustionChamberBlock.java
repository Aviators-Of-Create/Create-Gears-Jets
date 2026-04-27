package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion;

import dev.aviatorsofcreate.gearsandjets.content.BlockShapeHelper;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlockEntity;
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

public class BasicCombustionChamberBlock extends CombustionChamberBlock {

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicCombustionChamberBlockEntity(ModBlockEntityTypes.BASIC_COMBUSTION_CHAMBER.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntityTypes.BASIC_COMBUSTION_CHAMBER.get()) {
            return null;
        }

        return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
            if (blockEntity instanceof BasicCombustionChamberBlockEntity component) {
                component.tick();
            }
        };
    }

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

    private final SableBlockWeight sableBlockWeight;

    public BasicCombustionChamberBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
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
