package com.github.mouse0w0.mmr.graphics;

import org.joml.Matrix4f;

public interface Viewport {
    Matrix4f getProjMatrix();

    void resize(int width, int height);
}
