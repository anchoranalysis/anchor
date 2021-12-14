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

package org.anchoranalysis.image.voxel.resizer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates instances of {@link VoxelsResizer} to match particular circumstances.
 *
 * <p>This is a <i>singleton</i> class.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsResizerFactory {

    private static VoxelsResizerFactory instance = null;

    private static final VoxelsResizer NO_INTERPOLATOR = new VoxelsResizerNone();

    /**
     * Singleton instance of {@link VoxelsResizerFactory}.
     *
     * @return a single instance of this class.
     */
    public static VoxelsResizerFactory getInstance() {
        if (instance == null) {
            instance = new VoxelsResizerFactory();
        }
        return instance;
    }

    /**
     * An {@link VoxelsResizer} that is effectively disabled, and performs no interpolation, copying
     * a single (minimal corner) value for each voxel.
     *
     * @return a corresponding interpolator.
     */
    public VoxelsResizer noInterpolation() {
        return NO_INTERPOLATOR;
    }

    /**
     * An {@link VoxelsResizer} that is suitable for resizing a <i>binary</i> raster-image,
     * restricted to <i>two possible intensity values only</i>.
     *
     * @param outOfBoundsValue a value used to represent <i>out-of-bounds</i> voxels to provide
     *     context for the interpolation at boundaries. This should be one of the two permitted
     *     binary states.
     * @return a corresponding interpolator.
     */
    public VoxelsResizer binaryResizing(int outOfBoundsValue) {
        VoxelsResizerImgLib2 interpolator = new NearestNeighbor();
        interpolator.extendWith(outOfBoundsValue);
        return interpolator;
    }
}
