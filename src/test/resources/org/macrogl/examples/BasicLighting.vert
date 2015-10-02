#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 color;

out vec3 fragColor;
out vec3 fragNormal;

uniform mat4 projection;
uniform mat4 viewTransform;
uniform mat4 worldTransform;

void main() {
  gl_Position = projection * viewTransform * worldTransform * vec4(position, 1.0);
  fragColor  = color;
  fragNormal = normal;
}
