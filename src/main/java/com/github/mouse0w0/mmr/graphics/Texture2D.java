package com.github.mouse0w0.mmr.graphics;

import com.github.mouse0w0.mmr.image.BufferedImage;

import static org.lwjgl.opengl.GL30C.*;

public class Texture2D {
    private int id;

    public Texture2D(BufferedImage image) {
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image.getPixelBuffer());
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void dispose() {
        glDeleteTextures(id);
    }
}
