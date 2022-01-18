#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec3 aOffset;
layout (location = 3) in vec3 aScale;

out vec3 FragPos;
out vec4 VertColor;

uniform mat4 uViewProj;

void main()
{
    FragPos = aPos * aScale + aOffset;
    VertColor = aColor;

    gl_Position = uViewProj * vec4(FragPos, 1.0);
}