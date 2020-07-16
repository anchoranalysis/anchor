/* (C)2020 */
package org.anchoranalysis.image.object.factory.unionfind;

import java.nio.IntBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbor;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighborAbsoluteWithSlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighborFactory;
import org.anchoranalysis.image.voxel.neighborhood.Neighborhood;
import org.anchoranalysis.image.voxel.neighborhood.NeighborhoodFactory;
import org.jgrapht.alg.util.UnionFind;

final class MergeWithNeighbors {

    private static class PointTester
            extends ProcessVoxelNeighborAbsoluteWithSlidingBuffer<Integer> {

        private int minLabel;

        private UnionFind<Integer> unionIndex;

        public PointTester(SlidingBuffer<IntBuffer> slidingIndex, UnionFind<Integer> unionIndex) {
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
    private final SlidingBuffer<IntBuffer> slidingIndex;
    private final UnionFind<Integer> unionIndex;

    // Without mask
    public MergeWithNeighbors(
            VoxelBox<IntBuffer> indexBuffer,
            UnionFind<Integer> unionIndex,
            boolean do3D,
            boolean bigNeighborhood) {
        this.do3D = do3D;
        this.slidingIndex = new SlidingBuffer<>(indexBuffer);
        this.unionIndex = unionIndex;

        neighborhood = NeighborhoodFactory.of(bigNeighborhood);

        this.process =
                ProcessVoxelNeighborFactory.withinExtent(new PointTester(slidingIndex, unionIndex));
    }

    // Calculates the minimum label of the neighbors, making sure to merge any different values
    //   -1 indicates that there is no indexed neighbor
    public int calcMinNeighborLabel(Point3i point, int exstVal, int indxBuffer) {
        return IterateVoxels.callEachPointInNeighborhood(
                point, neighborhood, do3D, process, exstVal, indxBuffer);
    }

    public void shift() {
        slidingIndex.shift();
    }

    public void addElement(Integer element) {
        unionIndex.addElement(element);
    }
}
