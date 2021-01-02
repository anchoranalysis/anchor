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

package org.anchoranalysis.image.voxel.kernel.outline;

import java.util.Optional;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.spatial.point.Point3i;

// Keeps any on pixel that touches an off pixel, off otherwise
public class OutlineKernel extends OutlineKernelBase {

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public boolean acceptPoint(int ind, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        UnsignedByteBuffer buffer = getVoxels().getLocal(0).get(); // NOSONAR
        Optional<UnsignedByteBuffer> bufferZLess1 = getVoxels().getLocal(-1);
        Optional<UnsignedByteBuffer> bufferZPlus1 = getVoxels().getLocal(+1);

        int xLength = extent.x();

        int x = point.x();
        int y = point.y();

        if (binaryValues.isOff(buffer.getRaw(ind))) {
            return false;
        }

        // We walk up and down in x
        x--;
        ind--;
        if (x >= 0) {
            if (binaryValues.isOff(buffer.getRaw(ind))) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }

        x += 2;
        ind += 2;
        if (x < extent.x()) {
            if (binaryValues.isOff(buffer.getRaw(ind))) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }
        ind--;

        // We walk up and down in y
        y--;
        ind -= xLength;
        if (y >= 0) {
            if (binaryValues.isOff(buffer.getRaw(ind))) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }

        y += 2;
        ind += (2 * xLength);
        if (y < (extent.y())) {
            if (binaryValues.isOff(buffer.getRaw(ind))) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }
        ind -= xLength;

        if (params.isUseZ()) {

            if (bufferZLess1.isPresent()) {
                if (binaryValues.isOff(bufferZLess1.get().getRaw(ind))) {
                    return true;
                }
            } else {
                if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                    return true;
                }
            }

            if (bufferZPlus1.isPresent()) {
                if (binaryValues.isOff(bufferZPlus1.get().getRaw(ind))) {
                    return true;
                }
            } else {
                if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                    return true;
                }
            }
        }

        return false;
    }
}
