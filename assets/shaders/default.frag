varying vec4 vColor;
varying vec3 vNormal;

void main() {
    //this is hardcoded you want to pass it from your environment
    float ambient_strength = 0.1;
    vec3 light_direction = vec3(1.0, 0.25, 0.0);
    vec3 light_color = vec3(1.0, 1.0, 1.0);

    // ensure it's normalized
    // you can normalize it outside of the shader, since it's a directional light
    light_direction = normalize(light_direction);

    vec3 ambient_light = ambient_strength * light_color;

    // calculate the dot product of
    // the light to the vertex normal
    //if (dot(vNormal, light_direction) < 0.1) discard;
    vec3 diffuse_light = vec3(max(0.0, dot(vNormal, light_direction))) * light_color;

    // combine ambient and diffuse
    vec3 light = (ambient_light + diffuse_light) * vColor.xyz;

    // feed into our frag colour
    gl_FragColor = vec4(light, 1.0);
}
