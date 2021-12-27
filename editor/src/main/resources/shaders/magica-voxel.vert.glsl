#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec3 aOffset;

out vec3 FragPos;
out vec3 Normal;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

void main()
{
    FragPos = vec3(uModel * vec4(aPos + aOffset, 1.0));
    Normal = aNormal;

    gl_Position = uProjection * uView * vec4(FragPos, 1.0);
}