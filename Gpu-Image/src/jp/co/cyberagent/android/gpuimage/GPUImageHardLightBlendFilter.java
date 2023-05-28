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

public class GPUImageHardLightBlendFilter extends GPUImageTwoInputFilter {

    public static final String HARD_LIGHT_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" + " varying highp vec2 textureCoordinate2;\n" + "\n" + " uniform sampler2D inputImageTexture;\n" + " uniform sampler2D inputImageTexture2;\n" + "\n" + " const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" + "\n" + " void main()\n" + " {\n" + "     mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" + "     mediump vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" + "\n" + "     highp float ra;\n" + "     if (2.0 * overlay.r < overlay.a) {\n" + "         ra = 2.0 * overlay.r * base.r + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" + "     } else {\n" + "         ra = overlay.a * base.a - 2.0 * (base.a - base.r) * (overlay.a - overlay.r) + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" + "     }\n" + "     \n" + "     highp float ga;\n" + "     if (2.0 * overlay.g < overlay.a) {\n" + "         ga = 2.0 * overlay.g * base.g + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);\n" + "     } else {\n" + "         ga = overlay.a * base.a - 2.0 * (base.a - base.g) * (overlay.a - overlay.g) + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);\n" + "     }\n" + "     \n" + "     highp float ba;\n" + "     if (2.0 * overlay.b < overlay.a) {\n" + "         ba = 2.0 * overlay.b * base.b + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);\n" + "     } else {\n" + "         ba = overlay.a * base.a - 2.0 * (base.a - base.b) * (overlay.a - overlay.b) + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);\n" + "     }\n" + "     \n" + "     gl_FragColor = vec4(ra, ga, ba, 1.0);\n" + " }";

    public GPUImageHardLightBlendFilter() {
        super(HARD_LIGHT_BLEND_FRAGMENT_SHADER);
    }
}
