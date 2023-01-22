#version 330 core
out vec4 FragColor;

in vec3 FragPos;

uniform mat4 uViewProj;
uniform vec4 uPickingColor;


float computeDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * vec4(pos, 1.0);
    return (clip_space_pos.z / clip_space_pos.w);
}

// Courtesy of http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
// Renders the silhouette of a model using the given uniform color
void main()
{
    FragColor = uPickingColor;

    gl_FragDepth = computeDepth(FragPos);
}