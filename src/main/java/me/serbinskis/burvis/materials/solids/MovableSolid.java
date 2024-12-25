package me.serbinskis.burvis.materials.solids;

import com.badlogic.gdx.math.Vector2;
import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.core.Game;
import me.serbinskis.burvis.core.Grid;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;
import me.serbinskis.burvis.materials.gasses.Gas;
import me.serbinskis.burvis.materials.liquids.Liquid;
import me.serbinskis.burvis.utils.PhysicsUtils;

import java.awt.*;

public class MovableSolid extends Material {
    public static float GRAVITY = 9.81f;
    public static float TERMINAL_VELOCITY = 53f;
    public static float FALLING_TIME_INCREASE = 0.05f;
    public static float SPREAD_FACTOR = 50f;

    private final float frictionFactor;
    private final float inertialResistance;
    private final float terminalVelocity;
    private boolean isFreeFalling;
    private double fallingTime = 0;
    private Vector2 velocity;

    public MovableSolid(String name, Color color, float frictionFactor, float inertialResistance, float density) {
        super(name, color, density);
        this.frictionFactor = frictionFactor;
        this.terminalVelocity = PhysicsUtils.calculateTerminalVelocity(density, GRAVITY);
        this.inertialResistance = inertialResistance;
        this.isFreeFalling = false;
        this.velocity = new Vector2(0, 0);
}

    @Override
    public void update(Grid grid, int x, int y, Material material) {
        if (stepped.get(0) == Main.game.getStepped()) { return; }
        super.update(grid, x, y, material);

        fallingTime = this.isFalling(grid, x, y) ? Math.min((fallingTime + Game.TIME_PER_FRAME), Short.MAX_VALUE) : 0;
        velocity.x += (fallingTime == 0) ? Math.clamp(-this.terminalVelocity, Math.abs(velocity.y/2f) * (Game.RANDOM.nextBoolean() ? -1f : 1f), this.terminalVelocity) : 0;
        velocity.y = (fallingTime > 0) ? (float) Math.min(velocity.y + fallingTime * GRAVITY, this.terminalVelocity) : 0;

        if (grid.getMaterial(x, y - 1) == MaterialRegistry.AIR) {
            grid.moveMaterial(x, y, (int) (x + velocity.x), y - (int) velocity.y - 1);
        }
        else if (grid.getMaterial(x + 1, y - 1) == MaterialRegistry.AIR && grid.getMaterial(x + 1, y) == MaterialRegistry.AIR) {
            grid.moveMaterial(x, y, (int) (x + velocity.x) + 1, y - 1);
        }
        else if (grid.getMaterial(x - 1, y - 1) == MaterialRegistry.AIR && grid.getMaterial(x - 1, y) == MaterialRegistry.AIR) {
            grid.moveMaterial(x, y, (int) (x + velocity.x) - 1, y - 1);
        } else {
            //grid.moveMaterial(x, y, (int) (x + velocity.x), (int) (y + velocity.y));
        }

        velocity.x *= frictionFactor;
    }

    @Override
    public boolean canSwap(Material material) {
        return !(material instanceof ImmovableSolid);
    }

    @Override
    public boolean isFalling(Grid grid, int x, int y) {
        Material material = grid.getMaterial(x, y - 1);
        return (material instanceof Gas || material instanceof Liquid);
    }
}
