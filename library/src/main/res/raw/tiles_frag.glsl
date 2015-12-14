precision mediump float;       	// Set the default precision to medium. We don't need as high of a
								// precision in the fragment shader.
uniform sampler2D u_Texture;    // The input texture.

varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

float rand( vec2 co )
{
   return step(0.9, fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453));
}

// The entry point for our fragment shader.
void main()
{
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}
