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

package org.anchoranalysis.image.voxel.binary.connected;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.iterator.neighbor.IterateVoxelsNeighbors;
import org.anchoranalysis.image.voxel.iterator.neighbor.ProcessVoxelNeighbor;
import org.anchoranalysis.image.voxel.iterator.neighbor.ProcessVoxelNeighborAbsoluteWithSlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.neighbor.ProcessVoxelNeighborFactory;
import org.anchoranalysis.image.voxel.neighborhood.Neighborhood;
import org.anchoranalysis.image.voxel.neighborhood.NeighborhoodFactory;
import org.anchoranalysis.spatial.point.Point3i;
import org.jgrapht.alg.util.UnionFind;

final class MergeWithNeighbors {

    private static class PointEvaluator
            extends ProcessVoxelNeighborAbsoluteWithSlidingBuffer<Integer> {

        private int minLabel;

        private final UnionFind<Integer> unionIndex;

        public PointEvaluator(
                SlidingBuffer<UnsignedIntBuffer> slidingIndex, UnionFind<Integer> unionIndex) {
            super(slidingIndex);
            this.unionIndex = unionIndex;
        }

        @Override
        public void initSource(int sourceVal, int sourceOffsetXY) {
            super.initSource(sourceVal, sourceOffsetXY);
            minLabel = -1;
        }

        @Override
        public boolean processPoint(int xChange, int yChange, int x1, int y1) {

            int indexVal = getInt(xChange, yChange);

            if (indexVal == 0) {
                return false;
            }

            if (minLabel == -1) {
                minLabel = indexVal;
            } else {

                if (indexVal != minLabel) {

                    unionIndex.union(minLabel, indexVal);

                    if (indexVal < minLabel) {
                        minLabel = indexVal;
                    }
                }
            }
            return true;
        }

        /** The minimum label in the neighborhood */
        @Override
        public Integer collectResult() {
            assert (minLabel != 0);
            return minLabel;
        }
    }

    private final boolean do3D;
    private final ProcessVoxelNeighbor<Integer> process;
    private final Neighborhood neighborhood;
    private final SlidingBuffer<UnsignedIntBuffer> slidingIndex;
    private final UnionFind<Integer> unionIndex;

    /**
     * Creates for voxels containing indices.
     * 
     * @param voxels voxels containing indexes to labels.
     * @param unionIndex a union-find that will contain sets of disjoint indexes. It is updated as objects are merged.
     * @param do3D if true, the neighbours in the Z dimension are also considered, alongside the X and Y dimensions that are always considered.
     * @param bigNeighborhood if true, uses a big neighborhood, otherwise a small neighborhood, as defined in {@link NeighborhoodFactory}.
     */
    public MergeWithNeighbors(
            Voxels<UnsignedIntBuffer> voxels,
            UnionFind<Integer> unionIndex,
            boolean do3D,
            boolean bigNeighborhood) {
        this.do3D = do3D;
        this.slidingIndex = new SlidingBuffer<>(voxels);
        this.unionIndex = unionIndex;

        neighborhood = NeighborhoodFactory.of(bigNeighborhood);

        this.process =
                ProcessVoxelNeighborFactory.withinExtent(
                        new PointEvaluator(slidingIndex, unionIndex));
    }

    /**
     * Calculates the minimum label of the neighbors, making sure to merge any different values.
     * 
     * @param point the point whose neighbors are iterated.
     * @param existingValue the existing (current) intensity value at {@code point}.
     * @param indexBuffer the index in the voxels corresponding to the current point.
     * @return the minimum value across all neighbours, or -1 if no neighbor exists.
     */
    public int minNeighborLabel(Point3i point, int existingValue, int indexBuffer) {
        return IterateVoxelsNeighbors.callEachPointInNeighborhood(
                point, neighborhood, do3D, process, existingValue, indexBuffer);
    }

    public void shift() {
        slidingIndex.shift();
    }

    public void addElement(Integer element) {
        unionIndex.addElement(element);
    }
}
