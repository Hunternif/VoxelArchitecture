// Courtesy of http://asliceofrendering.com/scene%20helper/2020/01/05/InfiniteGrid/
#version 330 core

uniform mat4 uViewProj;
uniform mat4 uModel;

out vec3 nearPoint;
out vec3 farPoint;

// Grid position are in xy clipped space
vec3 gridPlane[6] = vec3[] (
    vec3(1, 1, 0), vec3(-1, -1, 0), vec3(-1, 1, 0),
    vec3(-1, -1, 0), vec3(1, 1, 0), vec3(1, -1, 0)
);

vec3 unprojectPoint(float x, float y, float z) {
    mat4 viewProjInv = inverse(uViewProj * uModel);
    vec4 unprojectedPoint =  viewProjInv * vec4(x, y, z, 1.0);
    return unprojectedPoint.xyz / unprojectedPoint.w;
}

void main() {
    vec3 p = gridPlane[gl_VertexID];
    nearPoint = unprojectPoint(p.x, p.y, 0.0);
    farPoint = unprojectPoint(p.x, p.y, 1.0);

    gl_Position = vec4(p, 1.0);
}