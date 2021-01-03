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

import java.util.Optional;
import java.util.function.Supplier;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.spatial.Extent;

/**
 * A parent class for any {@link BinaryKernel} that implements a morphological operation.
 * 
 * @author Owen Feehan
 *
 */
public abstract class BinaryKernelMorphological extends BinaryKernel {
    
    // START REQUIRED ARGUMENTS
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
    
    private final boolean failedFirstCheckOutcome;
    // END REQUIRED ARGUMENTS

    protected Extent extent;
    
    private LocalSlices inSlices;
    
    /**
     * Creates with a flag for big-neighborhood, and boolean outcomes for certain cases.
     * 
     * @param bigNeighborhood if true, a big neighborhood is used 2D-plane (8-connected instead of 4-connected), but not in Z-direction (remains 2-connected). 
     * @param unqualifiedOutcome the (negative) outcome that occurs if no neighbor qualifies (fulfills a condition). The positive outcome is assumed to be the complement of this.
     * @param failedFirstCheckOutcome if the first-check fails, this outcome is returned.
     */
    protected BinaryKernelMorphological(boolean bigNeighborhood, boolean unqualifiedOutcome, boolean failedFirstCheckOutcome) {
        super(3);
        this.unqualifiedOutcome = unqualifiedOutcome;
        this.qualifiedOutcome = !unqualifiedOutcome;
        this.bigNeighborhood = bigNeighborhood;
        this.failedFirstCheckOutcome = failedFirstCheckOutcome;
    }

    @Override
    public void init(Voxels<UnsignedByteBuffer> in, KernelApplicationParameters params) {
        this.extent = in.extent();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        this.inSlices = inSlices;
    }
    
    @Override
    public boolean acceptPoint(KernelPointCursor point) {

        UnsignedByteBuffer buffer = getVoxels().getLocal(0).get(); // NOSONAR

        if (!firstCheck(point,buffer)) {
            return failedFirstCheckOutcome;
        }

        if (qualifyFromX(point, buffer) || qualifyFromY(point, buffer) || maybeQualifyFromZ(point) || maybeQualifyFromBigNeighbourhood(point, buffer) ) {
            return qualifiedOutcome;
        } else {
            return unqualifiedOutcome;
        }
    }

    /** 
     * The first check done on the kernel center-point, before checking any neighbors.
     *
     * @return true if the check passed, and false otherwise.
     */
    protected abstract boolean firstCheck(KernelPointCursor point, UnsignedByteBuffer buffer);
    
    protected abstract boolean doesNeighborQualify(
            boolean guard,
            KernelPointCursor point,
            Supplier<UnsignedByteBuffer> buffer,
            int zShift);


    protected LocalSlices getVoxels() {
        return inSlices;
    }
    
    /** Do any neighbor voxels in X direction qualify the voxel? */
    private boolean qualifyFromX(KernelPointCursor point, UnsignedByteBuffer buffer) {
        // We walk up and down in x
        point.decrementX();

        if (doesNeighborQualify(point.nonNegativeX(), point, () -> buffer, 0)) {
            point.incrementX();
            return true;
        }

        point.incrementXTwice();

        try {
            if (doesNeighborQualify(point.lessThanMaxX(), point, () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementX();
        }

        return false;
    }

    /** Do any neighbor voxels in Y direction qualify the voxel? */
    private boolean qualifyFromY(KernelPointCursor point, UnsignedByteBuffer buffer) {
        point.decrementY();

        if (doesNeighborQualify(point.nonNegativeY(), point, () -> buffer, 0)) {
            point.incrementY();
            return true;
        }

        point.incrementYTwice();

        try {
            if (doesNeighborQualify(point.lessThanMaxY(), point, () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementY();
        }

        return false;
    }

    /** Do any neighbor voxels in Z direction qualify the voxel? */
    private boolean maybeQualifyFromZ(KernelPointCursor point) {
        if (point.isUseZ()) {
            return qualifyFromZDirection(point, -1) || qualifyFromZDirection(point, +1);
        } else {
            return false;
        }
    }
    
    /** If big-neighbor is enabled, do any voxels from the big neighborhood (not already covered) qualify the voxel? */
    private boolean maybeQualifyFromBigNeighbourhood(KernelPointCursor point, UnsignedByteBuffer buffer) {
        return bigNeighborhood && qualifyFromBigNeighbourhood(point, buffer);
    }
    
    /** Do any voxels from the big neighborhood (not already covered) qualify the voxel? */
    private boolean qualifyFromBigNeighbourhood(KernelPointCursor point, UnsignedByteBuffer buffer) {
        
        // x-1, y-1
        point.decrementX();
        point.decrementY();

        if (doesNeighborQualify(point.nonNegativeX() && point.nonNegativeY(), point, () -> buffer, 0)) {
            point.incrementX();
            point.incrementY();
            return true;
        }
        
        // x-1, y+1
        point.incrementYTwice();
        
        if (doesNeighborQualify(point.nonNegativeX() && point.lessThanMaxY(), point, () -> buffer, 0)) {
            point.incrementX();
            point.decrementY();
            return true;
        }
        
        // x+1, y+1
        point.incrementXTwice();

        if (doesNeighborQualify(point.lessThanMaxX() && point.lessThanMaxY(), point, () -> buffer, 0)) {
            point.decrementX();
            point.decrementY();
            return true;
        }

        // x+1, y-1
        
        point.decrementYTwice();
        
        try {
            if (doesNeighborQualify(point.lessThanMaxX() && point.nonNegativeY(), point, () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementX();
            point.incrementY();            
        }
        
        return false;
    }

    /** Does a neighbor voxel in <b>a specific Z direction</b> qualify the voxel? */
    private boolean qualifyFromZDirection(KernelPointCursor point, int zShift) {
        Optional<UnsignedByteBuffer> buffer = getVoxels().getLocal(zShift);
        return doesNeighborQualify(buffer.isPresent(), point, buffer::get, zShift);
    }
}
