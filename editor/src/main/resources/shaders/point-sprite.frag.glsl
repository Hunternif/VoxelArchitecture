#version 330 core

uniform sampler2D uTexSampler;

out vec4 color;

void main()
{
    color = texture(uTexSampler, gl_PointCoord);
}