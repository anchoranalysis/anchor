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

package org.anchoranalysis.image.voxel.kernel.morphological;

import java.util.Optional;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.spatial.point.Point3i;

// Erosion with a 3x3 or 3x3x3 kernel
final class DilationKernel extends BinaryKernelMorphologicalExtent {

    public DilationKernel(boolean bigNeighborhood) {
        super(false);
        this.bigNeighborhood = bigNeighborhood;
    }

    /** Use a big neighbourhood if in 2D, but ignored when in 3D. */
    private final boolean bigNeighborhood;

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public boolean acceptPoint(
            int index,
            Point3i point,
            BinaryValuesByte binaryValues,
            KernelApplicationParameters params) {

        UnsignedByteBuffer buffer = getVoxels().getLocal(0).get(); // NOSONAR
        Optional<UnsignedByteBuffer> bufferZLess1 = getVoxels().getLocal(-1);
        Optional<UnsignedByteBuffer> bufferZPlus1 = getVoxels().getLocal(+1);

        int xLength = extent.x();

        int x = point.x();
        int y = point.y();

        if (binaryValues.isOn(buffer.getRaw(index))) {
            return true;
        }

        // We walk up and down in x
        x--;
        index--;
        if (x >= 0) {
            if (binaryValues.isOn(buffer.getRaw(index))) {
                return isQualifiedOutcome();
            }
        } else {
            if (params.isOutsideHigh()) {
                return isQualifiedOutcome();
            }
        }

        x += 2;
        index += 2;
        if (x < extent.x()) {
            if (binaryValues.isOn(buffer.getRaw(index))) {
                return isQualifiedOutcome();
            }
        } else {
            if (params.isOutsideHigh()) {
                return isQualifiedOutcome();
            }
        }
        x--;
        index--;

        // We walk up and down in y
        y--;
        index -= xLength;
        if (y >= 0) {
            if (binaryValues.isOn(buffer.getRaw(index))) {
                return isQualifiedOutcome();
            }
        } else {
            if (params.isOutsideHigh()) {
                return isQualifiedOutcome();
            }
        }

        y += 2;
        index += (2 * xLength);
        if (y < (extent.y())) {
            if (binaryValues.isOn(buffer.getRaw(index))) {
                return isQualifiedOutcome();
            }
        } else {
            if (params.isOutsideHigh()) {
                return isQualifiedOutcome();
            }
        }
        y--;
        index -= xLength;

        if (bigNeighborhood) {

            // x-1, y-1

            x--;
            index--;

            y--;
            index -= xLength;

            if (x >= 0 && y >= 0) {
                if (binaryValues.isOn(buffer.getRaw(index))) {
                    return isQualifiedOutcome();
                }
            } else {
                if (params.isOutsideHigh()) {
                    return isQualifiedOutcome();
                }
            }

            // x-1, y+1

            y += 2;
            index += (2 * xLength);
            if (x >= 0 && y < (extent.y())) {
                if (binaryValues.isOn(buffer.getRaw(index))) {
                    return isQualifiedOutcome();
                }
            } else {
                if (params.isOutsideHigh()) {
                    return isQualifiedOutcome();
                }
            }
            y--;
            index -= xLength;

            x += 2;
            index += 2;

            y--;
            index -= xLength;

            // x +1, y-1

            if (x < extent.x() && y >= 0) {
                if (binaryValues.isOn(buffer.getRaw(index))) {
                    return isQualifiedOutcome();
                }
            } else {
                if (params.isOutsideHigh()) {
                    return isQualifiedOutcome();
                }
            }

            // x+1, y+1

            y += 2;
            index += (2 * xLength);
            if (x < extent.x() && y < (extent.y())) {
                if (binaryValues.isOn(buffer.getRaw(index))) {
                    return isQualifiedOutcome();
                }
            } else {
                if (params.isOutsideHigh()) {
                    return isQualifiedOutcome();
                }
            }
            index -= xLength;

            index--;
        }

        if (params.isUseZ()) {

            if (bufferZLess1.isPresent()) {
                if (binaryValues.isOn(bufferZLess1.get().getRaw(index))) {
                    return isQualifiedOutcome();
                }
            } else {
                if (params.isOutsideHigh()) {
                    return isQualifiedOutcome();
                }
            }

            if (bufferZPlus1.isPresent()) {
                if (binaryValues.isOn(bufferZPlus1.get().getRaw(index))) {
                    return isQualifiedOutcome();
                }
            } else {
                if (params.isOutsideHigh()) {
                    return isQualifiedOutcome();
                }
            }
        }

        return isUnqualifiedOutcome();
    }
}
