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

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbor;

/**
 * Provides either 8-connectivity or 26-connectivity as an neighborhood.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
final class BigNeighborhood implements Neighborhood {

    private final boolean includeCenterPoint;

    public BigNeighborhood() {
        this.includeCenterPoint = false;
    }

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public void processAllPointsInNeighborhood(boolean do3D, ProcessVoxelNeighbor<?> process) {

        if (do3D) {

            for (int z = -1; z <= 1; z++) {

                if (!process.notifyChangeZ(z)) {
                    continue;
                }

                for (int y = -1; y <= 1; y++) {
                    for (int x = -1; x <= 1; x++) {
                        if (includeCenterPoint || x != 0 || y != 0 || z != 0) {
                            process.processPoint(x, y);
                        }
                    }
                }
            }

        } else {

            if (!process.notifyChangeZ(0)) {
                return;
            }

            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    if (includeCenterPoint || x != 0 || y != 0) {
                        process.processPoint(x, y);
                    }
                }
            }
        }
    }
}
