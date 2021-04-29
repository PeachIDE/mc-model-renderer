package com.github.mouse0w0.mmr.graphics;

import com.github.mouse0w0.mmr.window.GLFWWindow;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public interface Camera {
    Vector3f UP = new Vector3f(0, 1, 0);

    Matrix4fc getViewMatrix();

    void bindWindow(GLFWWindow window);
}
