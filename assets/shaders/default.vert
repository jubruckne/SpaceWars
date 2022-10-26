#version 120

attribute vec3 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;
uniform float u_bw;
uniform mat4 u_projTrans;
varying vec4 vColor;
varying vec3 vNormal;

void main() {
    if (u_bw == 0.0) {
        vColor = a_color;
    } else {
        float gray = (a_color.r + a_color.g + a_color.b) / 3.0;
        vColor = vec4(1, 1, 1, 1.0);
    }
    gl_Position = u_projTrans * vec4(a_position.xyz, 1);

    vNormal = normalize(u_projTrans * vec4(a_normal, 1)).xyz;
}

