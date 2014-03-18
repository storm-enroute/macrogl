#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;
layout (location = 2) in vec3 velocity;

out vec3 newPosition;
out vec3 newColor;
out vec3 newVelocity;

uniform float dtSeconds;

void main() {
    newPosition = position + dtSeconds * velocity;

    newPosition += vec3(1);
    newPosition = mod(newPosition, 2);
    newPosition -= vec3(1);
    
    newColor    = color;
    newVelocity = velocity;
    
    gl_Position = vec4(newPosition, 1);
}
