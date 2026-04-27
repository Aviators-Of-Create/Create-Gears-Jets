package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class JetComponentBlock extends Block implements IWrenchable, EntityBlock {

    private final SableBlockWeight sableBlockWeight;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    ArrayList<BlockPos> jetComponents = new ArrayList<>();


    protected JetComponentBlock(Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties);
        this.sableBlockWeight = sableBlockWeight;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (hasAttachedComponents(context.getLevel(), context.getClickedPos(), state.getValue(FACING))) {
            return InteractionResult.FAIL;
        }

        return IWrenchable.super.onWrenched(state, context);
    }

    private void collectAllBlocks(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack tool = context.getItemInHand();

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (BlockPos blockPos : getJetEngineComponents(level, pos, state.getValue(FACING))) {
            collectThisBlock(level, blockPos, player, serverLevel, tool);
        }

    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {

        collectAllBlocks(state, context);

        return InteractionResult.SUCCESS;
    }

    protected static boolean hasAttachedComponents(Level level, BlockPos pos, Direction direction) {
        Block facing = level.getBlockState(pos.relative(direction)).getBlock();
        Block facingAway = level.getBlockState(pos.relative(direction.getOpposite())).getBlock();
        return (facingAway instanceof JetComponentBlock || facing instanceof JetComponentBlock);
    }

    private static ArrayList<BlockPos> getComponentsRecursive(Level level, BlockPos pos, Direction direction, BlockPos thisBlock) {

        BlockPos nextComponent = pos.relative(direction);
        ArrayList<BlockPos> attachedAfter = new ArrayList<>();
        attachedAfter.add(thisBlock);

        if (level.getBlockState(nextComponent).getBlock() instanceof JetComponentBlock) {
           attachedAfter.addAll(getComponentsRecursive(level, pos.relative(direction), direction, nextComponent));
        }

        return attachedAfter;
    }

    protected static ArrayList<BlockPos> getJetEngineComponents(Level level, BlockPos pos, Direction direction) {
        BlockPos front = pos.relative(direction);
        BlockPos back = pos.relative(direction.getOpposite());
        ArrayList<BlockPos> attachedBoth = new ArrayList<>();

        attachedBoth.add(pos);

        if (level.getBlockState(back).getBlock() instanceof JetComponentBlock) {
            attachedBoth.addAll(getComponentsRecursive(level, pos.relative(direction.getOpposite()), direction.getOpposite(), back));
        }

        if (level.getBlockState(front).getBlock() instanceof JetComponentBlock) {
            attachedBoth.addAll(getComponentsRecursive(level, pos.relative(direction), direction, front));
        }

        return (attachedBoth);
    }

    private static void collectThisBlock(Level level, BlockPos blockPos, @Nullable Player player, ServerLevel serverLevel, ItemStack tool) {
        BlockState blockState = level.getBlockState(blockPos);

        if (player != null && !player.isCreative()) {
            Block.getDrops(blockState, serverLevel, blockPos, level.getBlockEntity(blockPos), player, tool)
                    .forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }

        blockState.spawnAfterBreak(serverLevel, blockPos, ItemStack.EMPTY, true);
        level.destroyBlock(blockPos, false);
        IWrenchable.playRemoveSound(level, blockPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
