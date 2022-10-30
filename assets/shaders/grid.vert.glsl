#version 120

attribute vec3 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;

uniform mat4 u_projTrans;
uniform mat4 u_modelTrans;

varying vec4 vColor;
varying vec3 vNormal;
varying vec4 vPosition;

void main() {
    gl_Position = u_projTrans * u_modelTrans * vec4(a_position, 1);

    vPosition = u_projTrans * vec4(a_position, 1.0);
    vNormal = normalize(u_projTrans * u_modelTrans * vec4(a_position, 1)).xyz;
    vColor = a_color;
}

