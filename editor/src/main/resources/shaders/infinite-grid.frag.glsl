// Courtesy of http://asliceofrendering.com/scene%20helper/2020/01/05/InfiniteGrid/
#version 330 core

uniform float uNearFade;
uniform float uFarFade;

uniform mat4 uViewProj;
uniform mat4 uModel;

uniform vec4 uGridColor;
uniform vec4 uAxisXColor;
uniform vec4 uAxisZColor;

uniform bool showAxes;


out vec4 outColor;

in vec3 nearPoint;
in vec3 farPoint;

vec4 grid(vec3 fragPos3D, float scale) {
    // use the scale variable to set the distance between the lines:
    vec2 coord = fragPos3D.xz * scale;
    vec2 derivative = fwidth(coord);
    vec2 grid = abs(fract(coord - 0.5) - 0.5) / derivative;
    float line = min(grid.x, grid.y);
    float minimumz = min(derivative.y, 1);
    float minimumx = min(derivative.x, 1);
    vec4 color = uGridColor;
    // z axis
    if (showAxes && fragPos3D.x > -0.5 * minimumx && fragPos3D.x < 0.5 * minimumx) {
        color = uAxisZColor;
    }
    // x axis
    if (showAxes && fragPos3D.z > -0.5 * minimumz && fragPos3D.z < 0.5 * minimumz) {
        color = uAxisXColor;
    }
    color.a *= 1.0 - min(line, 1.0);
    return color;
}

float computeDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * uModel * vec4(pos, 1.0);
    return (clip_space_pos.z / clip_space_pos.w);
}

float computeLinearDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * uModel * vec4(pos.xyz, 1.0);
    // put back between -1 and 1:
    float clip_space_depth = (clip_space_pos.z / clip_space_pos.w) * 2.0 - 1.0;
    // get linear value between 0.01 and 100:
    float near = uNearFade;
    float far = uFarFade;
    float linearDepth = (2.0 * near * far) / (far + near - clip_space_depth * (far - near));
    // normalize:
    return linearDepth / far;
}

void main()
{
    // t is the paramter in the line equation: f(t) = x + n*t
    // Only draw the grid where t > 0
    float t = -nearPoint.y / (farPoint.y - nearPoint.y);
    vec3 fragPos3D = nearPoint + t * (farPoint - nearPoint);

    // combine multiple grid resolutions:
    vec4 grid1x = grid(fragPos3D, 1);
    vec4 grid10x = grid(fragPos3D, 0.1);
    outColor = vec4(max(grid1x, grid10x).xyz, grid1x.a + grid10x.a);

    // to use this correctly with voxels, need to write depth in the voxel shader too
//    gl_FragDepth = computeDepth(fragPos3D);

    float linearDepth = computeLinearDepth(fragPos3D);
    float fading = exp(-4*linearDepth);

    outColor.a *= float(t > 0) * fading;
}