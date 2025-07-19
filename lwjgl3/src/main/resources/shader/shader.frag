#ifdef GL_ES
precision mediump float;
#endif

uniform float u_Time;
uniform vec3 u_cameraPos;

varying vec4 v_col;

void main() {
    if (v_col.r == 0.0 && v_col.g == 0.0 && v_col.b == 0.0) {
        vec2 coord = gl_FragCoord.xy / 100.0 + vec2(u_Time / 10.0, u_Time / 15.0) + u_cameraPos.xy / 200.0;
        vec2 coordX = coord;
        coordX.x = float(floor(coordX.x + 0.5));
        vec2 coordY = coord;
        coordY.y = float(floor(coordY.y + 0.5));
        float someVal = sqrt(sqrt(distance(coord, coordX) + 0.02) * sqrt(distance(coord, coordY) + 0.02));
        float color = 1.0 - sqrt(someVal);
        gl_FragColor.b = color * color;

        gl_FragColor.a = 1.0;
    } else {
        gl_FragColor = v_col;
    }
}
