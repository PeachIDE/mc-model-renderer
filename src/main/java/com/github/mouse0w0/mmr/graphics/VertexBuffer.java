package com.github.mouse0w0.mmr.graphics;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {
    private int id;
    private int target;

    public VertexBuffer(int target) {
        this.target = target;
        id = glGenBuffers();
    }

    public void bufferData(ByteBuffer buffer, int usage) {
        glBindBuffer(target, id);
        glBufferData(target, buffer, usage);
    }

    public void bind() {
        glBindBuffer(target, id);
    }

    public void dispose() {
        glDeleteBuffers(id);
    }
}
