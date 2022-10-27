varying vec4 vColor;
varying vec3 vNormal;
varying vec4 vPosition;
varying vec2 vTexcoord;
uniform float u_bw;
uniform sampler2D u_texture;

void main() {
    //this is hardcoded you want to pass it from your environment
    float ambient_strength = 0.15;
    vec3 light_direction = vec3(0.75, 0.25, 0.0);
    vec3 light_color = vec3(1.0, 1.0, 1.0);

    // ensure it's normalized
    // you can normalize it outside of the shader, since it's a directional light
    light_direction = normalize(light_direction);

    vec3 ambient_light = ambient_strength * light_color;

    // calculate the dot product of
    // the light to the vertex normal
    //if (dot(vNormal, light_direction) < 0.1) discard;
    vec3 diffuse_light = vec3(max(0.0, dot(vNormal, light_direction))) * light_color;

    // feed into our frag colour
    if (u_bw == 0.0) {
        // combine ambient and diffuse
        vec3 light = (ambient_light + diffuse_light) * vColor.xyz;
        gl_FragColor = vec4(light, 1.0);
        gl_FragColor = texture2D(u_texture, vTexcoord.st) * vec4(ambient_light + diffuse_light, 1.0) * 3.0;

    } else {
        vec3 light = (ambient_light + diffuse_light) * vColor.xyz;
        gl_FragColor = vec4(light, 1.0);
    }
}
