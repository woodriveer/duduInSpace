#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;

void main() {
    vec2 uv = v_texCoords;
    
    // 1. Chromatic Aberration
    float amount = 0.002;
    vec4 r = texture2D(u_texture, uv + vec2(amount, 0.0));
    vec4 g = texture2D(u_texture, uv);
    vec4 b = texture2D(u_texture, uv - vec2(amount, 0.0));
    vec4 color = vec4(r.r, g.g, b.b, g.a);

    // 2. Scanlines
    float scanline = sin(uv.y * u_resolution.y * 1.5 + u_time * 5.0) * 0.1;
    color.rgb -= scanline;

    // 3. Simple CRT Curve (Vignette)
    float vignette = 1.0 - length(uv - 0.5) * 0.5;
    color.rgb *= vignette;

    gl_FragColor = v_color * color;
}
