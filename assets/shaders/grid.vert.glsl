#version 330
in vec4 a_color;
in vec3 a_position;
in vec3 a_normal;

uniform mat4 u_projMatrix;
uniform mat4 u_modelMatrix;

out vec4 vColor;
out vec3 vNormal;
out vec4 vPosition;
out vec3 vTexcoord;

uniform float u_textureMode; // 0 none, 1 map, 2 cubemap

void main() {
    gl_Position = u_projMatrix * u_modelMatrix * vec4(a_position, 1.0);

    vPosition = u_projMatrix * vec4(a_position, 1.0);
    vNormal = a_position;
    vColor = a_color;

    if (u_textureMode == 1.0) {
        vTexcoord = vec3(a_position.xy, 0.0) + 0.5;
        vTexcoord = vec3(vNormal.x / 2.0 + 0.5, vNormal.y / 2.0, 1.0);
        vTexcoord = vec3(vTexcoord.y, -vTexcoord.x, 1.0);
    }
}

