package me.serbinskis.burvis.input;

import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    public static int RADIUS = Main.DEBUG ? 0 : 5;
    public static int pressed = -1;

    public static void register(long glfwWindow) {
        glfwSetCursorPosCallback(glfwWindow, MouseInput::mouseMove);
        glfwSetMouseButtonCallback(glfwWindow, MouseInput::mouseClick);
        glfwSetScrollCallback(glfwWindow, MouseInput::mouseScroll);
    }

    public static void mouseMove(long window, double x, double y) {
        if (MouseInput.pressed == -1) { return; }
        //System.out.println("Mouse moved to position: (" + x + ", " + y + ")");

        int centerX = (int) (x * (Main.game.getGrid().getWidth() / (double) Main.WIDTH)); // Mapping x to grid space
        int centerY = Main.game.getGrid().getHeight() - (int) (y * (Main.game.getGrid().getHeight() / (double) Main.HEIGHT)); // Mapping y to grid space

        //Main.game.getGrid().setMaterial(centerX, centerY, MaterialRegistry.createMaterial(MaterialRegistry.SAND));
        //Main.game.getGrid().moveMaterial(centerX, centerY, centerX+100, centerY+50);

        for (int i = centerX - RADIUS; i <= centerX + RADIUS; i++) {
            for (int j = centerY - RADIUS; j <= centerY + RADIUS; j++) {
                if ((i - centerX) * (i - centerX) + (j - centerY) * (j - centerY) <= RADIUS * RADIUS) {
                    Material material = MouseInput.pressed == GLFW_MOUSE_BUTTON_LEFT ? MaterialRegistry.createMaterial(MaterialRegistry.SAND) : MaterialRegistry.STONE;
                    Main.game.getGrid().setMaterial(i, j, material);
                }
            }
        }
    }

    public static void mouseClick(long window, int button, int action, int mods) {
        MouseInput.pressed = (action == GLFW_PRESS) ? button : -1;
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        glfwGetCursorPos(window, xpos, ypos);
        mouseMove(window, xpos[0], ypos[0]);
    }

    public static void mouseScroll(long window, double x, double y) {
        //TODO: Implement
    }
}
