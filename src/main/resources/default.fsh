#version 330

in vec4 v_Color;
in vec2 v_TexCoord;

out vec4 fragColor;

uniform sampler2D tex0;

void main()
{
    fragColor = v_Color * texture(tex0, v_TexCoord);
}