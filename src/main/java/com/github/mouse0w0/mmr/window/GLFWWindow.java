package com.github.mouse0w0.mmr.window;

import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow {
    private long handle;

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

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwMakeContextCurrent(handle);

        initCallbacks();
        centerOnScreen();
    }

    private void initCallbacks() {
        glfwSetKeyCallback(handle, (pointer, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(pointer, true);
        });
        glfwSetWindowSizeCallback(handle, (pointer, w, h) -> {
            width = w;
            height = h;
            resized = true;
        });
    }

    public long getHandle() {
        return handle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        glfwSetWindowSize(handle, width, height);
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
                handle,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );
    }

    public void show() {
        glfwShowWindow(handle);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void swapBuffers() {
        glfwSwapBuffers(handle);
        if (resized) resized = false;
    }

    public void dispose() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }
}
