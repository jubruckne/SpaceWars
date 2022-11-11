#version 330
in vec4 a_color;
in vec3 a_position;
in vec3 a_normal;

uniform mat4 u_projMatrix;
uniform mat4 u_modelMatrix;
uniform float u_time;

out vec4 vColor;
out vec3 vNormal;
out vec4 vPosition;
out vec3 vTexcoord;

uniform float u_textureMode; // -1 wireframe, 0 shaded, 1 map, 2 cubemap

void main() {
    gl_Position = u_projMatrix * u_modelMatrix * vec4(a_position, 1.0);

    vPosition = u_projMatrix * vec4(a_position, 1.0);
    vNormal = a_position;
    vColor = a_color;

    if (u_textureMode == 1.0) {
        //vTexcoord = vec3(vTexcoord.y, -vTexcoord.x, 1.0);
        vTexcoord = vec3(a_normal.x + 0.5, -a_normal.y + 0.5, 1.0);
    } else if (u_textureMode == 2.0) {
        vTexcoord = vNormal.xyz;
    } else if (u_textureMode == -1.0) {
        gl_PointSize = 5.0;

        float glow = (sin((u_time * 1.1 + gl_VertexID * 2.333) * 0.035) + 0.35) * 0.15 + 0.05;

        vColor = vec4(
            min(1.0, a_color.r + glow),
            min(1.0, a_color.g + glow),
            min(1.0, a_color.b + glow),
            1.0);

        vColor = vec4(1 - a_color.r, 1 - a_color.g, 1 - a_color.b, 1);
    }

}

