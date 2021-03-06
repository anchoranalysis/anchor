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

package org.anchoranalysis.image.core.outline.traverser;

import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.point.Point3i;

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
                new Point3i(point.x() + xShift, point.y() + yShift, point.z() + zShift),
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

        BinaryValuesByte binaryValues = outline.binaryValuesByte();

        if (!outline.extent().contains(point)) {
            return false;
        }

        UnsignedByteBuffer buffer = outline.sliceBufferLocal(point.z());
        int offset = outline.extent().offsetSlice(point);

        // Check if the buffer allows us to read the pixel
        if (buffer.getRaw(offset) == binaryValues.getOffByte()) {
            return false;
        }

        // We do the check after first verifying that visiting the pixel is possible from the buffer
        if (!considerVisit.considerVisit(point, distance)) {
            return false;
        }

        buffer.putRaw(offset, binaryValues.getOffByte());

        return true;
    }
}
