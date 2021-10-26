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

package org.anchoranalysis.image.voxel.kernel.morphological;

import java.util.function.Supplier;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.neighbor.kernel.WalkPredicate;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

/**
 * A parent class for any {@link BinaryKernel} that implements a morphological operation.
 *
 * @author Owen Feehan
 */
public abstract class BinaryKernelMorphological extends BinaryKernel {

    // START REQUIRED ARGUMENTS
    /**
     * If true, a big neighborhood is used 2D-plane (8-connected instead of 4-connected), but not in
     * Z-direction (remains always 2-connected).
     */
    private final boolean bigNeighborhood;

    /**
     * The outcome that should occur for the kernel <b>if no neighbor fulfills the condition</b>
     * required by the kernel.
     */
    private final boolean unqualifiedOutcome;

    /**
     * The outcome that should occur for the kernel <b>if at least one neighbor fulfills the
     * condition</b> required by the kernel.
     */
    private final boolean qualifiedOutcome;

    /** If the first-check fails, this outcome is returned. */
    private final boolean failedFirstCheckOutcome;
    // END REQUIRED ARGUMENTS

    private LocalSlices slices;

    /**
     * Creates with a flag for big-neighborhood, and boolean outcomes for certain cases.
     *
     * @param bigNeighborhood if true, a big neighborhood is used 2D-plane (8-connected instead of
     *     4-connected), but not in Z-direction (remains always 2-connected).
     * @param unqualifiedOutcome the (negative) outcome that occurs if no neighbor qualifies
     *     (satisfies a condition). The positive outcome is assumed to be the complement of this.
     * @param failedFirstCheckOutcome if the first-check fails, this outcome is returned.
     */
    protected BinaryKernelMorphological(
            boolean bigNeighborhood, boolean unqualifiedOutcome, boolean failedFirstCheckOutcome) {
        super(3);
        this.unqualifiedOutcome = unqualifiedOutcome;
        this.qualifiedOutcome = !unqualifiedOutcome;
        this.bigNeighborhood = bigNeighborhood;
        this.failedFirstCheckOutcome = failedFirstCheckOutcome;
    }

    @Override
    public void notifyBuffer(LocalSlices slices, int sliceIndex) {
        this.slices = slices;
    }

    @Override
    public boolean calculateAt(KernelPointCursor point) {

        UnsignedByteBuffer buffer = slices.getLocal(0).get(); // NOSONAR

        if (!firstCheck(point, buffer)) {
            return failedFirstCheckOutcome;
        }

        WalkPredicate walker = new WalkPredicate(point, this::doesNeighborQualify, bigNeighborhood);
        if (walker.walk(buffer, slices)) {
            return qualifiedOutcome;
        } else {
            return unqualifiedOutcome;
        }
    }

    /**
     * The first check done on the kernel center-point, before checking any neighbors.
     *
     * @param point a kernel focused on a particular point.
     * @param buffer the associated buffer.
     * @return true if the check passed, and false otherwise.
     */
    protected abstract boolean firstCheck(KernelPointCursor point, UnsignedByteBuffer buffer);

    /**
     * Does a particular neighboring-point satisfy the conditions.
     *
     * @param inside true iff the neighboring-point is inside the scene.
     * @param point the point.
     * @param buffer the associated buffer.
     * @param zShift the buffer associated with the current point.
     * @return true if the neighboring-point satisfied the conditions.
     */
    protected abstract boolean doesNeighborQualify(
            boolean inside,
            KernelPointCursor point,
            Supplier<UnsignedByteBuffer> buffer,
            int zShift);
}
