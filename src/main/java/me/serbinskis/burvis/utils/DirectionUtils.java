package me.serbinskis.burvis.utils;

import me.serbinskis.burvis.materials.Material;

public class DirectionUtils {
    public static DirectionPair getDirection(Material material) {
        Direction horizontal = (material.getVelocity().x == 0) ? Direction.NONE : (material.getVelocity().x > 0) ? Direction.RIGHT : Direction.LEFT;
        Direction vertical = (material.getVelocity().y == 0) ? Direction.NONE : (material.getVelocity().y > 0) ? Direction.UP : Direction.DOWN;
        return new DirectionPair(horizontal, vertical);
    }

    public static Material getRelative(Material material, DirectionPair direction) {
        int x = direction.horizontal().equals(Direction.NONE) ? 0 : direction.horizontal().equals(Direction.LEFT) ? -1 : 1;
        int y = direction.vertical().equals(Direction.NONE) ? 0 : direction.vertical().equals(Direction.DOWN) ? -1 : 1;
        return material.getGrid().getMaterial(material.getX() + x, material.getY() + y);
    }

    public enum Direction { UP, DOWN, LEFT, RIGHT, NONE }
    public record DirectionPair(Direction horizontal, Direction vertical) {};
}
