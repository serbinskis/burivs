package me.serbinskis.burvis.core;

import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;
import me.serbinskis.burvis.materials.solids.MovableSolid;

import java.util.List;
import java.util.Optional;

public class Grid {
    private final Material[][] materials;
    private final int width;
    private final int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.materials = new Material[width][height];

        if (Main.DEBUG) {
            materials[20][0] = MaterialRegistry.createMaterial(MaterialRegistry.SAND);
            materials[19][0] = MaterialRegistry.createMaterial(MaterialRegistry.SAND);
            materials[20][1] = MaterialRegistry.createMaterial(MaterialRegistry.SAND);
        }

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

    public void setMaterial(int x, int y, Material material) {
        if (x < 0 || x >= width || y < 0 || y >= height) { return; }
        materials[x][y] = material;
    }

    public Material getMaterial(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) { return null; }
        return (materials[x][y]) == null ? MaterialRegistry.AIR : materials[x][y];
    }

    public void swapMaterial(int x1, int y1, int x2, int y2) {
        Material temp = materials[x1][y1];
        materials[x1][y1] = materials[x2][y2];
        materials[x2][y2] = temp;
    }

    public MovementResult moveMaterial(int x1, int y1, int x2, int y2) {
        // Get the material at the starting position
        if (Main.DEBUG) { System.out.println("moveMaterial: " + x1 + " " + y1); }
        Material material = getMaterial(x1, y1);
        if (material == null || material == MaterialRegistry.AIR) { return MovementResult.Success; }

        // Calculate the slope of the line
        int dx = x2 - x1;
        int dy = y2 - y1;

        // Traverse the grid along the line using the slope
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xStep = (double) dx / steps;
        double yStep = (double) dy / steps;

        double nextX = x1;
        double nextY = y1;
        double previousX;
        double previousY;

        for (int i = 1; i <= steps; i++) {
            previousX = nextX;
            previousY = nextY;
            nextX += xStep;
            nextY += yStep;

            int diffX = (int) (previousX - nextX);
            int diffY = (int) (previousY - nextY);

            //If no difference it means we don't have to move material
            int movementAbs = Math.abs(diffX) + Math.abs(diffY);
            if (movementAbs == 0) { continue; }

            //Get both current and next material
            Material current = getMaterial((int) previousX, (int) previousY);
            Material next = getMaterial((int) nextX, (int) nextY);

            //Check if we can move to next cell, if not return
            boolean hitXY = (movementAbs == 1) && (next == null || !current.canSwap(next));
            if (hitXY) { return Math.abs(diffX) == 1 ? MovementResult.HitX : MovementResult.HitY; }

            //Get diagonal space
            Material diagonal1 = getMaterial((int) previousX + diffX, (int) previousY);
            Material diagonal2 = getMaterial((int) previousX, (int) previousY + diffY);

            //Check if we have diagonal space for movement
            hitXY = (movementAbs == 2) && (diagonal1 == null || !current.canSwap(diagonal1));
            hitXY = hitXY && (diagonal2 == null || !current.canSwap(diagonal2));
            if (Main.DEBUG) { System.out.println(diffY + " " + previousX + " " + previousY + " " + diagonal2); }
            if (hitXY) { return MovementResult.HitXY; }

            //Swap materials
            swapMaterial((int) previousX, (int) previousY, (int) nextX, (int) nextY);

            //System.out.println("step: " + i + " | steps: " + steps);
            //Main.render();
            //try { Thread.sleep(100); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }

        return MovementResult.Success;
    }

    public void update() {
        for (int y = 0; y < materials[0].length; y++) {

            for (int x = materials.length - 1; x >= 0; x--) {
                Material material = materials[x][y];
                if ((material == MaterialRegistry.AIR) || (material == null)) { continue; }
                material.update(this, x, y);
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

    public void moveMaterial(MovableSolid material, int x1, int y1, int x2, int y2, MovementOptions ...opts) {
        MovementResult result = moveMaterial(x1, y1, x2, y2);
        if (Main.DEBUG) { System.out.println(result); }

        //Result can be Success instead of hitY
        boolean spreadVelocity = List.of(opts).contains(MovementOptions.SpreadVelocity) && (result == MovementResult.HitY);
        material.getVelocity().x += spreadVelocity ? Math.clamp(-material.getTerminalVelocity(), Math.abs(material.getVelocity().y/3f) * (Game.RANDOM.nextBoolean() ? -1f : 1f), material.getTerminalVelocity()) : 0;
        System.out.println("moveMaterial velocity: " + spreadVelocity + " " + material.getVelocity().x + " " + material.getVelocity().y);

        boolean resetVelocity = List.of(opts).contains(MovementOptions.ResetVelocity);
        if (resetVelocity && (result == MovementResult.HitX || result == MovementResult.HitXY)) { material.getVelocity().x = 0; }
        if (resetVelocity && (result == MovementResult.HitY || result == MovementResult.HitXY)) { material.getVelocity().y = 0; }
    }

    public enum MovementResult { Success, HitX, HitY, HitXY }
    public enum MovementOptions { ResetVelocity, SpreadVelocity }
}
