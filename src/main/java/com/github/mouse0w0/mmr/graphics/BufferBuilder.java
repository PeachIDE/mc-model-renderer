package com.github.mouse0w0.mmr.graphics;

import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BufferBuilder {
    private ByteBuffer buffer;
    private int stride;
    private int count;

    private boolean finish = true;

    public BufferBuilder() {
        this(4096);
    }

    public BufferBuilder(int initialCapacity) {
        buffer = MemoryUtil.memAlloc(initialCapacity);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getStride() {
        return stride;
    }

    public int getCount() {
        return count;
    }

    public void begin(int stride) {
        if (!finish)
            throw new IllegalStateException("Unfinished buffer");
        finish = false;
        buffer.clear();
        count = 0;
        this.stride = stride;
    }

    public void finish() {
        if (finish)
            throw new IllegalStateException("Finished buffer");
        if (buffer.position() % stride != 0)
            throw new IllegalStateException("Invalid vertex data");
        finish = true;
        buffer.flip();
        count = buffer.limit() / stride;
    }

    public void ensureCapacity(int capacity) {
        if (capacity > buffer.capacity()) {
            grow(Math.max(buffer.capacity() + buffer.capacity() << 1, capacity));
        }
    }

    protected void grow(int newCapacity) {
        ByteBuffer newBuffer = MemoryUtil.memAlloc(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        MemoryUtil.memFree(buffer);
        buffer = newBuffer;
    }

    public BufferBuilder pos(Vector3fc pos) {
        return pos(pos.x(), pos.y(), pos.z());
    }

    public BufferBuilder pos(float x, float y, float z) {
        ensureCapacity(buffer.position() + 3 * Float.BYTES);
        buffer.putFloat(x).putFloat(y).putFloat(z);
        return this;
    }

    public BufferBuilder color(Vector4fc color) {
        ensureCapacity(buffer.position() + 4 * Float.BYTES);
        buffer.putFloat(color.x()).putFloat(color.y()).putFloat(color.z()).putFloat(color.w());
        return this;
    }

    public BufferBuilder color(float r, float g, float b, float a) {
        ensureCapacity(buffer.position() + 4 * Float.BYTES);
        buffer.putFloat(r).putFloat(g).putFloat(b).putFloat(a);
        return this;
    }

    public BufferBuilder tex(float u, float v) {
        ensureCapacity(buffer.position() + 2 * Float.BYTES);
        buffer.putFloat(u).putFloat(v);
        return this;
    }

    public BufferBuilder normal(float x, float y, float z) {
        ensureCapacity(buffer.position() + 3 * Float.BYTES);
        buffer.putFloat(x).putFloat(y).putFloat(z);
        return this;
    }
}
