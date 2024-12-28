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
    public static float GRAVITY = 9.81f;
    public static float SPREAD_FACTOR = 5f;

    private final String name;
    public Color color;
    public final float density;
    public Vector2 velocity = new Vector2();
    public BitSet stepped = new BitSet(1);

    public Material(String name, Color color, float density) {
        this.name = name;
        this.color = color;
        this.density = density;
        this.stepped.set(0, 0);
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public float getDensity() {
        return density;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getSpreadVelocityX() {
        float spreadDirection = Game.RANDOM.nextBoolean() ? -1f : 1f;
        return Math.abs(velocity.y/SPREAD_FACTOR) * spreadDirection;
    }

    public float getSpreadVelocityY() {
        return Math.abs(velocity.y/SPREAD_FACTOR);
    }

    public void update(Grid grid, int x, int y) {
        stepped.flip(0);
    }

    public void render(Grid grid, int x, int y) {
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
    public boolean isFalling(Grid grid, int x, int y) { return false; }
}
