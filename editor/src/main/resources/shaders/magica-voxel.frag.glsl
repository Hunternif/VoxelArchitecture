#version 330 core
out vec4 FragColor;

in vec3 Normal;
in vec3 FragPos;


uniform vec3 uSkylightDir;
uniform vec3 uSkylightColor;
uniform float uSkylightPower;

uniform vec3 uBacklightDir;
uniform vec3 uBacklightColor;
uniform float uBacklightPower;

uniform vec3 uAmbientColor;
uniform float uAmbientPower;

uniform vec3 uObjectColor;


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
    vec3 totalLight = ambientLight + skyDiffLight + backDiffLight;
    vec3 result = totalLight * uObjectColor;
    FragColor = vec4(result, 1.0);
}