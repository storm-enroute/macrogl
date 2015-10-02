#version 330

in vec3 fragColor;
in vec3 fragNormal;

out vec4 finalColor;

uniform mat4 worldTransform;

uniform vec3 lightColor;
uniform vec3 lightDirection;
uniform float ambient;
uniform float diffuse;

void main() {
  vec3 transformedNormal = normalize(mat3(worldTransform) * fragNormal);
  float diffuseFactor = max(0, dot(transformedNormal, -lightDirection));
    
  vec3 diffuseColor = diffuseFactor * diffuse * lightColor;
  vec3 ambientColor = ambient * lightColor;
    
  finalColor = vec4(fragColor * (diffuseColor + ambientColor), 1.0);
}
