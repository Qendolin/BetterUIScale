#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float betteruiscale_smoothness;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color;
    if(betteruiscale_smoothness == 0.0f) {
        vec2 texSize = textureSize(Sampler0, 0);
        vec2 tx = texCoord0.xy * texSize;
        vec2 uv = (floor(tx) + 0.5) / texSize;
        color = texture(Sampler0, uv) * vertexColor * ColorModulator;
    } else {
        // Based on https://youtu.be/d6tp43wZqps?t=625 by t3ssel8r
        vec2 texSize = textureSize(Sampler0, 0);
        vec2 boxSize = clamp(fwidth(texCoord0.xy) * betteruiscale_smoothness * texSize, 1e-5, 1.0);
        vec2 tx = texCoord0.xy * texSize - 0.5 * boxSize;
        vec2 txOffset = smoothstep(vec2(1.0) - boxSize, vec2(1.0), fract(tx));
        vec2 uv = (floor(tx) + 0.5 + txOffset) / texSize;
        color = textureGrad(Sampler0, uv, dFdx(texCoord0.xy), dFdy(texCoord0.xy)) * vertexColor * ColorModulator;
    }

    if (color.a < 0.1) {
        discard;
    }
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
