#version 330 core

uniform vec3 uGridColor;

out vec4 color;

void main()
{
    color = vec4(uGridColor, 1.0);
}