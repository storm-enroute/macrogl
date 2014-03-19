#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;

out vec3 fragmentColor;
out vec4 worldPosition;

uniform mat4 transform;
uniform mat4 projection;

void main() {
    worldPosition = transform * vec4(position, 1.0);
    gl_Position = projection * worldPosition;
    fragmentColor = color;
}
