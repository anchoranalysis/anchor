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
package org.anchoranalysis.image.voxel.iterator.neighbor.kernel;

import java.util.function.Supplier;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;

/**
 * A predicate on whether a neighbor satisfies a condition in relation to a particular point that it neighbors.
 * 
 * @author Owen Feehan
 *
 */
public interface NeighborPredicate {

    /**
     * Tests if a neighbor satisfies a condition.
     *
     * @param inside if true, the point is inside the image, and the buffer buffer is read. if
     *     false, the point is treated as outside the image.
     * @param point the neighboring point.
     * @param buffer a buffer describing the slice on which the neighbor lies.
     * @param zShift the distance in z-dimension of the neighboring point from the source-point
     *     (around which neighbors exist).
     * @return true if the condition is satisfied, false otherwise.
     */
    boolean test(
            boolean inside,
            KernelPointCursor point,
            Supplier<UnsignedByteBuffer> buffer,
            int zShift);
}
