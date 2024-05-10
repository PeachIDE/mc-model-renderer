package com.github.mouse0w0.mmr.graphics;

import com.github.mouse0w0.minecraft.model.McModel;
import com.github.mouse0w0.minecraft.model.McModelHelper;
import com.github.mouse0w0.mmr.ModelBakery;
import com.github.mouse0w0.mmr.TextureMap;
import com.github.mouse0w0.mmr.image.BufferedImage;
import com.github.mouse0w0.mmr.window.GLFWHelper;
import com.github.mouse0w0.mmr.window.GLFWWindow;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL30C.*;

public class Renderer2D {
    private GLFWWindow window;

    private ShaderProgram program;

    private Matrix4f projMatrix = new Matrix4f().ortho(0f, 16f, 0f, 16f, -1000f, 1000f);
    private Matrix4f viewMatrix = new Matrix4f().translate(8f, 8f, 0f).scale(16f, 16f, 16f);

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
        glClearColor(0f, 0f, 0f, 0f);

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
            McModel model = McModelHelper.load(Paths.get("resource\\model.json"));
            TextureMap textureMap = TextureMap.builder().texture("Desktop/texture",
                    BufferedImage.load(Paths.get("resource\\texture.png"))).build();
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
            }

            program.bind();
            program.setUniform("projMatrix", projMatrix);
            //            program.setUniform("viewMatrix", viewMatrix);
            program.setUniform("viewMatrix",
                    new Matrix4f(viewMatrix).mul(new Matrix4f()
                            .rotateX((float) Math.toRadians(30))
                            .rotateY((float) Math.toRadians(225))
                            .scale(0.625f, 0.625f, 0.625f)
                            .translate(-0.5f, -0.5f, -0.5f)));

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
