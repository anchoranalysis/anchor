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

package org.anchoranalysis.image.io.bean.stack.provider;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.bean.provider.stack.ArrangeRaster;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.io.bean.stack.StackProviderWithLabel;
import org.anchoranalysis.image.io.stack.TileRasters;
import org.anchoranalysis.image.stack.Stack;

// A short-cut provider for tiling a number of stack providers with labels
public class TileWithLabels extends StackProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<StackProviderWithLabel> list = new ArrayList<>();

    @BeanField @Getter @Setter private int numCols = 3;

    @BeanField @Getter @Setter boolean createShort;

    @BeanField @Getter @Setter boolean scaleLabel = true;

    @BeanField @Getter @Setter
    boolean expandLabelZ = false; // Repeats the label in the z-dimension to match the stackProvider
    // END BEAN PROPERTIES

    @Override
    public Stack create() throws CreateException {
        ArrangeRaster arrangeRaster =
                TileRasters.createStackProvider(
                        list, numCols, createShort, scaleLabel, expandLabelZ);
        try {
            arrangeRaster.initRecursive(getInitializationParameters(), getLogger());
        } catch (InitException e) {
            throw new CreateException(e);
        }
        return arrangeRaster.create();
    }
}
