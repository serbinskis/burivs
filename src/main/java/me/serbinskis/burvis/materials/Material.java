package me.serbinskis.burvis.materials;

import com.badlogic.gdx.math.Vector2;
import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.core.Game;
import me.serbinskis.burvis.core.Grid;

import java.awt.*;
import java.util.BitSet;

import static org.lwjgl.opengl.GL11.*;

public class Material {
    public static float CELL_WIDTH = 2.0f / Main.GRID_WIDTH;
    public static float CELL_HEIGHT = 2.0f / Main.GRID_HEIGHT;
    public static float SPREAD_FACTOR = 2f;

    private final String name;
    private Grid grid;
    public Color color;
    public final float density;
    public Vector2 velocity = new Vector2(0, 0);
    public BitSet stepped = new BitSet(1);
    public int x = -1;
    public int y = -1;

    public Material(String name, Color color, float density) {
        this.name = name;
        this.color = color;
        this.density = density;
        this.stepped.set(0, 0);
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Color getColor() { return color; }
    public float getDensity() { return density; }
    public Vector2 getVelocity() { return velocity; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }

    public float getSpreadVelocityX() {
        float spreadDirection = Game.RANDOM.nextBoolean() ? -1f : 1f;
        return Math.abs(velocity.y/SPREAD_FACTOR) * spreadDirection;
    }

    public float getSpreadVelocityY() {
        return Math.abs(velocity.y/SPREAD_FACTOR);
    }

    public void update(Grid grid) {
        stepped.flip(0);
    }

    public void step(Grid grid) {}

    public void render(Grid grid) {
        glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);

        // Calculate the cell's corners in OpenGL coordinates
        float startX = -1.0f + x * CELL_WIDTH;
        float startY = -1.0f + y * CELL_HEIGHT;
        float endX = startX + CELL_WIDTH;
        float endY = startY + CELL_HEIGHT;

        glBegin(GL_QUADS); // Draw the cell as a rectangle
        glVertex2f(startX, startY); // Bottom-left corner
        glVertex2f(endX, startY);   // Bottom-right corner
        glVertex2f(endX, endY);     // Top-right corner
        glVertex2f(startX, endY);   // Top-left corner
        glEnd();
    }

    public boolean canSwap(Material material) { return false; }

    @Override
    public boolean equals(Object obj) {
        return (obj != null) && getClass().equals(obj.getClass());
    }
}
