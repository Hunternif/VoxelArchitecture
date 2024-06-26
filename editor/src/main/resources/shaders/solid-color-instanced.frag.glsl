#version 330 core

in vec3 FragPos;
in vec4 VertColor;

out vec4 color;

uniform mat4 uViewProj;
// This is a cheat to prevent Z-fighting of lines on top of voxels.
uniform float depthOffset;

float computeDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * vec4(pos, 1.0);
    clip_space_pos.z += depthOffset;
    return (clip_space_pos.z / clip_space_pos.w);
}

void main()
{
    color = VertColor;
    gl_FragDepth = computeDepth(FragPos);
}