#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in mat4 aModel;

out vec3 FragPos;
out vec4 VertColor;

uniform mat4 uViewProj;
uniform mat4 uModel;

void main()
{
    vec4 fragPos4 = uModel * aModel * vec4(aPos, 1.0);
    FragPos = fragPos4.xyz;
    VertColor = aColor;

    gl_Position = uViewProj * fragPos4;
}