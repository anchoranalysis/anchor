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

package org.anchoranalysis.image.voxel.kernel.density;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

public class DensityKernel extends BinaryKernel {

    private BinaryValuesByte bv;
    private double minDensityRatio;

    private VoxelBox<ByteBuffer> in;
    private LocalSlices inSlices;

    private static class Density {
        private int numberHit = 0;

        public void incrOn() {
            numberHit++;
        }

        public double ratioSize2D(int size) {
            int sizeSq = size * size;
            return ((double) numberHit) / sizeSq;
        }
    }

    public DensityKernel(int size, BinaryValuesByte bv, double minDensityRatio) {
        super(size);
        this.bv = bv;
        this.minDensityRatio = minDensityRatio;
    }

    private Density calcDensity(VoxelBox<ByteBuffer> in, LocalSlices inSlices, Point3i point) {

        // We count the number of on pixels inside a kernel

        Density density = new Density();

        int yMin = getYMin(point);
        int yMax = getYMax(point, in.extent());

        int xMin = getXMin(point);
        int xMax = getXMax(point, in.extent());

        for (int z = (-1 * getSizeHalf()); z <= getSizeHalf(); z++) {

            ByteBuffer arr = inSlices.getLocal(z);

            if (arr == null) {
                continue;
            }

            for (int y = yMin; y <= yMax; y++) {

                int indLocal = in.extent().offset(xMin, y);
                int indLocalMax = indLocal + xMax - xMin;

                while (indLocal <= indLocalMax) {

                    if (bv.isOn(arr.get(indLocal))) {
                        density.incrOn();
                    }
                    indLocal++;
                }
            }
        }

        return density;
    }

    @Override
    public boolean accptPos(int ind, Point3i point) {

        // We count the number of on pixels inside a kernel

        Density density = calcDensity(in, inSlices, point);

        double ratio = density.ratioSize2D(getSize());

        return (ratio >= minDensityRatio);
    }

    @Override
    public void init(VoxelBox<ByteBuffer> in) {
        this.in = in;
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        this.inSlices = inSlices;
    }
}
