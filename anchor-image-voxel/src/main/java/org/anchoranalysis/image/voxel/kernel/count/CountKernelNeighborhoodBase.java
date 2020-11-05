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

package org.anchoranalysis.image.voxel.kernel.count;

import java.util.Optional;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * The number of touching-faces of a voxel with a neighbor
 *
 * <p>i.e. the sum of all faces of a voxel that touch the face of a voxel belonging to a neighboring
 * pixel
 *
 * @author Owen Feehan
 */
public abstract class CountKernelNeighborhoodBase extends CountKernel {

    private boolean useZ;

    private BinaryValuesByte bv;

    private LocalSlices inSlices;

    private Extent extent;

    private boolean outsideAtThreshold = false;
    private boolean ignoreAtThreshold = false;

    /**
     * If true, a voxel is allowed to have more than 1 neighbor. If false, once at least one
     * neighbor is found, it exists with a count of 1
     */
    private boolean multipleMatchesPerVoxel = false;

    // Constructor
    public CountKernelNeighborhoodBase(
            boolean useZ, BinaryValuesByte bv, boolean multipleMatchesPerVoxel) {
        super(3);
        this.useZ = useZ;
        this.bv = bv;
        this.multipleMatchesPerVoxel = multipleMatchesPerVoxel;
    }

    @Override
    public void init(Voxels<UnsignedByteBuffer> in) {
        this.extent = in.extent();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        this.inSlices = inSlices;
    }

    protected abstract boolean isNeighborVoxelAccepted(
            Point3i point, int xShift, int yShift, int zShift, Extent extent);

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public int countAtPosition(int index, Point3i point) {

        UnsignedByteBuffer buffer = inSlices.getLocal(0).get(); // NOSONAR
        Optional<UnsignedByteBuffer> bufferZLess1 = inSlices.getLocal(-1);
        Optional<UnsignedByteBuffer> bufferZPlus1 = inSlices.getLocal(+1);

        int xLength = extent.x();

        int x = point.x();
        int y = point.y();

        if (bv.isOff(buffer.getRaw(index))) {
            return 0;
        }

        int count = 0;

        // We walk up and down in x
        x--;
        index--;
        if (x >= 0) {
            if (bv.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, -1, 0, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold
                    && isNeighborVoxelAccepted(point, -1, 0, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        }

        x += 2;
        index += 2;
        if (x < extent.x()) {
            if (bv.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, +1, 0, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold
                    && isNeighborVoxelAccepted(point, +1, 0, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        }
        index--;

        // We walk up and down in y
        y--;
        index -= xLength;
        if (y >= 0) {
            if (bv.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, 0, -1, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold
                    && isNeighborVoxelAccepted(point, 0, -1, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        }

        y += 2;
        index += (2 * xLength);
        if (y < (extent.y())) {
            if (bv.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, 0, +1, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold
                    && isNeighborVoxelAccepted(point, 0, +1, 0, extent)) {
                if (!multipleMatchesPerVoxel) {
                    return 1;
                }
                count++;
            }
        }
        index -= xLength;

        if (useZ) {
            if (bufferZLess1.isPresent()) {
                if (bv.isOff(bufferZLess1.get().getRaw(index))
                        && isNeighborVoxelAccepted(point, 0, 0, -1, extent)) {
                    if (!multipleMatchesPerVoxel) {
                        return 1;
                    }
                    count++;
                }
            } else {
                if (!ignoreAtThreshold
                        && !outsideAtThreshold
                        && isNeighborVoxelAccepted(point, 0, 0, -1, extent)) {
                    if (!multipleMatchesPerVoxel) {
                        return 1;
                    }
                    count++;
                }
            }

            if (bufferZPlus1.isPresent()) {
                if (bv.isOff(bufferZPlus1.get().getRaw(index))
                        && isNeighborVoxelAccepted(point, 0, 0, +1, extent)) {
                    if (!multipleMatchesPerVoxel) {
                        return 1;
                    }
                    count++;
                }
            } else {
                if (!ignoreAtThreshold
                        && !outsideAtThreshold
                        && isNeighborVoxelAccepted(point, 0, 0, +1, extent)) {
                    if (!multipleMatchesPerVoxel) {
                        return 1;
                    }
                    count++;
                }
            }
        }
        return count;
    }
}
