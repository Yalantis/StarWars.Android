precision mediump float;

uniform float u_Time;
uniform vec2 u_Resolution;

varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
varying float v_Radius;

void main()
{
    // Render a star
    float color = smoothstep(1.0, 0.0, length(v_TexCoordinate - vec2(0.5)) / v_Radius);
    gl_FragColor = vec4(color);
}
