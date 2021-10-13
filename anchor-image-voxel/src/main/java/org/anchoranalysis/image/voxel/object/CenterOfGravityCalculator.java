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

package org.anchoranalysis.image.voxel.object;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.axis.AxisConverter;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3d;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class CenterOfGravityCalculator {

    /**
     * Calculates the center of gravity of an object-mask treating all pixels of equal weight.
     *
     * <p>Specifically this is the mean of the position coordinates in each dimension
     *
     * @param object
     * @return the center-of-gravity or (NaN, NaN, NaN) if there are no pixels.
     */
    public static Point3d centerOfGravity(ObjectMask object) {

        int count = 0;
        Point3d sum = new Point3d();
        byte onByte = object.binaryValuesByte().getOnByte();
        Extent extent = object.extent();

        for (int z = 0; z < extent.z(); z++) {

            UnsignedByteBuffer buffer = object.sliceBufferLocal(z);

            int offset = 0;
            for (int y = 0; y < extent.y(); y++) {
                for (int x = 0; x < extent.x(); x++) {

                    if (buffer.getRaw(offset) == onByte) {
                        sum.add(x, y, z);
                        count++;
                    }
                    offset++;
                }
            }
        }

        if (count == 0) {
            return emptyPoint();
        }

        sum.divideBy(count);
        sum.add(object.boundingBox().cornerMin());
        return sum;
    }

    /**
     * Like {@link #centerOfGravity} but for a specific axis.
     *
     * @param object the object whose center-of-gravity is to be calculated on one axis.
     * @param axis which axis
     * @return the cog for that axis, or NaN if there are no points.
     */
    public static double centerOfGravityForAxis(ObjectMask object, Axis axis) {

        int count = 0;
        double sum = 0.0;
        byte onByte = object.binaryValuesByte().getOnByte();
        Extent extent = object.extent();

        for (int z = 0; z < extent.z(); z++) {

            UnsignedByteBuffer buffer = object.sliceBufferLocal(z);

            int offset = 0;
            for (int y = 0; y < extent.y(); y++) {
                for (int x = 0; x < extent.x(); x++) {

                    if (buffer.getRaw(offset) == onByte) {
                        sum += AxisConverter.valueFor(axis, x, y, z);
                        count++;
                    }
                    offset++;
                }
            }
        }

        if (count == 0) {
            return Double.NaN;
        }

        return (sum / count) + object.boundingBox().cornerMin().valueByDimension(axis);
    }

    private static Point3d emptyPoint() {
        return new Point3d(Double.NaN, Double.NaN, Double.NaN);
    }
}
