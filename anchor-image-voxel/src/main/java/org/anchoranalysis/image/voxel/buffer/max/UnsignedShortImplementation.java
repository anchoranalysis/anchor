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

import org.anchoranalysis.core.functional.unchecked.BiIntPredicate;
import org.anchoranalysis.image.voxel.buffer.primitive.PrimitiveConverter;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.spatial.box.Extent;

class UnsignedShortImplementation extends MaybeReplaceBufferBase<UnsignedShortBuffer> {

    /** The predicate to apply to determine, whether to replace a value or not. */
    private final BiIntPredicate predicate;

    public UnsignedShortImplementation(Extent extent, BiIntPredicate predicate) {
        super(extent, VoxelsFactory.getUnsignedShort());
        this.predicate = predicate;
    }

    @Override
    protected void maybeReplaceCurrentBufferPosition(
            UnsignedShortBuffer buffer, UnsignedShortBuffer projection) {
        short inPixel = buffer.getRaw();
        if (predicate.test(
                PrimitiveConverter.unsignedShortToInt(inPixel), projection.getUnsigned())) {
            projection.putRaw(projection.position() - 1, inPixel);
        }
    }

    @Override
    protected void assignCurrentBufferPosition(
            UnsignedShortBuffer buffer, UnsignedShortBuffer projection) {
        projection.putRaw(buffer.getRaw());
    }
}
