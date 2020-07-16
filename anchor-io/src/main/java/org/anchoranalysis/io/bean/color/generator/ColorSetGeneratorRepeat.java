/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;

public class ColorSetGeneratorRepeat extends ColorSetGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ColorSetGenerator colorSetGenerator;

    @BeanField @Getter @Setter private int times = 2;
    // END BEAN PROPERTIES

    @Override
    public ColorList generateColors(int numberColors) throws OperationFailedException {

        ColorList cl = colorSetGenerator.generateColors(numberColors);

        ColorList out = new ColorList();
        for (int i = 0; i < times; i++) {
            out.addAll(cl);
        }

        return out;
    }
}
