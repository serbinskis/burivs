package me.serbinskis.burvis.input;

import me.serbinskis.burvis.Main;
import me.serbinskis.burvis.materials.Material;
import me.serbinskis.burvis.materials.MaterialRegistry;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput {
    public static boolean[] keys = new boolean[GLFW_KEY_LAST];

    public static void register(long glfwWindow) {
        glfwSetKeyCallback(glfwWindow, KeyboardInput::keyCallback);
    }

    private static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key < 0 || key >= keys.length) { return; }
        if (action == GLFW_PRESS) { keys[key] = !keys[key]; }
    }

    public static boolean isKeyPressed(int key) {
        return keys[key];
    }

    public static boolean isKeyPressed(int key, boolean state) {
        boolean current_state = keys[key];
        keys[key] = state;
        return current_state;
    }

    public static void setKeyPressed(int key, boolean state) {
        keys[key] = state;
    }
}
