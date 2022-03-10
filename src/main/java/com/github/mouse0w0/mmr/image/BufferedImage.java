package com.github.mouse0w0.mmr.image;

import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.ByteBuffer.allocateDirect;
import static org.lwjgl.system.MemoryUtil.*;

public class BufferedImage {
    private final int width;
    private final int height;
    private final int stride;
    private final ByteBuffer pixelBuffer;
    private final long address;

    public static BufferedImage load(Path path) throws IOException {
        try {
            return load(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IOException("Cannot load image from url: " + path, e);
        }
    }

    public static BufferedImage load(InputStream input) throws IOException {
        try (InputStream is = input) {
            byte[] bytes = IOUtils.toByteArray(input);
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes).flip();
            return load(buffer);
        }
    }

    public static BufferedImage load(ByteBuffer bytes) throws IOException {
        ByteBuffer directBytes = bytes.isDirect() ? bytes : (ByteBuffer) ByteBuffer.allocateDirect(bytes.capacity()).put(bytes).flip();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            ByteBuffer pixelBuffer = STBImage.stbi_load_from_memory(directBytes, w, h, c, 4);
            if (pixelBuffer == null) {
                throw new IOException("Failed to load image");
            }
            return BufferedImage.wrap(pixelBuffer, w.get(0), h.get(0));
        }
    }

    public static BufferedImage wrap(ByteBuffer pixelBuffer, int width, int height) {
        return new BufferedImage(pixelBuffer, width, height, pixelBuffer.isReadOnly() || !pixelBuffer.isDirect());
    }

    public static BufferedImage resize(BufferedImage src, int width, int height) {
        BufferedImage image = new BufferedImage(width, height);
        image.setImage(0, 0, src);
        return image;
    }

    public BufferedImage(int width, int height) {
        this(allocateDirect(Integer.BYTES * width * height), width, height, false);
    }

    public BufferedImage(int width, int height, int fillColorRGBA) {
        this(width, height);
        fill(fillColorRGBA);
    }

    private BufferedImage(ByteBuffer pixelBuffer, int width, int height, boolean copy) {
        this.width = width;
        this.height = height;
        this.stride = width << 2;
        this.pixelBuffer = copy ? (ByteBuffer) allocateDirect(pixelBuffer.capacity()).put(pixelBuffer).flip() : pixelBuffer;
        this.address = memAddress0(this.pixelBuffer);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getPixelBuffer() {
        return pixelBuffer;
    }

    public int getPixel(int x, int y) {
        return pixelBuffer.getInt(y * stride + (x << 2));
    }

    public void setPixel(int x, int y, int rgba) {
        pixelBuffer.putInt(y * stride + (x << 2), rgba);
    }

    public void setPixel(int x, int y, int width, int height, int rgba) {
        for (int i = y, maxY = y + height; i < maxY; i++) {
            memSet(address + i * stride + (x << 2), rgba, width << 2);
        }
    }

    public void setImage(int x, int y, ByteBuffer src, int srcWidth, int srcHeight) {
        setImage(x, y, src, srcWidth, srcHeight, 0, 0, srcWidth, srcHeight);
    }

    public void setImage(int x, int y, BufferedImage src) {
        setImage(x, y, src.getPixelBuffer(), src.getWidth(), src.getHeight());
    }

    public void setImage(int x, int y, BufferedImage src, int srcMinX, int srcMinY, int srcMaxX, int srcMaxY) {
        setImage(x, y, src.getPixelBuffer(), src.getWidth(), src.getHeight(), srcMinX, srcMinY, srcMaxX, srcMaxY);
    }

    public void setImage(int x, int y, ByteBuffer src, int srcWidth, int srcHeight, int srcMinX, int srcMinY, int srcMaxX, int srcMaxY) {
        final int srcOffset = srcMinX << 2;
        final int dstOffset = x << 2;
        final long srcAddress = memAddress0(src) + srcOffset;
        final long dstAddress = address + dstOffset;
        final int srcStride = srcWidth << 2;
        final int bytes = (srcMaxX - srcMinX) << 2;
        for (int srcY = srcMinY, dstY = y; srcY < srcMaxY; srcY++, dstY++) {
            memCopy(srcAddress + srcY * srcStride, dstAddress + dstY * stride, bytes);
        }
    }

    public void fill(int rgba) {
        pixelBuffer.clear();
        memSet(pixelBuffer.asIntBuffer(), rgba);
    }
}
