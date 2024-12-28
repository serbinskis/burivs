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
import me.serbinskis.burvis.utils.Utils;

import java.awt.*;

public class MovableSolid extends Solid {
    private final float frictionFactor;
    private final float inertialResistance;
    private final float terminalVelocity;
    private double fallingTime = 0;

    public MovableSolid(String name, Color color, float frictionFactor, float inertialResistance, float density) {
        super(name, color, density);
        this.frictionFactor = frictionFactor;
        this.terminalVelocity = PhysicsUtils.calculateTerminalVelocity(density, GRAVITY);
        this.inertialResistance = inertialResistance;
        this.velocity = new Vector2(0, 0);
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

        fallingTime = (canMoveBellow || canMoveLeft || canMoveRight) ? Math.min((fallingTime + Game.TIME_PER_FRAME), Short.MAX_VALUE) : 0;

        //Add velocity y if falling
        if (canMoveBellow) {
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= (float) (fallingTime * GRAVITY); }
            if (Main.DEBUG) { System.out.println("canMoveBellow: " + canMoveBellow + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }
        else if (canMoveLeft) {
            if (Utils.isBetween(velocity.x, -0.50f, 0.01f)) { velocity.x = -0.51f; }
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= 0.51f; }
            if (Main.DEBUG) { System.out.println("canMoveLeft: " + canMoveLeft + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }
        else if (canMoveRight) {
            if (Utils.isBetween(velocity.x, -0.01f, 0.51f)) { velocity.x = 0.51f; }
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= 0.51f; }
            if (Main.DEBUG) { System.out.println("canMoveRight: " + canMoveRight + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }

        int nextX = Math.round(x + velocity.x);
        int nextY = Math.round(y + velocity.y);

        //Move material according to it's velocity
        if (x != nextX || y != nextY) {
            if (Main.DEBUG) { System.out.println("velocity: " + velocity.x + " " + velocity.y); }
            grid.moveMaterial(this, x, y, nextX, nextY, Grid.MovementOptions.ResetVelocity, Grid.MovementOptions.SpreadVelocity);
            if (Main.DEBUG) { System.out.println("grid.moveMaterial: " + canMoveRight + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }

        //if (Main.DEBUG) { System.out.println("velocity: " + ((int) velocity.x) + " " + ((int) velocity.y)); }
        velocity.x *= frictionFactor;
        velocity.y *= frictionFactor;
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
