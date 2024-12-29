package me.serbinskis.burvis.materials.liquids;

import com.badlogic.gdx.math.Vector2;
import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.core.Game;
import me.serbinskis.burvis.core.Grid;
import me.serbinskis.burvis.input.KeyboardInput;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.gasses.Gas;
import me.serbinskis.burvis.materials.solids.Solid;
import me.serbinskis.burvis.utils.PhysicsUtils;
import me.serbinskis.burvis.utils.Utils;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class Liquid extends Material {
    private final float frictionFactor;
    private final float inertialResistance;
    private final float terminalVelocity;
    private final float spreadFactor;
    private double fallingTime = 0;
    private Vector2 velocity;

    public Liquid(String name, Color color, float frictionFactor, float inertialResistance, float density) {
        super(name, color, density);
        this.frictionFactor = frictionFactor;
        this.terminalVelocity = PhysicsUtils.calculateTerminalVelocity(density, GRAVITY);
        this.inertialResistance = inertialResistance;
        this.velocity = new Vector2(0, 0);
        this.spreadFactor = 0f;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getTerminalVelocity() {
        return terminalVelocity;
    }

    public float getSpreadVelocityX() {
        float spreadDirection = Game.RANDOM.nextBoolean() ? -1f : 1f;
        return Math.abs(velocity.y/SPREAD_FACTOR) * spreadDirection;
        //return spreadDirection * 2;
    }

    public float getSpreadVelocityY() {
        return 0;
    }

    @Override
    public void update(Grid grid) {
        if (stepped.get(0) == Main.game.getStepped()) { return; }
        super.update(grid);

        boolean canMoveBellow = this.isFalling(grid);
        boolean canMoveLeft = !canMoveBellow && this.canSwap(grid.getMaterial(x - 1, y));
        boolean canMoveRight = !canMoveBellow && this.canSwap(grid.getMaterial(x + 1, y));
        boolean canMoveLeftDown = !canMoveBellow && this.canSwap(grid.getMaterial(x - 1, y - 1));
        boolean canMoveRightDown = !canMoveLeftDown && this.canSwap(grid.getMaterial(x + 1, y - 1));

        fallingTime = (canMoveBellow || canMoveLeft || canMoveRight) ? Math.min((fallingTime + Game.TIME_PER_FRAME), Short.MAX_VALUE) : 0;
        int randomDirection = (canMoveLeft && canMoveRight) ? (Game.RANDOM.nextBoolean() ? -1 : 1) : canMoveLeft ? -1 : 1;

        //Add velocity y if falling
        if (canMoveBellow) {
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= (float) (fallingTime * GRAVITY); }
            if (Utils.isBetween(velocity.y, -0.50f, 0.01f)) { velocity.y = -0.51f; }
            if (Main.DEBUG) { System.out.println("canMoveBellow: " + canMoveBellow + " " + fallingTime + " " + velocity.y + " " + x + " " + Math.round(x + velocity.y)); }
        } else if (canMoveLeftDown) {
            if (Utils.isBetween(velocity.x, -0.50f, 0.01f)) { velocity.x = -0.51f; }
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= 0.51f; }
            if (Main.DEBUG) { System.out.println("canMoveLeftDown: " + canMoveLeftDown + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }  else if (canMoveRightDown) {
            if (Utils.isBetween(velocity.x, -0.01f, 0.51f)) { velocity.x = 0.51f; }
            if ((velocity.y > -this.terminalVelocity)) { this.velocity.y -= 0.51f; }
            if (Main.DEBUG) { System.out.println("canMoveRightDown: " + canMoveRightDown + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        } else if (canMoveLeft && randomDirection == -1) {
            if (Utils.isBetween(velocity.x, -0.50f - spreadFactor, 0.01f)) { velocity.x = -0.51f - spreadFactor; }
            if (Main.DEBUG) { System.out.println("canMoveLeft: " + canMoveLeft + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        } else if (canMoveRight && randomDirection == 1) {
            if (Utils.isBetween(velocity.x, -0.01f, 0.51f + spreadFactor)) { velocity.x = 0.51f + spreadFactor; }
            if (Main.DEBUG) { System.out.println("canMoveRight: " + canMoveRight + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
        }

        int nextX = Math.round(x + velocity.x);
        int nextY = Math.round(y + velocity.y);

        //Move material according to it's velocity
        if (x != nextX || y != nextY) {
            if (Main.DEBUG) { System.out.println("velocity: " + velocity.x + " " + velocity.y); }
            grid.moveMaterial(this, x, y, nextX, nextY, Grid.MovementOptions.ResetVelocity, Grid.MovementOptions.SpreadVelocity);
            if (Main.DEBUG) { System.out.println("grid.moveMaterial: " + canMoveBellow + " " + fallingTime + " " + velocity.x + " " + x + " " + Math.round(x + velocity.x)); }
            while (KeyboardInput.isKeyPressed(GLFW_KEY_SPACE) && !KeyboardInput.isKeyPressed(GLFW_KEY_ENTER, false)) { try { Thread.sleep(1); } catch (InterruptedException ignored) {} }
        }

        //if (Main.DEBUG) { System.out.println("velocity: " + ((int) velocity.x) + " " + ((int) velocity.y)); }
        velocity.x *= frictionFactor;
        velocity.y *= frictionFactor;
    }

    @Override
    public boolean canSwap(Material material) {
        if (material instanceof Liquid) { return getDensity() > material.getDensity(); }
        return (material != null) && !(material instanceof Solid);
    }

    @Override
    public boolean isFalling(Grid grid) {
        Material material = grid.getMaterial(x, y - 1);
        if (material instanceof Liquid) { return getDensity() > material.getDensity(); }
        return (material instanceof Gas);
    }
}
