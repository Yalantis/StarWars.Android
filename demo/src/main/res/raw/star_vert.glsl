uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.
uniform float u_Time;
uniform vec2 u_Resolution;

attribute vec4 a_Position; //initial
attribute vec2 a_TexCoordinate;
attribute vec4 a_Misc; //initial

varying vec2 v_TexCoordinate;
varying float v_Radius;

#define RADIUS 3.5

float rand( vec2 co )
{
   return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void rotate( in float angle, inout vec2 vector )
{
	mat2 rotationMatrix = mat2( cos( angle ), -sin( angle ),
			    sin( angle ),  cos( angle ));
				     vector *= rotationMatrix;
}

void main()
{
	// Transform the vertex into eye space.
	//v_Position = vec3(u_MVMatrix * a_Position);

	float aspect = u_Resolution.x / u_Resolution.y;

	// Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;

	vec2 centerPos = a_Position.xy;

	float f = mix(1.0, a_Misc.t, u_Time);

    centerPos *= mod(f, RADIUS);

    float size = a_Misc.s;

    size = mix(0.0, size, mod(f, RADIUS)/RADIUS);

    vec2 relativePos = vec2(
	  (a_TexCoordinate.s - 0.5) * 2.0 * size,
	  (a_TexCoordinate.t - 0.5) * 2.0 * size
    );

    vec2 v = vec2(0.0, 1.0);


	vec4 pos = vec4(
	relativePos + centerPos,
	  0.0,
	  1.0
	);


	gl_Position = u_MVPMatrix * pos;

	v_Radius = size * 2.5;
}
