#version 330 core
out vec4 FragColor;

in vec3 Normal;
in vec3 FragPos;
in vec4 ColorOrUV;

uniform int uRenderMode; // colored or textured
const int MODE_COLORED = 1;
const int MODE_TEXTURED = 2;
const vec4 UNKNOWN_COLOR = vec4(1, 0, 1, 1); // pink

uniform sampler2D uTexSampler;

uniform mat4 uViewProj;

uniform vec3 uSkylightDir;
uniform vec3 uSkylightColor;
uniform float uSkylightPower;

uniform vec3 uBacklightDir;
uniform vec3 uBacklightColor;
uniform float uBacklightPower;

uniform vec3 uAmbientColor;
uniform float uAmbientPower;


float computeDepth(vec3 pos) {
    vec4 clip_space_pos = uViewProj * vec4(pos, 1.0);
    return (clip_space_pos.z / clip_space_pos.w);
}

vec4 getColor() {
    if (uRenderMode == MODE_COLORED) {
        return ColorOrUV;
    } else if (uRenderMode == MODE_TEXTURED) {
        return texture(uTexSampler, ColorOrUV.xy);
    } else {
        return UNKNOWN_COLOR;
    }
}

void main()
{
    // ambient
    vec3 ambientLight = uAmbientColor * uAmbientPower;

    // diffuse 1: "sky"
    vec3 norm = normalize(Normal);
    float skyDiff = max(dot(norm, -uSkylightDir), 0.0);
    vec3 skyDiffLight = uSkylightColor * skyDiff * uSkylightPower;

    // diffuse 2: "backlight"
    float backDiff = max(dot(norm, -uBacklightDir), 0.0);
    vec3 backDiffLight = uBacklightColor * backDiff * uBacklightPower;

    // sum up everything
    vec4 totalLight = vec4(ambientLight + skyDiffLight + backDiffLight, 1.0);
    FragColor = totalLight * getColor();

    // To use depth correctly with other elements like the grid:
    gl_FragDepth = computeDepth(FragPos);
}