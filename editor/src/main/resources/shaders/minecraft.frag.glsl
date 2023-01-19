#version 330 core
out vec4 FragColor;

in vec3 Normal;
in vec3 FragPos;
in vec2 TexPos;

uniform sampler2D uTexSampler;

uniform mat4 uViewProj;

uniform float uDarkenTop;
uniform float uDarkenBottom;
uniform float uDarkenX;
uniform float uDarkenZ;

const vec3 upDir = vec3(0, 1, 0);
const vec3 downDir = vec3(0, -1, 0);
const vec3 xDir = vec3(1, 0, 0);
const vec3 zDir = vec3(0, 0, 1);

float computeDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * vec4(pos, 1.0);
    return (clip_space_pos.z / clip_space_pos.w);
}

void main()
{
    // each side is darkened by multiplying it with black with some % opacity.
    // - top side is darkened by 5%
    // - Z axis (front & back) is darkened by 25%
    // - X axis (left & right) is darkened by 43%
    // - bottom is darkened by 54%
    // exact values are set via uniforms.

    vec3 norm = normalize(Normal);

    // ignore negative x and z, lighting is symmetric
    norm.x = abs(norm.x);
    norm.z = abs(norm.z);

    float factor = 1.0;

    // measure of alignment to each direction, from 0 to 1:
    float topPct = max(dot(norm, upDir), 0.0);
    float bottomPct = max(dot(norm, downDir), 0.0);
    float xPct = max(dot(norm, xDir), 0.0);
    float zPct = max(dot(norm, zDir), 0.0);

    // compute the darkening factor in each direction:
    // - if the normal aligns with the direction, use full darkening,
    // - if it's perpendicular, use 100% light.
    float topFactor = mix(1.0, 1.0 - uDarkenTop, topPct);
    float bottomFactor = mix(1.0, 1.0 - uDarkenBottom, bottomPct);
    float xFactor = mix(1.0, 1.0 - uDarkenX, xPct);
    float zFactor = mix(1.0, 1.0 - uDarkenZ, zPct);

    vec4 texColor = texture(uTexSampler, TexPos);
    float darkFactor = topFactor * bottomFactor * xFactor * zFactor;
    FragColor.rgb = texColor.rgb * darkFactor;
    FragColor.a = texColor.a;

    // To use depth correctly with other elements like the grid:
    gl_FragDepth = computeDepth(FragPos);
}