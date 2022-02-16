#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec3 aOffset;
layout (location = 3) in vec3 aScale;
layout (location = 4) in vec4 aVoxColor;

out vec3 FragPos;
out vec3 Normal;
out vec4 VoxColor;

uniform mat4 uViewProj;
uniform mat4 uModel;

void main()
{
    FragPos = aPos * aScale + aOffset;
    Normal = aNormal;
    VoxColor = aVoxColor;

    gl_Position = uViewProj * uModel * vec4(FragPos, 1.0);
}