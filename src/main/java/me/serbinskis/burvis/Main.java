package me.serbinskis.burvis;

import me.serbinskis.burvis.core.Game;
import me.serbinskis.burvis.input.MouseInput;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
	public static Game game;
	public static long glfwWindow;
	public static String title = "Burvis";
	public static int width = 640;
	public static int height = 640;

    public static void main(String[] args) {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW."); }

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

		Main.glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
		if (glfwWindow == NULL) { throw new IllegalStateException("Failed to create the GLFW window."); }
		Main.game = new Game(glfwWindow, width, height);
		MouseInput.register(glfwWindow);

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(glfwWindow, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		glfwMakeContextCurrent(glfwWindow);
		glfwShowWindow(glfwWindow);
		GL.createCapabilities();

		final int TARGET_FPS = 60;
		final double FRAME_TIME = 1.0 / TARGET_FPS; // Frame time in seconds
		long lastTime = System.nanoTime();
		double delta = 0;

		while (!glfwWindowShouldClose(glfwWindow)) {
			long now = System.nanoTime();
			delta += (now - lastTime) / 1e9; // Convert nanoseconds to seconds
			lastTime = now;

			glfwPollEvents();

			if (delta >= FRAME_TIME) {
				game.update(); // Update game state only when the frame cap is reached
				delta -= FRAME_TIME; // Subtract the frame time from delta
			}

			Main.render();
		}
        
        glfwTerminate();
	}

	public static void render() {
		glClearColor(0f, 0f, 0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		game.render();
		glfwSwapBuffers(glfwWindow);
	}
}