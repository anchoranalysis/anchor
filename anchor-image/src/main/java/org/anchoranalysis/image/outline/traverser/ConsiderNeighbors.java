/* (C)2020 */
package org.anchoranalysis.image.outline.traverser;

import java.nio.ByteBuffer;
import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

@AllArgsConstructor
class ConsiderNeighbors {

    private final ObjectMask objectOutline;
    private final int distance;
    private final List<Point3iWithDistance> localQueue;
    private final ConsiderVisit icv;
    private final Point3i point;

    public void considerNeighbors(boolean useZ, boolean bigNeighborhood) {

        // Look for any neighboring pixels and call them recursively
        considerAndQueue(1, 0, 0);

        considerAndQueue(-1, 0, 0);
        considerAndQueue(0, 1, 0);
        considerAndQueue(0, -1, 0);

        if (useZ) {
            considerAndQueue(0, 0, -1);
            considerAndQueue(0, 0, 1);
        }

        if (bigNeighborhood) {
            considerAndQueue(-1, -1, 0);
            considerAndQueue(-1, 1, 0);
            considerAndQueue(1, -1, 0);
            considerAndQueue(1, 1, 0);

            if (useZ) {
                considerAndQueue(-1, -1, -1);
                considerAndQueue(-1, 1, -1);
                considerAndQueue(1, -1, -1);
                considerAndQueue(1, 1, -1);

                considerAndQueue(-1, -1, 1);
                considerAndQueue(-1, 1, 1);
                considerAndQueue(1, -1, 1);
                considerAndQueue(1, 1, 1);
            }
        }
    }

    private void considerAndQueue(int xShift, int yShift, int zShift) {
        considerVisitAndQueueNeighborPoint(
                icv,
                new Point3i(point.getX() + xShift, point.getY() + yShift, point.getZ() + zShift),
                distance,
                localQueue);
    }

    private boolean considerVisitAndQueueNeighborPoint(
            ConsiderVisit considerVisit,
            Point3i point,
            int distance,
            List<Point3iWithDistance> points) {

        int distanceNew = distance + 1;

        if (considerVisitMarkRaster(considerVisit, point, distanceNew, objectOutline)) {
            points.add(new Point3iWithDistance(point, distanceNew));
            return true;
        }

        return false;
    }

    public static boolean considerVisitMarkRaster(
            ConsiderVisit considerVisit, Point3i point, int distance, ObjectMask outline) {

        VoxelBox<ByteBuffer> vb = outline.getVoxelBox();
        BinaryValuesByte bvb = outline.getBinaryValuesByte();

        if (!vb.extent().contains(point)) {
            return false;
        }

        ByteBuffer bb = vb.getPixelsForPlane(point.getZ()).buffer();
        int offset = vb.extent().offset(point.getX(), point.getY());

        // Check if the buffer allows us to read the pixel
        if (bb.get(offset) == bvb.getOffByte()) {
            return false;
        }

        // We do the check after first verifying that visiting the pixel is possible from the buffer
        if (!considerVisit.considerVisit(point, distance)) {
            return false;
        }

        bb.put(offset, bvb.getOffByte());

        return true;
    }
}
