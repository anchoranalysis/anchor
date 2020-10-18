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
package org.anchoranalysis.image.voxel.iterator.neighbor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.neighborhood.Neighborhood;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Utilities for iterating over the neighboring voxels to a given point.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsNeighbors {

    /**
     * Iterate over each point in the neighborhood of an existing point.
     *
     * <p>It also sets the source in {@code process}.
     *
     * @param sourcePoint the point to iterate over its neighborhood
     * @param neighborhood a definition of what constitutes the neighborhood
     * @param do3D whether to iterate in 2D or 3D
     * @param process is called for each voxel in the neighborhood of the source-point.
     * @return the result after processing each point in the neighborhood
     */
    public static <T> T callEachPointInNeighborhood(
            Point3i sourcePoint,
            Neighborhood neighborhood,
            boolean do3D,
            ProcessVoxelNeighbor<T> process,
            int sourceVal,
            int sourceOffsetXY) {
        process.initSource(sourcePoint, sourceVal, sourceOffsetXY);
        neighborhood.processAllPointsInNeighborhood(do3D, process);
        return process.collectResult();
    }
}
