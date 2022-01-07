/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.interpolator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;

/**
 * Defines a particular type of interpolation method that can be used for resizing images.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Interpolator extends AnchorBean<Interpolator> {

    /** A memoized instance of the {@link VoxelsResizer} that was created. */
    private VoxelsResizer memoized;

    /**
     * Memoizes a call to {@link #createVoxelsResizer()}.
     *
     * @return the memoized call.
     */
    public VoxelsResizer voxelsResizer() {
        if (memoized == null) {
            memoized = createVoxelsResizer();
        }
        return memoized;
    }

    /**
     * Create a {@link VoxelsResizer} that can be used for resizing voxel-buffers.
     *
     * @return an instance of {@link VoxelsResizer}.
     */
    protected abstract VoxelsResizer createVoxelsResizer();
}
