/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;

@AllArgsConstructor
@NoArgsConstructor
public class ShuffleColorSetGenerator extends ColorSetGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ColorSetGenerator source;
    // END BEAN PROPERTIES

    @Override
    public ColorList generateColors(int numberColors) throws OperationFailedException {
        ColorList lst = source.generateColors(numberColors);
        lst.shuffle();
        return lst;
    }
}
