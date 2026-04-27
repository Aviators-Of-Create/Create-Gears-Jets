package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

public abstract class ExhaustBlock extends JetComponentBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final SableBlockWeight sableBlockWeight;

    protected ExhaustBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canConnectExhaustTo(level, pos, state.getValue(FACING));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
                                     BlockPos currentPos, BlockPos facingPos) {
        return facing == state.getValue(FACING).getOpposite() && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    private @Nullable Direction findAttachmentFacing(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal() && canConnectExhaustTo(context.getLevel(), pos, clickedFace)) {
            return clickedFace;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction != clickedFace && canConnectExhaustTo(context.getLevel(), pos, direction)) {
                return direction;
            }
        }

        return null;
    }

    private boolean canConnectExhaustTo(LevelReader level, BlockPos exhaustPos, Direction facing) {
        BlockState nextBlock = level.getBlockState(exhaustPos.relative(facing.getOpposite()));

        // TODO: Edit if statement once turbines are added
        if (nextBlock.getBlock() instanceof CombustionChamberBlock) {
            return (nextBlock.getValue(FACING) == facing.getOpposite());
        }

        return false;
    }
}
