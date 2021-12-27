package com.github.mouse0w0.mmr.graphics;

import org.joml.Matrix4f;

public class PerspectiveViewport implements Viewport {

    private final float fov;
    private final float zNear;
    private final float zFar;

    private final Matrix4f projMatrix = new Matrix4f();

    public PerspectiveViewport() {
        this((float) Math.toRadians(60), 0.01f, 1000f);
    }

    public PerspectiveViewport(float fov, float zNear, float zFar) {
        this.fov = fov;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    @Override
    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

    @Override
    public void resize(int width, int height) {
        projMatrix.setPerspective(fov, (float) width / height, zNear, zFar);
    }
}
