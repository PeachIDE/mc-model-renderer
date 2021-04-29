package com.github.mouse0w0.mmr.graphics;

import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int id;

    private int vsh;
    private int fsh;

    public ShaderProgram() {
        id = glCreateProgram();
        if (id == 0) {
            throw new RuntimeException("Cannot create shader program");
        }
    }

    public void loadVertexShader(String source) {
        vsh = loadShader(GL_VERTEX_SHADER, source);
    }

    public void loadFragmentShader(String source) {
        fsh = loadShader(GL_FRAGMENT_SHADER, source);
    }

    protected int loadShader(int type, String source) {
        int shader = glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("Cannot create shader");
        }

        glShaderSource(shader, source);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Cannot compile shader, message: " + glGetShaderInfoLog(shader, 1024));
        }

        glAttachShader(id, shader);

        return shader;
    }

    public void link() {
        glLinkProgram(id);

        if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Cannot link shader, message: " + glGetProgramInfoLog(id, 1024));
        }

        if (vsh != 0) {
            glDetachShader(id, vsh);
        }
        if (fsh != 0) {
            glDetachShader(id, fsh);
        }
//        glValidateProgram(id);
//        if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0);
    }

    public void bind() {
        glUseProgram(id);
    }

    public void dispose() {
        glDeleteProgram(id);
    }

    public void setUniform(String uniform, Matrix4fc value) {
        int location = glGetUniformLocation(id, uniform);
        if (location < 0) throw new RuntimeException("Cannot found uniform: " + uniform);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
        }
    }
}
