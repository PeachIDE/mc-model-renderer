package com.github.mouse0w0.mmr.graphics;

import static org.lwjgl.opengl.GL30C.*;

public class Mesh {
    private final int id;

    private int mode;
    private int vertexCount;

    public Mesh() {
        id = glGenVertexArrays();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void bindBuffer(VertexBuffer buffer) {
        bind();
        buffer.bind();
    }

    public void bindArrayBuffer(VertexBuffer buffer, int index, int size, int type, boolean normalized, int stride, long pointer) {
        bindBuffer(buffer);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public void drawArrays() {
        bind();
        glDrawArrays(mode, 0, vertexCount);
    }

    public void dispose() {
        glDeleteVertexArrays(id);
    }
}
