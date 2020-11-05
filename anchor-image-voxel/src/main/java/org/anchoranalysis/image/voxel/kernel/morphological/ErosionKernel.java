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
import java.util.function.Supplier;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Erosion with a 3x3 or 3x3x3 kernel
 *
 * @author Owen Feehan
 */
public final class ErosionKernel extends BinaryKernelMorphologicalExtent {

    // Constructor
    public ErosionKernel(BinaryValuesByte binaryValues, boolean outsideAtThreshold, boolean useZ) {
        super(binaryValues, outsideAtThreshold, useZ);
    }

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public boolean acceptPoint(int index, Point3i point) {

        UnsignedByteBuffer buffer = inSlices.getLocal(0).get(); // NOSONAR

        int xLength = extent.x();

        int x = point.x();
        int y = point.y();

        if (binaryValues.isOff(buffer.getRaw(index))) {
            return false;
        }

        // We walk up and down in x
        x--;
        index--;
        if (x >= 0) {
            if (binaryValues.isOff(buffer.getRaw(index))) {
                return false;
            }
        } else {
            if (outsideAtThreshold) {
                return false;
            }
        }

        x += 2;
        index += 2;
        if (x < extent.x()) {
            if (binaryValues.isOff(buffer.getRaw(index))) {
                return false;
            }
        } else {
            if (outsideAtThreshold) {
                return false;
            }
        }
        index--;

        // We walk up and down in y
        y--;
        index -= xLength;
        if (y >= 0) {
            if (binaryValues.isOff(buffer.getRaw(index))) {
                return false;
            }
        } else {
            if (outsideAtThreshold) {
                return false;
            }
        }

        y += 2;
        index += (2 * xLength);
        if (y < (extent.y())) {
            if (binaryValues.isOff(buffer.getRaw(index))) {
                return false;
            }
        } else {
            if (outsideAtThreshold) {
                return false;
            }
        }
        index -= xLength;

        return maybeCheckZ(() -> inSlices.getLocal(-1), () -> inSlices.getLocal(+1), index);
    }

    private boolean maybeCheckZ(
            Supplier<Optional<UnsignedByteBuffer>> bufferZLess1,
            Supplier<Optional<UnsignedByteBuffer>> bufferZPlus1,
            int index) {
        return !useZ || (
                checkZBuffer(bufferZLess1.get(), index)
                && checkZBuffer(bufferZPlus1.get(), index));
    }

    private boolean checkZBuffer(Optional<UnsignedByteBuffer> buffer, int index) {
        if (buffer.isPresent()) {
            return !binaryValues.isOff(buffer.get().getRaw(index));
        } else {
            return !outsideAtThreshold;
        }
    }
}
