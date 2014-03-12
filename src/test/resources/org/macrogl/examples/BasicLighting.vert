#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 color;

out vec3 fragmentColor;

uniform mat4 projection;

const mat4 transform = mat4(
    vec4(0.5, 0.0, 0.0, 0.0),
    vec4(0.0, 0.5, 0.0, 0.0),
    vec4(0.0, 0.0, 0.5, 0.0),
    vec4(1.0, -0.7, -4.0, 1.0));

void main() {
    gl_Position = projection * transform * vec4(position, 1.0);
    fragmentColor = color;
}
