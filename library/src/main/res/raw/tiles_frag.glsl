precision mediump float;       	// Set the default precision to medium. We don't need as high of a precision in the fragment shader.
uniform sampler2D u_Texture;    // The input texture.

varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

void main()
{
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}
