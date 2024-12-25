package me.serbinskis.burvis.core;

import java.util.BitSet;
import java.util.Random;

public class Game {
    public static int FPS = 60;
    public static double TIME_PER_FRAME = 1.0 / Game.FPS;
    public static Random RANDOM = new Random(System.nanoTime());
    private final long glfwWindow;
    private final Grid grid;
    public BitSet stepped = new BitSet(1);

    public Game(long glfwWindow, int width, int height) {
        this.glfwWindow = glfwWindow;
        this.grid = new Grid(width, height);
    }

    public Grid getGrid() {
        return grid;
    }

    public void update() {
        stepped.flip(0);
        grid.update();
    }

    public void render() {
        grid.render(glfwWindow);
    }

    public boolean getStepped() {
        return stepped.get(0);
    }
}
