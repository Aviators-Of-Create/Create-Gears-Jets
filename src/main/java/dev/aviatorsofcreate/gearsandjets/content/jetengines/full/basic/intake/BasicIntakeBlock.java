package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake;

import dev.aviatorsofcreate.gearsandjets.content.BlockShapeHelper;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.IntakeBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.IntakeBlockEntity;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BasicIntakeBlock extends IntakeBlock {

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicIntakeBlockEntity(ModBlockEntityTypes.BASIC_INTAKE.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntityTypes.BASIC_INTAKE.get()) {
            return null;
        }

        return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
            if (blockEntity instanceof BasicIntakeBlockEntity component) {
                component.tick();
            }
        };
    }

    private static final Map<Direction, VoxelShape> SHAPES = BlockShapeHelper.horizontalShapes(
            Direction.SOUTH,
            BlockShapeHelper.or(
                    BlockShapeHelper.box(1.25D, 1.0D, 0.0D, 14.75D, 2.0D, 6.0D),
                    BlockShapeHelper.box(1.25D, 14.0D, 0.0D, 14.75D, 15.0D, 6.0D),
                    BlockShapeHelper.box(14.0D, 1.25D, 0.0D, 15.0D, 14.75D, 6.0D),
                    BlockShapeHelper.box(1.0D, 1.25D, 0.0D, 2.0D, 14.75D, 6.0D)
            )
    );

    public BasicIntakeBlock(Properties properties, SableBlockWeight sableBlockWeight) {
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
