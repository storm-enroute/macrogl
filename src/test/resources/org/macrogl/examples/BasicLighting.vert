#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 color;

out vec3 fragmentColor;

uniform mat4 projection;
uniform mat4 viewTransform;

void main() {
    gl_Position = projection * viewTransform * vec4(position, 1.0);
    fragmentColor = color;
}
