#ifdef GL_ES
precision mediump float;
#endif

uniform float u_Time;

varying vec4 v_col;

void main() {
    if (v_col.r == 0 && v_col.g == 0 && v_col.b == 0) {
        vec2 coord = gl_FragCoord.xy / 100 + vec2(u_Time / 10, u_Time / 15);
        vec2 coordX = coord;
        coordX.x = round(coordX.x);
        vec2 coordY = coord;
        coordY.y = round(coordY.y);
        float color = 1;
        color -= sqrt(distance(coord, coordX)) / 1.9;
        color -= sqrt(distance(coord, coordY)) / 1.9;
        gl_FragColor.b = color * color;

        gl_FragColor.a = 1;
    } else {
        gl_FragColor = v_col;
    }
}
