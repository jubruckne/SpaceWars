#version 410

in vec4 vColor;
in vec3 vNormal;
in vec4 vPosition;
in vec2 vTexcoord;

uniform float u_textureMode; // 0 none, 1 map, 2 cubemap
uniform sampler2D u_texture;
uniform samplerCube u_textureCube;

out vec4 FragColor;

void main() {
    if (u_textureMode == 0.0) {
        FragColor = vColor;
    } else if (u_textureMode == 1.0) {
        FragColor = texture(u_texture, normalize(vNormal).xy);
    } else {
        FragColor = texture(u_textureCube, normalize(vNormal).xyz);
    }
}
