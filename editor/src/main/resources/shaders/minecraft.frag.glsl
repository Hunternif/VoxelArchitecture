#version 330 core
out vec4 FragColor;

in vec3 Normal;
in vec3 FragPos;
in vec4 ColorOrUV;
in vec2 AOUV;

uniform int uRenderMode; // colored or textured
const int MODE_COLORED = 1;
const int MODE_TEXTURED = 2;
const vec4 UNKNOWN_COLOR = vec4(1, 0, 1, 1); // pink

uniform sampler2D uBlockTexture;
uniform sampler2D uAOTexture;

uniform mat4 uViewProj;

uniform float uDarkenTop;
uniform float uDarkenBottom;
uniform float uDarkenX;
uniform float uDarkenZ;

uniform float uAOPower;

// This is a cheat to prevent Z-fighting of lines on top of voxels.
uniform float depthOffset;

const vec3 upDir = vec3(0, 1, 0);
const vec3 downDir = vec3(0, -1, 0);
const vec3 xDir = vec3(1, 0, 0);
const vec3 zDir = vec3(0, 0, 1);

float computeDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * vec4(pos, 1.0);
    clip_space_pos.z += depthOffset;
    return (clip_space_pos.z / clip_space_pos.w);
}

vec4 getColor() {
    if (uRenderMode == MODE_COLORED) {
        return ColorOrUV;
    } else if (uRenderMode == MODE_TEXTURED) {
        return texture(uBlockTexture, ColorOrUV.xy);
    } else {
        return UNKNOWN_COLOR;
    }
}

vec4 getAOColor() {
    // Negative UV means no AO
    if (AOUV.x >= 0 && AOUV.y >= 0) {
        return mix(vec4(1, 1, 1, 1), texture(uAOTexture, AOUV.xy), uAOPower);
    } else {
        return vec4(1, 1, 1, 1);
    }
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

    vec4 color = getColor() * getAOColor();
    float darkFactor = topFactor * bottomFactor * xFactor * zFactor;
    FragColor.rgb = color.rgb * darkFactor;
    FragColor.a = color.a;

    // To use depth correctly with other elements like the grid:
    gl_FragDepth = computeDepth(FragPos);
}