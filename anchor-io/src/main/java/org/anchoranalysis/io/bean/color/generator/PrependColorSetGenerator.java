/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.RGBColorBean;

@NoArgsConstructor
public class PrependColorSetGenerator extends ColorSetGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ColorSetGenerator source;

    @BeanField @Getter @Setter private RGBColorBean prependColor;
    // END BEAN PROPERTIES

    public PrependColorSetGenerator(ColorSetGenerator source, RGBColor prependColor) {
        super();
        this.source = source;
        this.prependColor = new RGBColorBean(prependColor);
    }

    @Override
    public ColorList generateColors(int numberColors) throws OperationFailedException {
        ColorList lst = source.generateColors(numberColors);
        lst.shuffle();

        lst.add(0, prependColor.rgbColor());

        return lst;
    }
}
