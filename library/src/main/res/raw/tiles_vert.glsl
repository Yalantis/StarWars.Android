uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.
uniform float u_DeltaPos;

attribute vec4 a_Position;		// Per-vertex position information we will pass in.
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in.

// Contains tile's x and y position + random value at .z
attribute vec3 a_TileXY;

varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.

void main()
{
  // Pass through the texture coordinate.
  v_TexCoordinate = a_TexCoordinate;

  // Tiles' elevation
  float z = - (a_TileXY.z - 0.5) * sin(u_DeltaPos/7.0) * 1.3;

  vec4 calcPos = a_Position;

  calcPos.z = z;

  float randomShift = a_TileXY.z;

  // Just some random perturbations
  calcPos.x += u_DeltaPos * (randomShift - 0.5) * 0.3;
  calcPos.y += u_DeltaPos + randomShift * sin(u_DeltaPos / 30.0) * 3.3;

  gl_Position = u_MVPMatrix * calcPos;
}
