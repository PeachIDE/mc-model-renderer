package com.github.mouse0w0.mmr.window;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWHelper {
    private static boolean initialized;

    public static void initialize() {
        if (initialized) return;

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        initialized = true;
    }

    public static void terminate() {
        if (!initialized)
            throw new IllegalStateException("GLFW not initialized");

        glfwTerminate();
        initialized = false;
    }
}
