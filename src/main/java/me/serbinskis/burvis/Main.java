package me.serbinskis.burvis;

import me.serbinskis.burvis.core.Game;
import me.serbinskis.burvis.input.KeyboardInput;
import me.serbinskis.burvis.input.MouseInput;
import me.serbinskis.burvis.utils.ThreadUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
	public static boolean DEBUG = true;
	public static Game game;
	public static long glfwWindow;
	public static String title = "Burvis";
	public static int WIDTH = 800;
	public static int HEIGHT = 800;
	public static int GRID_WIDTH = 200;
	public static int GRID_HEIGHT = 200;

    public static void main(String[] args) {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW."); }

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

		Main.glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
		if (glfwWindow == NULL) { throw new IllegalStateException("Failed to create the GLFW window."); }
		Main.game = new Game(glfwWindow, GRID_WIDTH, GRID_HEIGHT);

		MouseInput.register(glfwWindow);
		KeyboardInput.register(glfwWindow);

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(glfwWindow, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
		glfwMakeContextCurrent(glfwWindow);
		glfwShowWindow(glfwWindow);
		GL.createCapabilities();

		Runnable updater = ThreadUtils.startThread(Game.TIME_PER_FRAME, Main::update);
		Runnable renderer = ThreadUtils.startTimer(Game.TIME_PER_FRAME, Main::render);

		while (!glfwWindowShouldClose(glfwWindow)) {
			glfwPollEvents();
			renderer.run();
		}

        glfwTerminate();
		System.exit(0);
	}

	public static void update() {
		if (!KeyboardInput.isKeyPressed(GLFW_KEY_SPACE)) { game.update(); }
		if (KeyboardInput.isKeyPressed(GLFW_KEY_ENTER)) { game.update(); }
		KeyboardInput.setKeyPressed(GLFW_KEY_ENTER, false);
	}

	public static void render() {
		glClearColor(0f, 0f, 0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		game.render();
		glfwSwapBuffers(glfwWindow);
	}
}