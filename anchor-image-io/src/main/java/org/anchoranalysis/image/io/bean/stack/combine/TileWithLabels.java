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

package org.anchoranalysis.image.io.bean.stack.combine;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.input.TileStackProviders;

/**
 * Tiles a number of {@link StackProviderWithLabel}s as a single {@link StackProvider}.
 *
 * @author Owen Feehan
 */
public class TileWithLabels extends StackProvider {

    // START BEAN PROPERTIES
    /** The list of {@link StackProviderWithLabel}s that are tiled. */
    @BeanField @Getter @Setter private List<StackProviderWithLabel> list = Arrays.asList();

    /**
     * How many columns when tiling, so long as there are sufficient {@link
     * StackProviderWithLabel}s.
     */
    @BeanField @Getter @Setter private int numColumns = 3;

    /**
     * When true, the voxel-data-type of the created image is <i>unsigned short</i>, otherwise
     * <i>unsigned byte</i>.
     */
    @BeanField @Getter @Setter boolean createShort;

    /**
     * When true, the label is repeated across all z-slices in the stack. when false, it appears on
     * only one z-slice.
     */
    @BeanField @Getter @Setter boolean expandLabelZ = false;

    // END BEAN PROPERTIES

    @Override
    public Stack get() throws ProvisionFailedException {
        StackProvider arrangeRaster =
                TileStackProviders.tile(list, numColumns, createShort, expandLabelZ);
        try {
            arrangeRaster.initializeRecursive(getInitialization(), getLogger());
        } catch (InitializeException e) {
            throw new ProvisionFailedException(e);
        }
        return arrangeRaster.get();
    }
}
