package me.serbinskis.burvis.core;

import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.input.KeyboardInput;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;

import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class Grid {
    private final Material[][] materials;
    private final int width;
    private final int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.materials = new Material[width][height];

        /*if (Main.DEBUG) {
            materials[20][0] = MaterialRegistry.createMaterial(MaterialRegistry.SAND);
            materials[19][0] = MaterialRegistry.createMaterial(MaterialRegistry.SAND);
            materials[20][1] = MaterialRegistry.createMaterial(MaterialRegistry.SAND);
        }*/

        /*for (int y = 0; y < 200; y++) {
            for (int x = 100; x < 200; x++) {
                materials[x][y] = MaterialRegistry.SAND;
                //materials[x][y] = new MaterialRegistry.Material(new Color((x / 200f),(y / 200f), 1.0f), 0);
            }
        }*/
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isOutsideBounds(int x, int y) {
        return (x < 0 || x >= width || y < 0 || y >= height);
    }

    public boolean setMaterial(Material material, int x, int y) {
        if (isOutsideBounds(x, y)) { return false; }
        setMaterialUnsafe(material, x, y);
        return true;
    }

    public void setMaterialUnsafe(Material material, int x, int y) {
        (materials[x][y] = material).setPosition(x, y);
    }

    public Material getMaterial(int x, int y) {
        if (isOutsideBounds(x, y)) { return null; }
        if (materials[x][y] == null) { setMaterialUnsafe(MaterialRegistry.createMaterial(MaterialRegistry.AIR), x, y); }
        return getMaterialUnsafe(x, y);
    }

    public Material getMaterialUnsafe(int x, int y) {
        return materials[x][y];
    }

    public boolean swapMaterial(int x1, int y1, int x2, int y2) {
        if (isOutsideBounds(x1, y1) || isOutsideBounds(x2, y2)) { return false; }
        swapMaterialUnsafe(x1, y1, x2, y2);
        return true;
    }

    public void swapMaterialUnsafe(int x1, int y1, int x2, int y2) {
        Material temp = getMaterialUnsafe(x1, y1);
        setMaterialUnsafe(materials[x2][y2], x1, y1);
        setMaterialUnsafe(temp, x2, y2);
    }

    public MovementRecord moveMaterial(int x1, int y1, int x2, int y2) {
        // Get the material at the starting position
        if (Main.DEBUG) { System.out.println("moveMaterial: " + x1 + " " + y1 + " " + x2 + " " + y2); }
        if (MaterialRegistry.AIR.equals(getMaterial(x1, y1))) { return new MovementRecord(x1, y1, MovementResult.NoChange); }

        // Calculate the slope of the line
        int dx = x2 - x1;
        int dy = y2 - y1;

        // Traverse the grid along the line using the slope
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xStep = (double) dx / steps;
        double yStep = (double) dy / steps;

        double nextX = x1;
        double nextY = y1;
        double currentX;
        double currentY;

        for (int i = 1; i <= steps; i++) {
            currentX = nextX;
            currentY = nextY;
            nextX += xStep;
            nextY += yStep;

            int diffX = (int) (currentX - nextX);
            int diffY = (int) (currentY - nextY);

            //If no difference it means we don't have to move material
            int movementAbs = Math.abs(diffX) + Math.abs(diffY);
            if (movementAbs == 0) { continue; }

            //Get both current and next material
            Material current = getMaterial((int) currentX, (int) currentY);
            Material next = getMaterial((int) nextX, (int) nextY);
            Material diagonalX = (movementAbs == 2) ? getMaterial((int) nextX, (int) currentY) : null;
            Material diagonalY = (movementAbs == 2) ? getMaterial((int) currentX, (int) nextY) : null;

            //Check if we can move to next cell, if not return
            boolean canMoveNext = (next != null && current.canSwap(next));
            boolean canMoveDiagonalX = (movementAbs == 2) && (diagonalX != null && current.canSwap(diagonalX));
            boolean canMoveDiagonalY = (movementAbs == 2) && (diagonalY != null && current.canSwap(diagonalY));

            //Handle vertical or horizontal hitting, does not handle diagonal ones
            boolean hitXY = (movementAbs == 1) && !canMoveNext;
            if (hitXY) { return new MovementRecord((int) currentX, (int) currentY, Math.abs(diffX) == 1 ? MovementResult.HitX : MovementResult.HitY); }

            //Handle diagonal hitting
            if (movementAbs == 2) {
                if (!canMoveNext && canMoveDiagonalX && !canMoveDiagonalY) { return new MovementRecord((int) currentX, (int) currentY, MovementResult.HitY); }
                if (!canMoveNext && !canMoveDiagonalX && canMoveDiagonalY) { return new MovementRecord((int) currentX, (int) currentY, MovementResult.HitX); }
                if (!canMoveNext || (!canMoveDiagonalX && !canMoveDiagonalY)) { return new MovementRecord((int) currentX, (int) currentY, MovementResult.HitXY); }
            }

            //Swap materials
            if (Main.DEBUG) { System.out.println("step: " + i + " " + canMoveNext + " " + canMoveDiagonalX + " " + canMoveDiagonalY); }
            swapMaterialUnsafe((int) currentX, (int) currentY, (int) nextX, (int) nextY);

            //System.out.println("step: " + i + " | steps: " + steps);
            if (KeyboardInput.isKeyPressed(GLFW_KEY_SPACE)) { try { Thread.sleep(100); } catch (InterruptedException e) { throw new RuntimeException(e); } }
        }

        return new MovementRecord(x2, y2, MovementResult.Success);
    }

    public void update() {
        for (int y = 0; y < materials[0].length; y++) {

            for (int x = materials.length - 1; x >= 0; x--) {
                Material material = getMaterialUnsafe(x, y);
                if ((material == null) || MaterialRegistry.AIR.equals(material)) { continue; }
                material.update(this);
            }
        }
    }

    public void render(long glfwWindow) {
        // Loop through each material in the grid
        for (int x = 0; x < materials.length; x++) {
            for (int y = 0; y < materials[x].length; y++) {
                Material material = Optional.ofNullable(materials[x][y]).orElse(MaterialRegistry.AIR);
                material.render(this, x, y);
            }
        }
    }

    public void moveMaterial(Material material, int x1, int y1, int x2, int y2, MovementOptions ...opts) {
        MovementRecord record = moveMaterial(x1, y1, x2, y2);
        MovementResult result = record.result();

        boolean resetVelocity = List.of(opts).contains(MovementOptions.ResetVelocity);
        boolean spreadVelocity = List.of(opts).contains(MovementOptions.SpreadVelocity);

        //There is chance that we move to empty cell, but next cell is occupied
        //For this we update the result, so that we know before hands if we will hit that neighbour
        if ((opts.length > 0) && (result == MovementResult.Success)) {
            Material horizontal = getMaterial(record.x() + (x2 > x1 ? 1 : -1), record.y());
            Material vertical = getMaterial(record.x(), record.y() + (y2 > y1 ? 1 : -1));
            boolean canSwapHorizontal = (horizontal != null && material.canSwap(horizontal));
            boolean canSwapVertical = (vertical != null && material.canSwap(vertical));
            if (!canSwapHorizontal && !canSwapVertical) { result = MovementResult.HitXY; }
            if (canSwapHorizontal && !canSwapVertical) { result = MovementResult.HitY; }
            if (!canSwapHorizontal && canSwapVertical) { result = MovementResult.HitX; }
        }

        if (Main.DEBUG) { System.out.println(result); }
        float spreadVelocityX = material.getSpreadVelocityX();
        float spreadVelocityY = material.getSpreadVelocityY();

        //If we hit vertically or horizontally reset velocity
        if (resetVelocity && (result == MovementResult.HitX || result == MovementResult.HitXY)) { material.getVelocity().x = 0; }
        if (resetVelocity && (result == MovementResult.HitY || result == MovementResult.HitXY)) { material.getVelocity().y = 0; }

        //If we hit vertically we spread vertical velocity to horizontal
        if (spreadVelocity && (result == MovementResult.HitY)) {
            material.getVelocity().x += spreadVelocityX;
            material.getVelocity().y += spreadVelocityY;
        }
    }

    public record MovementRecord(int x, int y, MovementResult result) {}
    public enum MovementResult { NoChange, Success, HitX, HitY, HitXY }
    public enum MovementOptions { ResetVelocity, SpreadVelocity }
}
