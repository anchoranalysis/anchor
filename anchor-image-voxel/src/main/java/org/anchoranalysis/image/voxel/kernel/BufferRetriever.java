/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.kernel;

import java.util.Optional;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Retrieves a buffer corresponding to a relative index in the z-dimension.
 * 
 * @author Owen Feehan
 *
 */
public interface BufferRetriever {

    /**
     * Get a buffer at a particular index in the z-dimension.
     * 
     * The index is relative to a current position e.g. {@code -1, -2, +1, +2} etc.
     *
     * <p>If an invalid index is requested, then {@link Optional#empty} is returned.
     *
     * @param relativeZIndex the shift in the z index relative to the current z-slice's index
     * @return the buffer corresponding to the z-index, if it exists.
     */
    Optional<UnsignedByteBuffer> getLocal(int relativeZIndex);
}
