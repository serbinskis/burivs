package me.serbinskis.burvis.materials.solids;

import com.badlogic.gdx.math.Vector2;
import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.core.Grid;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;

import java.awt.*;

public class MovableSolid extends Material {
    public static float GRAVITY = 10f;
    public static float FALLING_TIME_INCREASE = 0.05f;

    private final float frictionFactor;
    private final float inertialResistance;
    private boolean isFreeFalling;
    private float fallingTime = 0;
    private Vector2 velocity;
    private Vector2 acceleration;

    public MovableSolid(String name, Color color, float frictionFactor, float inertialResistance, float density) {
        super(name, color, density);
        this.frictionFactor = frictionFactor;
        this.inertialResistance = inertialResistance;
        this.isFreeFalling = false;
        this.velocity = new Vector2(0, 0);
        this.acceleration = new Vector2(0, 0);
}

    @Override
    public void update(Grid grid, int x, int y, Material material) {
        if (stepped.get(0) == Main.game.getStepped()) { return; }
        super.update(grid, x, y, material);

        isFreeFalling = grid.getMaterial(x, y - 1) == MaterialRegistry.EMPTY;
        fallingTime = isFreeFalling ? Math.clamp(((fallingTime + FALLING_TIME_INCREASE) * 1.1f), 0f, 1f) : 0;
        velocity.y = Math.clamp(velocity.y + (GRAVITY * fallingTime), -GRAVITY, GRAVITY);

        if (grid.getMaterial(x, y - 1) == MaterialRegistry.EMPTY) {
            grid.moveMaterial(x,y,  x, y - (int) velocity.y);
        }
        else if (grid.getMaterial(x + 1, y - 1) == MaterialRegistry.EMPTY && grid.getMaterial(x + 1, y) == MaterialRegistry.EMPTY) {
            grid.moveMaterial(x, y, x + 1, y - 1);
        }
        else if (grid.getMaterial(x - 1, y - 1) == MaterialRegistry.EMPTY && grid.getMaterial(x - 1, y) == MaterialRegistry.EMPTY) {
            grid.moveMaterial(x, y, x - 1, y - 1);
        }
    }

    @Override
    public boolean canSwap(Material material) {
        return material == MaterialRegistry.EMPTY;
    }
}
