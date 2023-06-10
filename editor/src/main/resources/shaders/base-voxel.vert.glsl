#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec4 aColorOrUV;
layout (location = 3) in vec2 aAOUV;
layout (location = 4) in mat4 aModel;

out vec3 FragPos;
out vec3 Normal;
out vec4 ColorOrUV;
out vec2 AOUV;

uniform mat4 uViewProj;
uniform mat4 uModel;

void main()
{
    vec4 fragPos4 = uModel * aModel * vec4(aPos, 1.0);
    FragPos = fragPos4.xyz;
    ColorOrUV = aColorOrUV;
    AOUV = aAOUV;

    // assuming that uModel can rotate and translate, we must rotate the normal:
    // see https://stackoverflow.com/questions/14196252/rotate-normals-in-shader
    mat3 normalMat = mat3(uModel);
    Normal = normalize(aNormal * normalMat);

    gl_Position = uViewProj * fragPos4;
}