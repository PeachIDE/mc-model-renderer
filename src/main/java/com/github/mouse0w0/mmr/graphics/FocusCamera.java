package com.github.mouse0w0.mmr.graphics;

import com.github.mouse0w0.mmr.window.GLFWWindow;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static org.lwjgl.glfw.GLFW.*;

public class FocusCamera implements Camera {
    private final Matrix4f viewMatrix = new Matrix4f();

    private final Vector3fc center;

    private double pitch = 0;
    private double yaw = 0;
    private double distance = 1f;

    public FocusCamera(Vector3fc center) {
        this.center = center;
    }

    @Override
    public Matrix4fc getViewMatrix() {
        return viewMatrix.identity().lookAt(new Vector3f(0, 0, -1)
                .rotateX((float) Math.toRadians(pitch))
                .rotateY((float) Math.toRadians(yaw))
                .mul((float) distance)
                .add(center), center, UP);
    }

    private boolean rotating = false;

    private float mouseSensitivity = 0.2f;
    private double lastCursorX;
    private double lastCursorY;

    @Override
    public void bindWindow(GLFWWindow window) {
        glfwSetMouseButtonCallback(window.getPointer(), (pointer, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1) {
                if (action == GLFW_PRESS) {
                    lastCursorX = Double.NaN;
                    lastCursorY = Double.NaN;
                }
                rotating = action != GLFW_RELEASE;
            }
        });
        glfwSetCursorPosCallback(window.getPointer(), (pointer, x, y) -> {
            if (rotating) {
                if (Double.isNaN(lastCursorX)) lastCursorX = x;
                if (Double.isNaN(lastCursorY)) lastCursorY = y;

                double deltaCursorX = lastCursorX - x; // Not x - lastCursorX
                double deltaCursorY = lastCursorY - y;
                lastCursorX = x;
                lastCursorY = y;
                pitch = Math.min(89.0f, Math.max(-89.0f, pitch - deltaCursorY * mouseSensitivity));
                yaw += deltaCursorX * mouseSensitivity;
            }
        });
        glfwSetScrollCallback(window.getPointer(), (pointer, x, y) -> {
            distance = Math.min(Math.max(distance - y * mouseSensitivity, 0), 3);
        });
    }
}
