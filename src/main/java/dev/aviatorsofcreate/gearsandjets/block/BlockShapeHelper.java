package dev.aviatorsofcreate.gearsandjets.block;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

final class BlockShapeHelper {
    private BlockShapeHelper() {
    }

    static Map<Direction, VoxelShape> horizontalShapes(Direction baseFacing, VoxelShape baseShape) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int quarterTurns = Math.floorMod(horizontalIndex(direction) - horizontalIndex(baseFacing), 4);
            shapes.put(direction, rotateY(baseShape, quarterTurns));
        }
        return shapes;
    }

    static VoxelShape or(VoxelShape... shapes) {
        VoxelShape result = Shapes.empty();
        for (VoxelShape shape : shapes) {
            result = Shapes.or(result, shape);
        }
        return result.optimize();
    }

    static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static int horizontalIndex(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> throw new IllegalArgumentException("Direction must be horizontal: " + direction);
        };
    }

    private static VoxelShape rotateY(VoxelShape shape, int quarterTurns) {
        VoxelShape rotated = shape;
        for (int i = 0; i < quarterTurns; i++) {
            rotated = rotateY90(rotated);
        }
        return rotated.optimize();
    }

    private static VoxelShape rotateY90(VoxelShape shape) {
        VoxelShape[] rotated = {Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                rotated[0] = Shapes.or(rotated[0], Shapes.box(1.0D - maxZ, minY, minX, 1.0D - minZ, maxY, maxX))
        );
        return rotated[0];
    }
}
