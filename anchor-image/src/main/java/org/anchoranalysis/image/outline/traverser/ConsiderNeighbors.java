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

package org.anchoranalysis.image.outline.traverser;

import java.nio.ByteBuffer;
import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;

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

        Voxels<ByteBuffer> vb = outline.getVoxels();
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
