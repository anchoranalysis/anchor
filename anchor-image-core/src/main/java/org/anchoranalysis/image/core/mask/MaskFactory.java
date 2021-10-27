/*-
 * #%L
 * anchor-image
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
package org.anchoranalysis.image.core.mask;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Helper routines to create new instances of {@link Mask}
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskFactory {

    /**
     * Creates a binary-mask for specific dimensions with all voxels set to <i>off</i>.
     *
     * <p>The mask uses default binary-values of <i>off</i> (0) and <i>on</i> (255)
     *
     * @param dimensions the dimensions to create the mask for
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOff(Dimensions dimensions) {
        return createMaskOff(dimensions, BinaryValues.getDefault());
    }

    /**
     * Creates a binary-mask for specific dimensions with all voxels set to <i>off</i>.
     *
     * @param dimensions the dimensions to create the mask for
     * @param binaryValues binary-values
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOff(Dimensions dimensions, BinaryValues binaryValues) {
        Mask mask =
                new Mask(
                        ChannelFactory.instance()
                                .create(dimensions, UnsignedByteVoxelType.INSTANCE),
                        binaryValues);
        // By default the voxels are 0. If <i>off</i> value is not 0, it needs to be explicitly
        // assigned.
        if (binaryValues.getOffInt() != 0) {
            mask.assignOff().toAll();
        }
        return mask;
    }

    /**
     * Creates a binary-mask for specific dimensions with all voxels set to <i>on</i>.
     *
     * <p>The mask uses default binary-values of <i>off</i> (0) and <i>on</i> (255)
     *
     * @param dimensions the dimensions to create the mask for
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOn(Dimensions dimensions) {
        return createMaskOn(dimensions, BinaryValues.getDefault());
    }

    /**
     * Creates a binary-mask for specific dimensions with all voxels set to ON
     *
     * @param dimensions the dimensions to create the mask for
     * @param binaryValues binary-values
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOn(Dimensions dimensions, BinaryValues binaryValues) {
        Mask mask = createMaskOff(dimensions, binaryValues);
        mask.assignOn().toAll();
        return mask;
    }
}
