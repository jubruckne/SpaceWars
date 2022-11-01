#version 130

attribute vec3 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;

uniform mat4 u_projTrans;
uniform mat4 u_modelTrans;

varying vec4 vColor;
varying vec3 vNormal;
varying vec4 vPosition;
varying vec2 vTexcoord;

#define PI 3.1415926538
#define _2PI PI * 2.0

void main() {
    vColor = a_color;
    gl_Position = u_projTrans * u_modelTrans * vec4(a_position, 1);

    vNormal = normalize(vec4(a_position, 1)).xyz;
    vPosition = u_projTrans * vec4(a_position, 1.0);

    Texcoord = vec2(
        (atan2_(vNormal.x, vNormal.z) / -_2PI) * 0.5,
        (asin(vNormal.y) / PI + 0.5)
    );

    vNormal = normalize(u_projTrans * u_modelTrans * vec4(a_position, 1)).xyz;



}

