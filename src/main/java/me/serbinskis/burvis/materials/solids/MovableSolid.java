package me.serbinskis.burvis.materials.solids;

import com.badlogic.gdx.math.Vector2;
import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.core.Grid;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.gasses.Gas;
import me.serbinskis.burvis.materials.liquids.Liquid;
import me.serbinskis.burvis.utils.PhysicsUtils;

import java.awt.*;

import static me.serbinskis.burvis.utils.PhysicsUtils.GRAVITY;

public class MovableSolid extends Solid {
    private final float frictionFactor;
    private final float inertialResistance;
    private final float terminalVelocity;
    private boolean isFalling = false;

    public MovableSolid(String name, Color color, float frictionFactor, float inertialResistance, float density) {
        super(name, color, density);
        this.frictionFactor = frictionFactor;
        this.terminalVelocity = PhysicsUtils.calculateGameTerminalVelocity(density, GRAVITY);
        this.inertialResistance = inertialResistance;
        this.velocity = new Vector2(0, 0);
    }

    public float getTerminalVelocity() {
        return terminalVelocity;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public boolean isFreeFalling(Grid grid) {
        Material material = grid.getMaterial(getX(), getY() - 1);
        if (material instanceof MovableSolid solid) { return solid.isFreeFalling(grid); }
        return (material instanceof Gas || material instanceof Liquid);
    }

    @Override
    public void update(Grid grid) {
        if (stepped.get(0) == Main.game.getStepped()) { return; }
        super.update(grid);

        boolean canMoveBellow = this.isFreeFalling(grid);
        //boolean canMoveLeft = !canMoveBellow && this.canSwap(grid.getMaterial(getY() - 1, getY() - 1));
        //boolean canMoveRight = !canMoveBellow && this.canSwap(grid.getMaterial(getY() + 1, getY() - 1));
        //boolean isFreeFalling = (canMoveBellow || canMoveLeft || canMoveRight);

        /*//Utils.debug("x: " + x + " y: " + y + " canMoveBellow: " + canMoveBellow + " canMoveLeft: " + canMoveLeft + " canMoveRight: " + canMoveRight);

        Utils.debug("velocity.y before frictionFactor: " + velocity.y);
        velocity.x *= frictionFactor * (isFreeFalling ? 0.5f : 1f);
        //velocity.y *= frictionFactor;

        Utils.debug("velocity.y after frictionFactor: " + velocity.y);
        if (isFreeFalling && (velocity.y > -this.terminalVelocity)) { this.velocity.y -= (float) (Game.TIME_PER_FRAME * GRAVITY); }
        //if (canMoveLeft) { velocity.x -= 1f; } else if (canMoveRight) { velocity.x += 1f; }
        Utils.debug("velocity.y after isFreeFalling: " + velocity.y);

        int nextX = Math.round(getX() + velocity.x);
        int nextY = Math.round(getY() + velocity.y);

        if (getX() == nextX && getY() == nextY) { return; }
        Grid.MovementRecord record = grid.moveMaterial(getX(), getY(), nextX, nextY);
        //Utils.debug("drag: " + PhysicsUtils.calculateVerticalDrag(this.getDensity(), velocity.y));

        if (record.result().equals(Grid.MovementResult.HitY)) { velocity.y = -velocity.y * PhysicsUtils.COF; } //Make solid bounce when hitting ground, but it should lose some of the velocity
        if (record.result().equals(Grid.MovementResult.HitY) && (velocity.y < 1)) { velocity.y = 0f; } //Without this, it can cause infinite bouncing
        Utils.debug("velocity.y after HitY: " + velocity.y);*/
    }

    /*@Override
    public void update(Grid grid) {
        if (stepped.get(0) == Main.game.getStepped()) { return; }
        super.update(grid);

        boolean canMoveBellow = this.isFreeFalling(grid);
        boolean canMoveLeft = !canMoveBellow && this.canSwap(grid.getMaterial(x - 1, y - 1));
        boolean canMoveRight = !canMoveLeft && this.canSwap(grid.getMaterial(x + 1, y - 1));
        isFalling = (canMoveBellow || canMoveLeft || canMoveRight);

        //Add velocity y if falling
        if (canMoveBellow) {
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= (float) (Game.TIME_PER_FRAME * GRAVITY * 2f); }
            if (Main.DEBUG) { System.out.println("canMoveBellow: " + canMoveBellow + " " + isFalling + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }
        else if (canMoveLeft) {
            if (Utils.isBetween(velocity.x, -0.50f, 0.01f)) { velocity.x = -0.51f; }
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= 0.51f; }
            if (Main.DEBUG) { System.out.println("canMoveLeft: " + canMoveLeft + " " + isFalling + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }
        else if (canMoveRight) {
            if (Utils.isBetween(velocity.x, -0.01f, 0.51f)) { velocity.x = 0.51f; }
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= 0.51f; }
            if (Main.DEBUG) { System.out.println("canMoveRight: " + canMoveRight + " " + isFalling + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }

        int nextX = Math.round(x + velocity.x);
        int nextY = Math.round(y + velocity.y);

        //Move material according to it's velocity
        if (x != nextX || y != nextY) {
            if (Main.DEBUG) { System.out.println("velocity: " + velocity.x + " " + velocity.y); }
            grid.moveMaterial(this, x, y, nextX, nextY, Grid.MovementOptions.ResetVelocity, Grid.MovementOptions.SpreadVelocity);
            if (Main.DEBUG) { System.out.println("grid.moveMaterial: " + canMoveRight + " " + isFalling + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }

        //if (Main.DEBUG) { System.out.println("velocity: " + ((int) velocity.x) + " " + ((int) velocity.y)); }
        velocity.x *= frictionFactor;
        velocity.y *= frictionFactor;
        //Main.render();
    }*/

    @Override
    public boolean canSwap(Material material) {
        return (material != null) && !(material instanceof Solid);
    }
}
