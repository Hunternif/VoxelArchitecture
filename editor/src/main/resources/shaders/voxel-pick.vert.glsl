#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 FragPos;

uniform mat4 uViewProj;
uniform mat4 uModel;

// Courtesy of http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
// Renders the silhouette of a model using the given uniform color
void main()
{
    vec4 fragPos4 = uModel * vec4(aPos, 1.0);
    FragPos = fragPos4.xyz;
    gl_Position = uViewProj * fragPos4;
}