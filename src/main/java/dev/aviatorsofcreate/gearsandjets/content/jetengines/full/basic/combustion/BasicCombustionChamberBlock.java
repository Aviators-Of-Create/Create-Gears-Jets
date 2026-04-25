package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.JetComponentBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake.IntakeBlock;
import dev.aviatorsofcreate.gearsandjets.enums.MachineState;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class BasicCombustionChamberBlock extends CombustionChamberBlock implements EntityBlock, IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<Boolean> POWERED = BlockStateProperties.POWERED;
    private final SableBlockWeight sableBlockWeight;

    public static final EnumProperty<MachineState> MACHINE_STATE =
            EnumProperty.create("machine_state", MachineState.class);

    protected BasicCombustionChamberBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(MACHINE_STATE, MachineState.OFF)
        );
    }


    public SableBlockWeight getSableBlockWeight() {
        return sableBlockWeight;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicCombustionChamberBlockEntity(ModBlockEntityTypes.COMBUSTION_CHAMBER.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntityTypes.COMBUSTION_CHAMBER.get()) {
            return null;
        }

        return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
            if (blockEntity instanceof BasicCombustionChamberBlockEntity chamber) {
                chamber.tick();
            }
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite());
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

        collectAttachedBlock(level, pos.relative(state.getValue(FACING)), player, context.getItemInHand(), serverLevel);
        collectAttachedBlock(level, pos.relative(state.getValue(FACING).getOpposite()), player, context.getItemInHand(), serverLevel);

        if (player != null && !player.isCreative()) {
            Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos), player, context.getItemInHand())
                    .forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }

        state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
        level.destroyBlock(pos, false);
        IWrenchable.playRemoveSound(level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            destroyAttachedBlockWithoutDrops(level, pos.relative(state.getValue(FACING)));
            destroyAttachedBlockWithoutDrops(level, pos.relative(state.getValue(FACING).getOpposite()));
        }

        return super.playerWillDestroy(level, pos, state, player);
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
        builder.add(FACING, POWERED, MACHINE_STATE);
    }

    private static boolean hasAttachedIntakeOrExhaust(Level level, BlockPos pos, Direction facing) {
        return isIntakeOrExhaust(level.getBlockState(pos.relative(facing)))
                || isIntakeOrExhaust(level.getBlockState(pos.relative(facing.getOpposite())));
    }

    private static boolean isIntakeOrExhaust(BlockState state) {
        Block block = state.getBlock();
        return block instanceof IntakeBlock || block instanceof ExhaustBlock;
    }

    private static void collectAttachedBlock(Level level, BlockPos attachedPos, @Nullable Player player, ItemStack tool, ServerLevel serverLevel) {
        BlockState attachedState = level.getBlockState(attachedPos);
        Block attachedBlock = attachedState.getBlock();
        if (!(attachedBlock instanceof IntakeBlock) && !(attachedBlock instanceof ExhaustBlock)) {
            return;
        }

        if (player != null && !player.isCreative()) {
            Block.getDrops(attachedState, serverLevel, attachedPos, level.getBlockEntity(attachedPos), player, tool)
                    .forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }

        attachedState.spawnAfterBreak(serverLevel, attachedPos, ItemStack.EMPTY, true);
        level.destroyBlock(attachedPos, false);
    }

    private static void destroyAttachedBlockWithoutDrops(Level level, BlockPos attachedPos) {
        BlockState attachedState = level.getBlockState(attachedPos);
        Block attachedBlock = attachedState.getBlock();
        if (!(attachedBlock instanceof IntakeBlock) && !(attachedBlock instanceof ExhaustBlock)) {
            return;
        }

        level.destroyBlock(attachedPos, false);
    }
}
