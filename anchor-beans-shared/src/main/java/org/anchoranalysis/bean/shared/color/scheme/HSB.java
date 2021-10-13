/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.bean.shared.color.scheme;

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;

/**
 * Creates a sequence of colors by varying the hue-component in a <a
 * href="https://georeference.org/doc/colors_as_hue_saturation_and_brightness.htm">HSB color
 * model</a>.
 *
 * <p>The saturation and brightness are held constant at {@code 0.5}.
 *
 * <p>The range of hues are partitioned evenly by the number of colors desired.
 *
 * @author Owen Feehan
 */
public class HSB extends ColorScheme {

    private static final float SATURATION = 0.5f;

    private static final float BRIGHTNESS = 0.5f;

    @Override
    public ColorList createList(int size) {

        ColorList out = new ColorList();

        for (int i = 0; i < size; i++) {
            float hue = ((float) i) / size;
            int rgb = java.awt.Color.HSBtoRGB(hue, SATURATION, BRIGHTNESS);
            out.add(new RGBColor(rgb));
        }

        return out;
    }
}
