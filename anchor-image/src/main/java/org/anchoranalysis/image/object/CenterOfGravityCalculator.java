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

package org.anchoranalysis.image.object;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.voxel.box.VoxelBox;

final class CenterOfGravityCalculator {

    private CenterOfGravityCalculator() {}

    /**
     * Calculates the center of gravity of an object-mask treating all pixels of equal weight.
     *
     * <p>Specifically this is the mean of the position coordinates in each dimension
     *
     * @param object
     * @return the center-of-gravity or (NaN, NaN, NaN) if there are no pixels.
     */
    public static Point3d calcCenterOfGravity(ObjectMask object) {

        VoxelBox<ByteBuffer> vb = object.getVoxelBox();

        int cnt = 0;
        Point3d sum = new Point3d();
        byte onByte = object.getBinaryValuesByte().getOnByte();

        for (int z = 0; z < vb.extent().getZ(); z++) {

            ByteBuffer bb = vb.getPixelsForPlane(z).buffer();

            int offset = 0;
            for (int y = 0; y < vb.extent().getY(); y++) {
                for (int x = 0; x < vb.extent().getX(); x++) {

                    if (bb.get(offset) == onByte) {
                        sum.add(x, y, z);
                        cnt++;
                    }
                    offset++;
                }
            }
        }

        if (cnt == 0) {
            return emptyPoint();
        }

        sum.divideBy(cnt);
        sum.add(object.getBoundingBox().cornerMin());
        return sum;
    }

    /**
     * Like {@link #calcCenterOfGravity} but for a specific axis.
     *
     * @param object the object whose center-of-gravity is to be calculated on one axis.
     * @param axisType which axis
     * @return the cog for that axis, or NaN if there are no points.
     */
    public static double calcCenterOfGravityForAxis(ObjectMask object, AxisType axisType) {

        VoxelBox<ByteBuffer> vb = object.getVoxelBox();

        int cnt = 0;
        double sum = 0.0;
        byte onByte = object.getBinaryValuesByte().getOnByte();

        for (int z = 0; z < vb.extent().getZ(); z++) {

            ByteBuffer bb = vb.getPixelsForPlane(z).buffer();

            int offset = 0;
            for (int y = 0; y < vb.extent().getY(); y++) {
                for (int x = 0; x < vb.extent().getX(); x++) {

                    if (bb.get(offset) == onByte) {
                        sum += AxisTypeConverter.valueFor(axisType, x, y, z);
                        cnt++;
                    }
                    offset++;
                }
            }
        }

        if (cnt == 0) {
            return Double.NaN;
        }

        return (sum / cnt) + object.getBoundingBox().cornerMin().getValueByDimension(axisType);
    }

    private static Point3d emptyPoint() {
        return new Point3d(Double.NaN, Double.NaN, Double.NaN);
    }
}
