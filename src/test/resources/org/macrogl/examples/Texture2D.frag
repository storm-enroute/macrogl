#version 330

in vec2 fragmentTextureCoord;

out vec4 finalColor;

uniform sampler2D testTexture;

void main() {
    finalColor = vec4(texture(testTexture, fragmentTextureCoord).rgb, 1.0);
}

