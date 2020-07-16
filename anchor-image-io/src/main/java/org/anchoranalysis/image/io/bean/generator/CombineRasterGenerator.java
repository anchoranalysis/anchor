/* (C)2020 */
package org.anchoranalysis.image.io.bean.generator;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterBean;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;

/**
 * Combines a number of generators of Raster images by tiling their outputs together
 *
 * <p>The order of generators is left to right, then top to bottom
 *
 * @author Owen Feehan
 * @param <T> iteration-type
 */
public class CombineRasterGenerator<T> extends AnchorBean<CombineRasterGenerator<T>> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ArrangeRasterBean arrangeRaster;

    // A list of all generators to be tiled (left to right, then top to bottom)
    @BeanField @Getter @Setter
    private List<IterableObjectGenerator<T, Stack>> generatorList = new ArrayList<>();
    // END BEAN PROPERTIES

    public void add(IterableObjectGenerator<T, Stack> generator) {
        generatorList.add(generator);
    }

    public IterableObjectGenerator<T, Stack> createGenerator() {
        return new CombineGenerator<>(arrangeRaster, generatorList);
    }

    @Override
    public String getBeanDscr() {
        return getBeanName();
    }
}
