package com.github.mouse0w0.mmr.graphics;

import com.github.mouse0w0.minecraft.model.McModel;
import com.github.mouse0w0.minecraft.model.McModelHelper;
import com.github.mouse0w0.mmr.ModelBakery;
import com.github.mouse0w0.mmr.TextureMap;
import com.github.mouse0w0.mmr.image.BufferedImage;
import com.github.mouse0w0.mmr.window.GLFWHelper;
import com.github.mouse0w0.mmr.window.GLFWWindow;
import org.apache.commons.io.IOUtils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL30C.*;

public class Renderer {
    private GLFWWindow window;
    private ShaderProgram program;
    private Viewport viewport;
    private Camera camera;

    private Mesh mesh;
    private Texture2D texture;

    public void run() {
        initialize();
        loop();
        dispose();
    }

    private void initialize() {
        window = new GLFWWindow(600, 600, "Model Viewer");
        window.show();

        GL.createCapabilities();
        glClearColor(0f, 0f, 0f, 1f);

        viewport = new PerspectiveViewport();

        camera = new FocusCamera(new Vector3f(0.5f, 0, 0.5f));
        camera.bindWindow(window);

        program = new ShaderProgram();
        try {
            program.loadVertexShader(IOUtils.resourceToString("/default.vsh", StandardCharsets.UTF_8));
            program.loadFragmentShader(IOUtils.resourceToString("/default.fsh", StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        program.link();

        BufferBuilder buf = new BufferBuilder();
        buf.begin((3 + 4 + 2) * Float.BYTES);

        try {
            McModel model = McModelHelper.load(Paths.get("resource\\rail.json"));
            TextureMap textureMap = TextureMap.builder()
                    .texture("block/rail", BufferedImage.load(Paths.get("resource\\rail.png")))
                    .texture("block/rail_wood", BufferedImage.load(Paths.get("resource\\rail_wood.png"))).build();
            texture = new Texture2D(textureMap.getImage());
            ModelBakery.bake(model, textureMap::getTexCoord, buf);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        buf.finish();

        VertexBuffer vertexBuffer = new VertexBuffer(GL_ARRAY_BUFFER);
        vertexBuffer.bufferData(buf.getBuffer(), GL_STATIC_DRAW);

        mesh = new Mesh();
        mesh.setMode(GL_TRIANGLES);
        mesh.setVertexCount(buf.getCount());
        mesh.bindArrayBuffer(vertexBuffer, 0, 3, GL_FLOAT, false, 36, 0);
        mesh.bindArrayBuffer(vertexBuffer, 1, 4, GL_FLOAT, false, 36, 12);
        mesh.bindArrayBuffer(vertexBuffer, 2, 2, GL_FLOAT, false, 36, 28);
    }

    private void loop() {
        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (window.isResized()) {
                glViewport(0, 0, window.getWidth(), window.getHeight());
                viewport.resize(window.getWidth(), window.getHeight());
            }

            program.bind();
            program.setUniform("projMatrix", viewport.getProjMatrix());
            program.setUniform("viewMatrix", camera.getViewMatrix());

            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            texture.bind();
            mesh.drawArrays();

            glDisable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);

            window.swapBuffers();

            glfwPollEvents();
        }
    }

    private void dispose() {
        window.dispose();

        GLFWHelper.terminate();
    }
}
