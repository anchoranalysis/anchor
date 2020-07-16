/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.RGBColorBean;

public class ColorSetGeneratorRGBColor extends ColorSetGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private RGBColorBean rgbColor;
    // END BEAN PROPERTIES

    @Override
    public ColorList generateColors(int numberColors) throws OperationFailedException {

        ColorList out = new ColorList();
        for (int i = 0; i < numberColors; i++) {
            out.add(rgbColor.rgbColor());
        }
        return out;
    }
}
