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
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.BufferRetriever;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Walks in X, Y and Z directions from a point, executing a {@link Runnable} if the neighbour
 * satisfies conditions.
 *
 * <p>The conditions are:
 *
 * <ul>
 *   <li>An associated buffer for the point must have an OFF value, <i>or</i> if its outside the
 *       scene, the {@link KernelApplicationParameters} parameters must indicate off.
 *   <li>An additional predicate around the point.
 * </ul>
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class WalkRunnable {

    /** The cursor used for iterating through points. */
    private final KernelPointCursor point;

    /**
     * An additional predicate that must also be satisfied, as the <b>second</b> condition (see
     * class description).
     */
    private final Predicate<Point3i> additionalPredicate;

    /** Executed whenever an neighbor satisfied the conditions. */
    private final Runnable executeWhenSatisfied;

    /**
     * Walks in X and Y direction, and Z direction if enabled.
     *
     * @param buffer the buffer associated with the current slice
     * @param bufferRetriever a means of retrieving buffers for other slices, accepting a relative
     *     shift compared to current slice (e.g. -1, +1) etc.
     */
    public void walk(UnsignedByteBuffer buffer, BufferRetriever bufferRetriever) {
        walkX(buffer);

        walkY(buffer);

        if (point.isUseZ()) {
            walkZ(bufferRetriever);
        }
    }

    /** Walk in the x-dimension seeing if neighbors qualify. */
    private void walkX(UnsignedByteBuffer buffer) {
        point.decrementX();

        executeIf(point.nonNegativeX(), () -> buffer);

        point.incrementXTwice();

        executeIf(point.lessThanMaxX(), () -> buffer);

        point.decrementX();
    }

    /** Walk in the y-dimension seeing if neighbors qualify. */
    private void walkY(UnsignedByteBuffer buffer) {
        point.decrementY();

        executeIf(point.nonNegativeY(), () -> buffer);

        point.incrementYTwice();

        executeIf(point.lessThanMaxY(), () -> buffer);

        point.decrementY();
    }

    /** Walk in the z-dimension seeing if neighbors qualify. */
    private void walkZ(BufferRetriever bufferRetriever) {
        point.decrementZ();
        executeIfZDirection(bufferRetriever, -1);
        point.incrementZTwice();
        executeIfZDirection(bufferRetriever, +1);
        point.decrementZ();
    }

    /** Does a neighbor voxel in <b>a specific Z-direction</b> qualify the voxel? */
    private void executeIfZDirection(BufferRetriever bufferRetriever, int zShift) {
        Optional<UnsignedByteBuffer> buffer = bufferRetriever.getLocal(zShift);
        executeIf(buffer.isPresent(), buffer::get);
    }

    private void executeIf(boolean inside, Supplier<UnsignedByteBuffer> buffer) {
        if (inside) {
            if (point.isBufferOff(buffer.get()) && additionalPredicate.test(point.getPoint())) {
                executeWhenSatisfied.run();
            }
        } else {
            if (point.isOutsideOffUnignored() && additionalPredicate.test(point.getPoint())) {
                executeWhenSatisfied.run();
            }
        }
    }
}
