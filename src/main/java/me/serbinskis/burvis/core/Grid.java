package me.serbinskis.burvis.core;

import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;

public class Grid {
    private final Material[][] materials;
    private final int width;
    private final int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.materials = new Material[width][height];

        /*for (int y = 0; y < 200; y++) {
            for (int x = 100; x < 200; x++) {
                materials[x][y] = MaterialRegistry.SAND;
                //materials[x][y] = new MaterialRegistry.Material(new Color((x / 200f),(y / 200f), 1.0f), 0);
            }
        }*/
    }

    public void setMaterial(int x, int y, Material material) {
        if (x < 0 || x >= width || y < 0 || y >= height) { return; }
        materials[x][y] = material;
    }

    public Material getMaterial(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) { return null; }
        return (materials[x][y]) == null ? MaterialRegistry.EMPTY : materials[x][y];
    }

    public void swapMaterial(int x1, int y1, int x2, int y2) {
        Material temp = materials[x1][y1];
        materials[x1][y1] = materials[x2][y2];
        materials[x2][y2] = temp;
    }

    public void moveMaterial(int x1, int y1, int x2, int y2) {
        // Get the material at the starting position
        Material material = getMaterial(x1, y1);
        if (material == null || material == MaterialRegistry.EMPTY) { return; }

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

        for (int i = 0; i <= steps; i++) {
            previousX = nextX;
            previousY = nextY;
            nextX += xStep;
            nextY += yStep;

            if (previousX == nextX && previousY == nextY) { continue; }
            Material material1 = getMaterial((int) previousX, (int) previousY);
            Material material2 = getMaterial((int) nextX, (int) nextY);
            if (material2 == null || !material1.canSwap(material2)) { break; }
            swapMaterial((int) previousX, (int) previousY, (int) nextX, (int) nextY);
        }
    }

    public void update() {
        for (int y = 0; y < materials[0].length; y++) {

            for (int x = materials.length - 1; x >= 0; x--) {
                Material material = materials[x][y];
                if ((material == MaterialRegistry.EMPTY) || (material == null)) { continue; }
                material.update(this, x, y, material);
            }
        }
    }

    public void render(long glfwWindow) {
        // Loop through each material in the grid
        for (int x = 0; x < materials.length; x++) {
            for (int y = 0; y < materials[x].length; y++) {
                Material material = materials[x][y];
                if (material == null || material == MaterialRegistry.EMPTY) { continue; }
                material.render(this, x, y);
            }
        }
    }
}
