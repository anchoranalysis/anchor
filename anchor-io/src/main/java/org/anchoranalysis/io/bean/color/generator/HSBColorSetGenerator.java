/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;

public class HSBColorSetGenerator extends ColorSetGenerator {

    @Override
    public ColorList generateColors(int numberColors) {

        ColorList lst = new ColorList();

        for (int i = 0; i < numberColors; i++) {

            float h = ((float) i) / numberColors;
            float s = (float) 0.5;
            float v = (float) 0.5;

            int rgb = java.awt.Color.HSBtoRGB(h, s, v);
            lst.add(new RGBColor(rgb));
        }

        return lst;
    }
}
