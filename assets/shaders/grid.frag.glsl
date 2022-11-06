#version 330

in vec4 vColor;
in vec3 vNormal;
in vec4 vPosition;
in vec3 vTexcoord;

uniform float u_textureMode; // -1 wireframe, 0 shaded, 1 map, 2 cubemap
uniform sampler2D u_texture;
uniform samplerCube u_textureCube;

out vec4 FragColor;

void main() {
    if (u_textureMode == -1.0) {
        FragColor = vColor;
    } else if (u_textureMode == 0.0) {
        FragColor = vColor;
    } else if (u_textureMode == 1.0) {
        FragColor = texture(u_texture, vTexcoord.st);
    } else {
        FragColor = texture(u_textureCube, normalize(vNormal).xyz);
    }
}
