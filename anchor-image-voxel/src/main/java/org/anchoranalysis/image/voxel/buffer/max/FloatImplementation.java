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

package org.anchoranalysis.image.voxel.buffer.max;

import java.nio.FloatBuffer;
import org.anchoranalysis.core.functional.unchecked.BiFloatPredicate;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.spatial.box.Extent;

class FloatImplementation extends MaybeReplaceBufferBase<FloatBuffer> {

    /** The predicate to apply to determine, whether to replace a value or not. */
    private final BiFloatPredicate predicate;

    public FloatImplementation(Extent extent, BiFloatPredicate predicate) {
        super(extent, VoxelsFactory.getFloat());
        this.predicate = predicate;
    }

    @Override
    protected void maybeReplaceCurrentBufferPosition(FloatBuffer buffer, FloatBuffer projection) {
        float inPixel = buffer.get();
        if (predicate.test(inPixel, projection.get())) {
            projection.put(projection.position() - 1, inPixel);
        }
    }

    @Override
    protected void assignCurrentBufferPosition(FloatBuffer buffer, FloatBuffer projection) {
        projection.put(buffer.get());
    }
}
