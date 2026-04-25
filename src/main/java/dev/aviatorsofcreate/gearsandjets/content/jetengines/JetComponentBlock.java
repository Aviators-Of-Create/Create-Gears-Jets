package dev.aviatorsofcreate.gearsandjets.content.jetengines;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake.IntakeBlock;
import dev.aviatorsofcreate.gearsandjets.enums.MachineState;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class JetComponentBlock extends Block implements EntityBlock, IWrenchable {

    private final SableBlockWeight sableBlockWeight;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final EnumProperty<MachineState> MACHINE_STATE =
            EnumProperty.create("machine_state", MachineState.class);

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
        for (JetComponentBlock block : getAttachedComponentsBothSides(level, thisPos, level.getBlockState(thisPos).getValue(FACING))) {

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

        if (player != null && !player.isCreative()) {
            Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos), player, context.getItemInHand())
                    .forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }

        state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
        level.destroyBlock(pos, false);
        IWrenchable.playRemoveSound(level, pos);
        return InteractionResult.SUCCESS;
    }

    protected static boolean hasAttachedComponents(Level level, BlockPos pos, Direction direction) {
        Block facing = level.getBlockState(pos.relative(direction)).getBlock();
        Block facingAway = level.getBlockState(pos.relative(direction.getOpposite())).getBlock();
        return (facingAway instanceof JetComponentBlock || facing instanceof JetComponentBlock);
    }

    private static ArrayList<JetComponentBlock> getAttachedComponentsRecursive(Level level, BlockPos pos, Direction direction, JetComponentBlock thisBlock) {

        Block nextComponent = level.getBlockState(pos.relative(direction)).getBlock();
        ArrayList<JetComponentBlock> attachedAfter = new ArrayList<>();
        attachedAfter.add(thisBlock);

        if (nextComponent instanceof JetComponentBlock block) {
           attachedAfter.addAll(getAttachedComponentsRecursive(level, pos.relative(direction), direction, block));
        }

        return attachedAfter;
    }

    protected static ArrayList<JetComponentBlock> getAttachedComponentsBothSides(Level level, BlockPos pos, Direction direction) {
        Block front = level.getBlockState(pos.relative(direction)).getBlock();
        Block back = level.getBlockState(pos.relative(direction.getOpposite())).getBlock();
        ArrayList<JetComponentBlock> attachedBack, attachedFront, attachedBoth = new ArrayList<>();

        if (back instanceof JetComponentBlock block) {
            attachedBack = getAttachedComponentsRecursive(level, pos.relative(direction.getOpposite()), direction.getOpposite(), block);
        } else attachedBack = null;
        attachedBoth.addAll(attachedBack);

        if (front instanceof JetComponentBlock block) {
            attachedFront = getAttachedComponentsRecursive(level, pos.relative(direction), direction, block);
        } else attachedFront = null;
        attachedBoth.addAll(attachedFront);
        return (attachedBoth);
    }
}
