varying vec4 vColor;
varying vec3 vNormal;

void main() {
    //	gl_FragColor = vColor;

    //this is hardcoded you want to pass it from your environment
    vec3 light = vec3(1.0, 0.0, 0.0);//it needs to be a uniform

    // ensure it's normalized
    light = normalize(light);//you can normalize it outside of the shader, since it's a directional light

    // calculate the dot product of
    // the light to the vertex normal
    float dProd = max(0.0, dot(vNormal, light));

    // feed into our frag colour
    gl_FragColor = vec4(dProd,
                        dProd,
                        dProd,
                        1.0) * vColor;


}
