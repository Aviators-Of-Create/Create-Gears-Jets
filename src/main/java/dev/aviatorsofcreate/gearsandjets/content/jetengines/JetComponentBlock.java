package dev.aviatorsofcreate.gearsandjets.content.jetengines;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class JetComponentBlock extends Block implements EntityBlock, IWrenchable {

    private final SableBlockWeight sableBlockWeight;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;


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

    private static void collectAllBlocks(Level level, BlockPos thisPos, @Nullable Player player, ItemStack tool, ServerLevel serverLevel) {
        for (BlockPos blockPos : getJetEngineComponents(level, thisPos, level.getBlockState(thisPos).getValue(FACING))) {
            collectThisBlock(level, blockPos, player, tool, serverLevel);
        }
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, level.getBlockState(pos), player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return InteractionResult.SUCCESS;
        }

        collectAllBlocks(level, pos, player, player.getMainHandItem(), serverLevel);

        if (!player.isCreative()) {
            Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos), player, context.getItemInHand())
                    .forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }

        return InteractionResult.SUCCESS;
    }

    protected static boolean hasAttachedComponents(Level level, BlockPos pos, Direction direction) {
        Block facing = level.getBlockState(pos.relative(direction)).getBlock();
        Block facingAway = level.getBlockState(pos.relative(direction.getOpposite())).getBlock();
        return (facingAway instanceof JetComponentBlock || facing instanceof JetComponentBlock);
    }

    private static ArrayList<BlockPos> getAttachedComponentsRecursive(Level level, BlockPos pos, Direction direction, BlockPos thisBlock) {

        BlockPos nextComponent = pos.relative(direction);
        ArrayList<BlockPos> attachedAfter = new ArrayList<>();
        attachedAfter.add(thisBlock);

        if (level.getBlockState(nextComponent).getBlock() instanceof JetComponentBlock) {
           attachedAfter.addAll(getAttachedComponentsRecursive(level, pos.relative(direction), direction, nextComponent));
        }

        return attachedAfter;
    }

    protected static ArrayList<BlockPos> getJetEngineComponents(Level level, BlockPos pos, Direction direction) {
        BlockPos front = pos.relative(direction);
        BlockPos back = pos.relative(direction.getOpposite());
        ArrayList<BlockPos> attachedBack, attachedFront, attachedBoth = new ArrayList<>();

        attachedBoth.add(pos);

        if (level.getBlockState(back).getBlock() instanceof JetComponentBlock) {
            attachedBack = getAttachedComponentsRecursive(level, pos.relative(direction.getOpposite()), direction.getOpposite(), back);
        } else attachedBack = null;
        attachedBoth.addAll(attachedBack);

        if (level.getBlockState(front).getBlock() instanceof JetComponentBlock) {
            attachedFront = getAttachedComponentsRecursive(level, pos.relative(direction), direction, front);
        } else attachedFront = null;
        attachedBoth.addAll(attachedFront);
        return (attachedBoth);
    }

    private static void collectThisBlock(Level level, BlockPos blockPos, @Nullable Player player, ItemStack tool, ServerLevel serverLevel) {
        BlockState blockState = level.getBlockState(blockPos);

        if (player != null && !player.isCreative()) {
            Block.getDrops(blockState, serverLevel, blockPos, level.getBlockEntity(blockPos), player, tool)
                    .forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }

        blockState.spawnAfterBreak(serverLevel, blockPos, ItemStack.EMPTY, true);
        level.destroyBlock(blockPos, false);
        IWrenchable.playRemoveSound(level, blockPos);
    }
}
