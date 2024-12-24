package me.serbinskis.burvis.input;

import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    public static int pressed = -1;

    public static void register(long glfwWindow) {
        glfwSetCursorPosCallback(glfwWindow, MouseInput::mouseMove);
        glfwSetMouseButtonCallback(glfwWindow, MouseInput::mouseClick);
        glfwSetScrollCallback(glfwWindow, MouseInput::mouseScroll);
    }

    public static void mouseMove(long window, double x, double y) {
        if (MouseInput.pressed == -1) { return; }
        //System.out.println("Mouse moved to position: (" + x + ", " + y + ")");

        int radius = 10;
        int centerX = (int) x;
        int centerY = Main.height - (int) y;

        for (int i = centerX - radius; i <= centerX + radius; i++) {
            for (int j = centerY - radius; j <= centerY + radius; j++) {
                if ((i - centerX) * (i - centerX) + (j - centerY) * (j - centerY) <= radius * radius) {
                    Material material = MouseInput.pressed == GLFW_MOUSE_BUTTON_LEFT ? MaterialRegistry.createMaterial(MaterialRegistry.SAND) : MaterialRegistry.EMPTY;
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
