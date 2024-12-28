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

public class MovableSolid extends Solid {
    public static float GRAVITY = 9.81f;
    public static float TERMINAL_VELOCITY = 53f;
    public static float FALLING_TIME_INCREASE = 0.05f;
    public static float SPREAD_FACTOR = 3f;

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

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getTerminalVelocity() {
        return terminalVelocity;
    }

    @Override
    public void update(Grid grid, int x, int y) {
        if (stepped.get(0) == Main.game.getStepped()) { return; }
        super.update(grid, x, y);

        boolean canMoveBellow = this.isFalling(grid, x, y);
        boolean canMoveLeft = !canMoveBellow && this.canSwap(grid.getMaterial(x - 1, y - 1));
        boolean canMoveRight = !canMoveLeft && this.canSwap(grid.getMaterial(x + 1, y - 1));
        //System.out.println(grid.getMaterial(x + 1, y - 1).getName());
        float spreadDirection = Game.RANDOM.nextBoolean() ? -1f : 1f;
        //if (Main.DEBUG) { System.out.println("update: " + x + " " + y + " " + canMoveLeft + " " + canMoveRight); }

        fallingTime = this.isFalling(grid, x, y) ? Math.min((fallingTime + Game.TIME_PER_FRAME), Short.MAX_VALUE) : 0;
        velocity.y = (fallingTime > 0) ? (float) Math.min(velocity.y + fallingTime * GRAVITY, this.terminalVelocity) : 0;

        if (canMoveBellow) {
            if (Main.DEBUG) { System.out.println("canMoveBellow: " + canMoveBellow); }
            grid.moveMaterial(this, x, y, (int) (x + velocity.x), y - (int) velocity.y - 1, Grid.MovementOptions.ResetVelocity, Grid.MovementOptions.SpreadVelocity);
        }
        else if (canMoveLeft && ((int) velocity.x) == 0) {
            if (Main.DEBUG) { System.out.println("canMoveLeft: " + canMoveLeft); }
            grid.moveMaterial(this, x, y, x - 1, y - 1, Grid.MovementOptions.ResetVelocity);
        }
        else if (canMoveRight && ((int) velocity.x) == 0) {
            if (Main.DEBUG) { System.out.println("canMoveRight: " + canMoveRight); }
            grid.moveMaterial(this, x, y, x + 1, y - 1, Grid.MovementOptions.ResetVelocity);
        } else if (((int) velocity.x) != 0 || ((int) velocity.y) != 0) {
            if (Main.DEBUG) { System.out.println("velocity: " + velocity.x + " " + velocity.y); }
            grid.moveMaterial(this, x, y, (int) (x + velocity.x), (int) (y + velocity.y), Grid.MovementOptions.ResetVelocity);
        }

        //if (Main.DEBUG) { System.out.println("velocity: " + ((int) velocity.x) + " " + ((int) velocity.y)); }
        velocity.x *= frictionFactor;
        //Main.render();
    }

    @Override
    public boolean canSwap(Material material) {
        return (material != null) && !(material instanceof Solid);
    }

    @Override
    public boolean isFalling(Grid grid, int x, int y) {
        Material material = grid.getMaterial(x, y - 1);
        return (material instanceof Gas || material instanceof Liquid);
    }
}
