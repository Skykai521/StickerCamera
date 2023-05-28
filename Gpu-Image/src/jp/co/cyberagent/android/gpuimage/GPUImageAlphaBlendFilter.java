/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.cyberagent.android.gpuimage;

/**
 * Mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
 */
public class GPUImageAlphaBlendFilter extends GPUImageMixBlendFilter {

    public static final String ALPHA_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" + " varying highp vec2 textureCoordinate2;\n" + "\n" + " uniform sampler2D inputImageTexture;\n" + " uniform sampler2D inputImageTexture2;\n" + " \n" + " uniform lowp float mixturePercent;\n" + "\n" + " void main()\n" + " {\n" + "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" + "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" + "\n" + "   gl_FragColor = vec4(mix(textureColor.rgb, textureColor2.rgb, textureColor2.a * mixturePercent), textureColor.a);\n" + " }";

    public GPUImageAlphaBlendFilter() {
        super(ALPHA_BLEND_FRAGMENT_SHADER);
    }

    public GPUImageAlphaBlendFilter(float mix) {
        super(ALPHA_BLEND_FRAGMENT_SHADER, mix);
    }
}
