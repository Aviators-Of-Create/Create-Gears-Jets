package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import dev.aviatorsofcreate.gearsandjets.content.jetengines.JetComponentBlock;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.LevelReader;

public abstract class IntakeBlock extends JetComponentBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final SableBlockWeight sableBlockWeight;

    protected IntakeBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = findAttachmentFacing(context);
        if (facing == null) {
            return null;
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    private static @Nullable Direction findAttachmentFacing(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal() && isValidCombustionChamber(context.getLevel(), pos, clickedFace)) {
            return clickedFace;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction != clickedFace && isValidCombustionChamber(context.getLevel(), pos, direction)) {
                return direction;
            }
        }

        return null;
    }

    private static boolean isValidCombustionChamber(LevelReader level, BlockPos intakePos, Direction facing) {
        BlockPos chamberPos = intakePos.relative(facing.getOpposite());
        BlockState chamberState = level.getBlockState(chamberPos);
        return chamberState.getBlock() instanceof BasicCombustionChamberBlock
                && chamberState.hasProperty(BasicCombustionChamberBlock.FACING)
                && chamberState.getValue(BasicCombustionChamberBlock.FACING) == facing;
    }
}
