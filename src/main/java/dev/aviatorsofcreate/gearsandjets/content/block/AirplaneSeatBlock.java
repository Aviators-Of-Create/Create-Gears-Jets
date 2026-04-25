package dev.aviatorsofcreate.gearsandjets.content.block;

import java.util.List;

import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;

public class AirplaneSeatBlock extends SeatBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final double SEAT_HEIGHT_OFFSET = 4.0 / 16.0;

    public AirplaneSeatBlock(Properties properties) {
        super(properties, DyeColor.GRAY);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        return state.setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter reader, Entity entity) {
        BlockPos pos = entity.blockPosition();
        if (entity instanceof Player || !(entity instanceof LivingEntity) || !canBePickedUp(entity)
                || isSeatOccupied(entity.level(), pos)) {
            if (entity.isSuppressingBounce()) {
                super.updateEntityAfterFallOn(reader, entity);
                return;
            }

            Vec3 vec3 = entity.getDeltaMovement();
            if (vec3.y < 0.0D) {
                double scale = entity instanceof LivingEntity ? 1.0D : 0.8D;
                entity.setDeltaMovement(vec3.x, -vec3.y * 0.66D * scale, vec3.z);
            }

            return;
        }
        if (reader.getBlockState(pos).getBlock() != this) {
            return;
        }
        sitDownHigher(entity.level(), pos, entity);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || player instanceof FakePlayer) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
        if (!seats.isEmpty()) {
            SeatEntity seatEntity = seats.getFirst();
            List<Entity> passengers = seatEntity.getPassengers();
            if (!passengers.isEmpty() && passengers.getFirst() instanceof Player) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide) {
                seatEntity.ejectPassengers();
                player.startRiding(seatEntity);
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        sitDownHigher(level, pos, getLeashed(level, player).or(player));
        return ItemInteractionResult.SUCCESS;
    }

    private static void sitDownHigher(Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        SeatEntity seat = new SeatEntity(level);
        seat.setPos(pos.getX() + 0.5D, pos.getY() + SEAT_HEIGHT_OFFSET, pos.getZ() + 0.5D);
        level.addFreshEntity(seat);
        entity.startRiding(seat, true);
        if (entity instanceof net.minecraft.world.entity.TamableAnimal tamableAnimal) {
            tamableAnimal.setInSittingPose(true);
        }
    }
}
