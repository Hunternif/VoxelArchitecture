#version 330 core

uniform sampler2D uTexSampler;

in vec2 fTexCoords;

out vec4 color;

void main()
{
    color = texture(uTexSampler, fTexCoords);
}