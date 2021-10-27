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

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.BufferRetriever;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;

/**
 * Walks in X, Y and Z directions from a point, test a {@link NeighborPredicate} to determine if a
 * neighbor satisfies conditions.
 *
 * <p>As soon as any neighbor matches the predicate, a true value is returned for the voxel. A false
 * is returned only if no neighbors match the predicate.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class WalkPredicate {

    /** The cursor used for iterating through points. */
    private final KernelPointCursor point;

    /** Whether a neighbor qualifies or not. */
    private final NeighborPredicate predicate;

    /**
     * Whether to use a big-neighborhood or not.
     *
     * <p>if true, a big neighborhood is used 2D-plane (8-connected instead of 4-connected), but not
     * in Z-direction (remains 2-connected).
     */
    private final boolean bigNeighborhood;

    /**
     * Do any neighboring voxels in <i>any</i> direction satisfy the predicate?
     *
     * @param buffer the buffer associated with the current slice
     * @param bufferRetriever a means of retrieving buffers for other slices, accepting a relative
     *     shift compared to current slice (e.g. -1, +1) etc.
     * @return true iff at least one neighbor voxel in <i>any</i> direction satisfies the predicate.
     */
    public boolean walk(UnsignedByteBuffer buffer, BufferRetriever bufferRetriever) {
        return walkX(buffer)
                || walkY(buffer)
                || walkZ(bufferRetriever)
                || maybeQualifyFromBigNeighborhood(buffer);
    }

    /** Do any neighboring voxels in X direction qualify the voxel? */
    private boolean walkX(UnsignedByteBuffer buffer) {
        // We walk up and down in x
        point.decrementX();

        if (testIf(point.nonNegativeX(), () -> buffer, 0)) {
            point.incrementX();
            return true;
        }

        point.incrementXTwice();

        try {
            if (testIf(point.lessThanMaxX(), () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementX();
        }

        return false;
    }

    /** Do any neighboring voxels in Y direction qualify the voxel? */
    private boolean walkY(UnsignedByteBuffer buffer) {
        point.decrementY();

        if (testIf(point.nonNegativeY(), () -> buffer, 0)) {
            point.incrementY();
            return true;
        }

        point.incrementYTwice();

        try {
            if (testIf(point.lessThanMaxY(), () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementY();
        }

        return false;
    }

    /** Do any neighboring voxels in Z direction qualify the voxel? */
    private boolean walkZ(BufferRetriever bufferRetriever) {
        if (point.isUseZ()) {
            return qualifyFromZDirection(bufferRetriever, -1)
                    || qualifyFromZDirection(bufferRetriever, +1);
        } else {
            return false;
        }
    }

    /**
     * If big-neighbor is enabled, do any voxels from the big neighborhood (not already covered)
     * qualify the voxel?
     */
    private boolean maybeQualifyFromBigNeighborhood(UnsignedByteBuffer buffer) {
        return bigNeighborhood && qualifyFromBigNeighborhood(buffer);
    }

    /** Do any voxels from the big neighborhood (not already covered) qualify the voxel? */
    private boolean qualifyFromBigNeighborhood(UnsignedByteBuffer buffer) {

        // x-1, y-1
        point.decrementX();
        point.decrementY();

        if (testIf(point.nonNegativeX() && point.nonNegativeY(), () -> buffer, 0)) {
            point.incrementX();
            point.incrementY();
            return true;
        }

        // x-1, y+1
        point.incrementYTwice();

        if (testIf(point.nonNegativeX() && point.lessThanMaxY(), () -> buffer, 0)) {
            point.incrementX();
            point.decrementY();
            return true;
        }

        // x+1, y+1
        point.incrementXTwice();

        if (testIf(point.lessThanMaxX() && point.lessThanMaxY(), () -> buffer, 0)) {
            point.decrementX();
            point.decrementY();
            return true;
        }

        // x+1, y-1
        point.decrementYTwice();

        try {
            if (testIf(point.lessThanMaxX() && point.nonNegativeY(), () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementX();
            point.incrementY();
        }

        return false;
    }

    /** Does a neighbor voxel in <b>a specific Z-direction</b> qualify the voxel? */
    private boolean qualifyFromZDirection(BufferRetriever bufferRetriever, int zShift) {
        Optional<UnsignedByteBuffer> buffer = bufferRetriever.getLocal(zShift);
        return testIf(buffer.isPresent(), buffer::get, zShift);
    }

    private boolean testIf(boolean inside, Supplier<UnsignedByteBuffer> buffer, int zShift) {
        return predicate.test(inside, point, buffer, zShift);
    }
}
