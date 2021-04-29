#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec4 color;
layout (location=2) in vec2 texCoord;

out vec4 v_Color;
out vec2 v_TexCoord;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

void main()
{
    gl_Position = projMatrix * viewMatrix * vec4(position, 1.0);
    v_Color = color;
    v_TexCoord = texCoord;
}