/*-
 * #%L
 * anchor-image-io
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
    public String descriptionBean() {
        return getBeanName();
    }
}
