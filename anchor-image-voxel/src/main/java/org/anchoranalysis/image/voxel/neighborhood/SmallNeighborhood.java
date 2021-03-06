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

package org.anchoranalysis.image.voxel.neighborhood;

import org.anchoranalysis.image.voxel.iterator.neighbor.ProcessVoxelNeighbor;

// 4 or 6 connectivity
final class SmallNeighborhood implements Neighborhood {

    @Override
    public void processNeighborhoodPoints(boolean useZ, ProcessVoxelNeighbor<?> process) {

        int numDims = useZ ? 3 : 2;

        process.notifyChangeZ(0);

        for (int d = 0; d < numDims; d++) {
            for (int j = -1; j <= 1; j += 2) {

                // If it's the z dimension we notify change in the Z value
                if (d == 2) {
                    if (process.notifyChangeZ(j)) {
                        processDimension(process, j, d);
                    }
                } else {
                    processDimension(process, j, d);
                }
            }
        }
    }

    private final void processDimension(ProcessVoxelNeighbor<?> process, int j, int d) {
        switch (d) {
            case 0:
                process.processPoint(j, 0);
                break;
            case 1:
                process.processPoint(0, j);
                break;
            case 2:
                process.processPoint(0, 0);
                break;
            default:
                assert false;
        }
    }
}
