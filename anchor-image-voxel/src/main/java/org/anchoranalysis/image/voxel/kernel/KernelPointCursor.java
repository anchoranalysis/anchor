/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.kernel;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * A mutable context around determining whether a particular point should be <i>on</i> or <i>off</i>
 * with a {@link BinaryKernel}.
 *
 * <p>It can be used to efficiently iterate over the neighbours around a particular point.
 *
 * @author Owen Feehan
 */
public final class KernelPointCursor {

    @Getter @Setter private int index;

    /** The point around which the cursor may iterate. */
    @Getter private Point3i point;

    private final BinaryValuesByte binaryValues;
    private final KernelApplicationParameters params;

    @Getter private final Extent extent;
    private final int xExtent;
    private final int yExtent;

    /**
     * Two times {@link #xExtent}, used to save computation as this occurs frequently in an
     * operation.
     */
    private final int xExtentTwice;

    public KernelPointCursor(
            int index,
            Point3i point,
            Extent extent,
            BinaryValuesByte binaryValues,
            KernelApplicationParameters params) {
        this.index = index;
        this.point = point;
        this.extent = extent;
        this.xExtent = extent.x();
        this.yExtent = extent.y();
        this.xExtentTwice = xExtent * 2;
        this.binaryValues = binaryValues;
        this.params = params;
    }

    public void incrementX() {
        point.incrementX();
        index++;
    }

    public void incrementXTwice() {
        point.incrementX(2);
        index += 2;
    }

    public void decrementX() {
        point.decrementX();
        index--;
    }

    public void incrementY() {
        point.incrementY();
        index += xExtent;
    }

    public void incrementYTwice() {
        point.incrementY(2);
        index += xExtentTwice;
    }

    public void decrementY() {
        point.decrementY();
        index -= xExtent;
    }

    public void decrementYTwice() {
        point.decrementY(2);
        index -= xExtentTwice;
    }

    public void incrementZ() {
        point.incrementZ();
    }

    public void incrementZTwice() {
        point.incrementZ(2);
    }

    public void decrementZ() {
        point.decrementZ();
    }

    public boolean isUseZ() {
        return params.isUseZ();
    }

    public boolean nonNegativeX() {
        return point.x() >= 0;
    }

    public boolean nonNegativeY() {
        return point.y() >= 0;
    }

    public boolean lessThanMaxX() {
        return point.x() < xExtent;
    }

    public boolean lessThanMaxY() {
        return point.y() < yExtent;
    }

    public boolean isBufferOn(UnsignedByteBuffer buffer) {
        return binaryValues.isOn(buffer.getRaw(index));
    }

    public boolean isBufferOff(UnsignedByteBuffer buffer) {
        return binaryValues.isOff(buffer.getRaw(index));
    }

    public boolean isOutsideOn() {
        return params.isOutsideOn();
    }

    public boolean isOutsideOffUnignored() {
        return params.isOutsideOffUnignored();
    }

    public void incrementIndex() {
        index++;
    }
}
