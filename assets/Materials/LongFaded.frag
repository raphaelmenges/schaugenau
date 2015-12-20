#if defined(HAS_GLOWMAP) || defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

#if defined(DISCARD_ALPHA)
    uniform float m_AlphaDiscardThreshold;
#endif

uniform vec4 m_Color;
uniform sampler2D m_ColorMap;
uniform sampler2D m_LightMap;

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;

varying float pass_z;

void main(){
	// fog
	float fade = min(max(0.0f,-5.0f-(pass_z/200.0f)),1.0f);
    vec4 fogColor = vec4(0.40234375f, 0.7734375f, 0.2265625f, 1.0f);
    vec4 color = texture2D(m_ColorMap, texCoord1);
    
    #if defined(DISCARD_ALPHA)
        if(color.a < m_AlphaDiscardThreshold){
           discard;
        }
    #endif
    
    #ifdef HAS_LIGHTMAP
    	color.rgb *= texture2D(m_LightMap, texCoord2).rgb;
    #endif

    color = (1.0f-fade) * color + (fade) * fogColor;     

    #ifdef HAS_COLOR
        color *= m_Color;
    #endif

    gl_FragColor = color;
    ;
}