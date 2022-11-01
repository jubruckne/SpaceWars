#version 410

in vec3 a_position;
in vec4 a_color;
in vec3 a_normal;

uniform mat4 u_projTrans;
uniform mat4 u_modelTrans;

out vec4 vColor;
out vec3 vNormal;
out vec4 vPosition;
out vec2 vTexcoord;

uniform float u_textureMode; // 0 none, 1 map, 2 cubemap

void main() {
    gl_Position = u_projTrans * u_modelTrans * vec4(a_position, 1);

    vPosition = u_projTrans * vec4(a_position, 1.0);
    vNormal = (vec4(a_position, 1.0)).xyz;
    vTexcoord = a_position.zy;
    vColor = a_color;
}

