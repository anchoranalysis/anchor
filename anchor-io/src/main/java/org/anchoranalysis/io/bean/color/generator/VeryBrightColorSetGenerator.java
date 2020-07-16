/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;

public class VeryBrightColorSetGenerator extends ColorSetGenerator {

    // From http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines
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
    // Previously "#F20056",

    private static RGBColor hex2Rgb(String colorStr) {
        return new RGBColor(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    @Override
    public ColorList generateColors(int numberColors) {

        int hexCodesSize = hexCodes.length;

        ColorList out = new ColorList();
        for (int i = 0; i < numberColors; i++) {
            int hexCodeIndex = i % hexCodesSize;
            out.add(hex2Rgb(hexCodes[hexCodeIndex]));
        }

        return out;
    }
}
