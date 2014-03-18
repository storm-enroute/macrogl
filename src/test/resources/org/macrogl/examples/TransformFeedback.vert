#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;
layout (location = 2) in vec3 velocity;

out vec3 newPosition;
out vec3 newColor;
out vec3 newVelocity;

uniform float dtSeconds;
uniform samplerBuffer triangleVertices;

// http://www.blackpawn.com/texts/pointinpoly/
bool inside(vec3 p, vec3 a, vec3 b, vec3 c)  {
    vec3 v0 = c - a;
    vec3 v1 = b - a;
    vec3 v2 = p - a;

    float dot00 = dot(v0, v0);
    float dot01 = dot(v0, v1);
    float dot02 = dot(v0, v2);
    float dot11 = dot(v1, v1);
    float dot12 = dot(v1, v2);

    float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    return (u >= 0) && (v >= 0) && (u + v < 1);
}

void main() {
    newPosition = position + dtSeconds * velocity;

    newColor = color;
    newVelocity = velocity;

    vec3 v0 = texelFetch(triangleVertices, 0).xyz;
    vec3 v1 = texelFetch(triangleVertices, 1).xyz;
    vec3 v2 = texelFetch(triangleVertices, 2).xyz;

    if (inside(newPosition, v0, v1, v2)) {
        newVelocity = -velocity;
        newPosition = position + dtSeconds * newVelocity;
        
        if (inside(newPosition, v0, v1, v2)) {
            newPosition = vec3(1);
        }
    }

    newPosition += vec3(1);
    newPosition = mod(newPosition, 2);
    newPosition -= vec3(1);
    
    gl_Position = vec4(newPosition, 1);
}
