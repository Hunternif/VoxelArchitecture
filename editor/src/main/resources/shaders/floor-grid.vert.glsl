#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aOffset;

out vec3 FragPos;

uniform mat4 uProjection;
uniform mat4 uView;

void main()
{
    vec4 pos4 = vec4(aPos.x + aOffset.x, aPos.y, aPos.z + aOffset.y, 1.0);
    gl_Position = uProjection * uView * pos4;
}