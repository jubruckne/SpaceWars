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
varying vec2 vTexcoord;
varying float iTime;
varying float iResolution;

#define PI 3.1415926538
#define PI2 PI * 2.0


float atan2_(float y, float x) {
    float t0, t1, t2, t3, t4;

    t3 = abs(x);
    t1 = abs(y);
    t0 = max(t3, t1);
    t1 = min(t3, t1);
    t3 = float(1) / t0;
    t3 = t1 * t3;

    t4 = t3 * t3;
    t0 = -float(0.013480470);
    t0 = t0 * t4 + float(0.057477314);
    t0 = t0 * t4 - float(0.121239071);
    t0 = t0 * t4 + float(0.195635925);
    t0 = t0 * t4 - float(0.332994597);
    t0 = t0 * t4 + float(0.999995630);
    t3 = t0 * t3;

    t3 = (abs(y) > abs(x)) ? float(1.570796327) - t3 : t3;
    t3 = (x < 0) ? float(3.141592654) - t3 : t3;
    t3 = (y < 0) ? -t3 : t3;

    return t3;
}

float atan2(vec2 dir)
{
    float angle = asin(dir.x) > 0 ? acos(dir.y) : -acos(dir.y);
    return angle;
}

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
    vTexcoord = vec2(
        (atan2_(a_normal.z, a_normal.x) / PI2) * 0.125 / 0.25,
        (sin(a_normal.y) / PI + 0.5) * 2.0 - 0.5
    );

    if(vTexcoord.x < 0.0)
        vTexcoord.x += 1.0;
    else if(vTexcoord.x > 1.0)
        vTexcoord.x -= 1.0;

    if(vTexcoord.y < 0.0)
        vTexcoord.y += 1.0;
    else if(vTexcoord.y > 1.0)
        vTexcoord.y -= 1.0;

    vTexcoord.y = 1.0 - vTexcoord.y;
    vTexcoord.x = 1.0 - vTexcoord.x;


}

