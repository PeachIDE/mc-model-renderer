package com.github.mouse0w0.mmr;

import com.github.mouse0w0.mmr.image.BufferedImage;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextureMap {
    private final BufferedImage image;
    private final Map<String, Vector4f> texCoords;

    public static Builder builder() {
        return new Builder();
    }

    private TextureMap(BufferedImage image, Map<String, Vector4f> texCoords) {
        this.image = image;
        this.texCoords = texCoords;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Vector4f getTexCoord(String name) {
        return texCoords.get(name);
    }

    public static class Builder {
        private Map<String, BufferedImage> textureMap = new LinkedHashMap<>();

        public Builder texture(String name, BufferedImage texture) {
            textureMap.put(name, texture);
            return this;
        }

        public TextureMap build() {
            float mapWidth = 0, mapHeight = 0;
            for (BufferedImage image : textureMap.values()) {
                mapWidth += image.getWidth();
                if (image.getHeight() > mapHeight) {
                    mapHeight = image.getHeight();
                }
            }

            BufferedImage texMap = new BufferedImage((int) mapWidth, (int) mapHeight);
            Map<String, Vector4f> texCoords = new HashMap<>();

            int x = 0, y = 0;
            for (Map.Entry<String, BufferedImage> entry : textureMap.entrySet()) {
                BufferedImage image = entry.getValue();
                int width = image.getWidth(), height = image.getHeight();
                Vector4f uv = new Vector4f(x / mapWidth,
                        y / mapHeight,
                        (x + width) / mapWidth,
                        (y + height) / mapHeight);
                texCoords.put(entry.getKey(), uv);
                texMap.setImage(x, y, image);
                x += width;
                y += height;
            }

            return new TextureMap(texMap, texCoords);
        }
    }
}
