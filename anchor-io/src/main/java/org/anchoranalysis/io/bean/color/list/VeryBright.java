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

package org.anchoranalysis.io.bean.color.list;

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;

/**
 * Creates a list by repeating a defined list of very bright colors.
 *
 * <p>Thanks to the <a href="http://tango-project.org/Tango_Icon_Theme_Guidelines/">Tango project's
 * theme guidelines</a>.
 *
 * @author Owen Feehan
 */
public class VeryBright extends ColorListFactory {

    private static String[] hexCodes =
            new String[] {
                "#fce94f", // Butter
                "#fcaf3e", // Orange
                "#e9b96e", // Chocolate
                "#ad7fa8", // Plum
                "#edd400", // Butter
                "#f57900", // Orange
                "#c17d11", // Chocolate
                "#75507b", // Plum
                "#c4a000", // Butter
                "#ce5c00", // Orange
                "#8f5902", // Chocolate
                "#5ce566", // Plum
                "#692DAC", "#FF0000", "#00FF00", "#0000FF", "#6600CC", // Purple
                "#F20056", "#AAF200", "#33FF99", // Greenish-blue
                "#FFFF33", // Yellow
                "#FF9900", // Orange
                "#33FFFF"
            };

    @Override
    public ColorList create(int size) {

        int hexCodesSize = hexCodes.length;

        ColorList out = new ColorList();
        for (int i = 0; i < size; i++) {
            int hexCodeIndex = i % hexCodesSize;
            out.add(hex2Rgb(hexCodes[hexCodeIndex]));
        }

        return out;
    }

    private static RGBColor hex2Rgb(String colorStr) {
        return new RGBColor(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }
}
