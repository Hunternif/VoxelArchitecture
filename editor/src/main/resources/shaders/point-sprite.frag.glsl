#version 330 core

uniform sampler2D uTexSampler;

in vec3 FragPos;

out vec4 color;

uniform mat4 uViewProj;

float computeDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * vec4(pos, 1.0);
    return (clip_space_pos.z / clip_space_pos.w);
}

void main()
{
    color = texture(uTexSampler, gl_PointCoord);
    gl_FragDepth = computeDepth(FragPos);
}