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

package org.anchoranalysis.image.io.object.output.grayscale;

import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.core.mask.MaskFromObjects;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.channel.output.ChannelGenerator;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes objects merged together as a mask.
 *
 * @author Owen Feehan
 */
public class ObjectsMergedAsMaskGenerator extends ObjectsAsGrayscaleGenerator {

    /**
     * Create with particular size for the mask image.
     *
     * @param dimensions the {@link Dimensions} of the image in which the mask should be created
     *     (sufficiently large to contain any possible merged objects).
     */
    public ObjectsMergedAsMaskGenerator(Dimensions dimensions) {
        super(dimensions);
    }

    @Override
    public Stack transform(ObjectCollection element) throws OutputWriteFailedException {
        Mask mask =
                MaskFromObjects.createFromObjects(
                        element, dimensions(), BinaryValuesInt.getDefault());
        return new ChannelGenerator().transform(mask.channel());
    }
}
