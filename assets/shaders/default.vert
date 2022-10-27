#version 120

attribute vec3 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;

uniform float u_time;
uniform float u_bw;
uniform mat4 u_projTrans;
uniform mat4 u_modelTrans;

varying vec4 vColor;
varying vec3 vNormal;
varying vec4 vPosition;
varying float iTime;
varying float iResolition;
void main() {
    //float gray = min(1, max(max(a_color.x, a_color.y), a_color.z));
    if (u_bw == 0.0) {
        vColor = a_color;
    } else {
        vColor = vec4(
            min(1.0, a_color.r + 0.5),
            min(1.0, a_color.g + 0.5),
            min(1.0, a_color.b + 0.5),
            1.0
        );
    }

    iTime = u_time;
    gl_Position = u_projTrans * u_modelTrans * vec4(a_position, 1);

    vNormal = normalize(u_projTrans * u_modelTrans * vec4(a_position, 1)).xyz;
    vPosition = u_projTrans * vec4(a_position, 1.0);
}

