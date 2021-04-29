package com.github.mouse0w0.mmr.window;

import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow {
    private long pointer;

    private int width;
    private int height;
    private String title;

    private boolean resized = true;

    public GLFWWindow() {
        initialize();
    }

    public GLFWWindow(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        initialize();
    }

    private void initialize() {
        GLFWHelper.initialize();

        pointer = glfwCreateWindow(width, height, title, NULL, NULL);
        if (pointer == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwMakeContextCurrent(pointer);

        initCallbacks();
        centerOnScreen();
    }

    private void initCallbacks() {
        glfwSetKeyCallback(pointer, (pointer, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(pointer, true);
        });
        glfwSetWindowSizeCallback(pointer, (pointer, w, h) -> {
            width = w;
            height = h;
            resized = true;
        });
    }

    public long getPointer() {
        return pointer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        glfwSetWindowSize(pointer, width, height);
    }

    public String getTitle() {
        return title;
    }

    public boolean isResized() {
        return resized;
    }

    public void centerOnScreen() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(
                pointer,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );
    }

    public void show() {
        glfwShowWindow(pointer);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(pointer);
    }

    public void swapBuffers() {
        glfwSwapBuffers(pointer);
        if (resized) resized = false;
    }

    public void dispose() {
        glfwFreeCallbacks(pointer);
        glfwDestroyWindow(pointer);
    }
}
