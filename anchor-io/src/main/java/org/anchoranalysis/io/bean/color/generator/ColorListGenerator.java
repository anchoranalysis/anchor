/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.RGBColorBean;

// If the list is too small for size, then we extend with the final item
public class ColorListGenerator extends ColorSetGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<RGBColorBean> list;
    // END BEAN PROPERTIES

    @Override
    public ColorList generateColors(int numberColors) throws OperationFailedException {

        try {
            ColorList out = new ColorList();
            for (RGBColorBean item : list) {
                out.add(item.duplicateBean().rgbColor());
            }

            while (out.size() < numberColors) {
                out.add(list.get(list.size() - 1).duplicateBean().rgbColor());
            }

            return out;

        } catch (BeanDuplicateException e) {
            throw new OperationFailedException(e);
        }
    }
}
