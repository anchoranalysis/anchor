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
 * <p>Both a {@link Point3i} and an associated index (in a voxel buffer} are kept as mutable state,
 * that are generally changed together in a single operation.
 *
 * @author Owen Feehan
 */
public final class KernelPointCursor {

    /** 
     * The index in the buffer that the kernel currently is focussed on.
     *
     * <p>The buffer pertains to the X and Y dimensions only.
     */
    @Getter @Setter private int index;

    /** The point around which the cursor may iterate. */
    @Getter private Point3i point;

    private final BinaryValuesByte binaryValues;
    private final KernelApplicationParameters params;

    /** The size of the image the kernel iterates over. */
    @Getter private final Extent extent;
    
    private final int xExtent;
    private final int yExtent;

    /**
     * Two times {@link #xExtent}, used to save computation as this occurs frequently in an
     * operation.
     */
    private final int xExtentTwice;

    /**
     * Creates to be focused around a particular point in the image.
     * 
     * @param index the index in the buffer referring to {@code point}.
     * @param point the point in the image (in three dimensions) where current focus resides. 
     * @param extent the size of the image.
     * @param binaryValues what intensity values define <i>on</i> and <i>off</i> states.
     * @param params parameters that influence how the kernel is applied.
     */
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

    /**
     * Increments the point and associated index by one in the X dimension.
     */
    public void incrementX() {
        point.incrementX();
        index++;
    }

    /**
     * Increments the point and associated index by two in the X dimension.
     */
    public void incrementXTwice() {
        point.incrementX(2);
        index += 2;
    }

    /**
     * Decrements the point and associated index by one in the X dimension.
     */
    public void decrementX() {
        point.decrementX();
        index--;
    }

    /**
     * Increments the point and associated index by one in the Y dimension.
     */
    public void incrementY() {
        point.incrementY();
        index += xExtent;
    }

    /**
     * Increments the point and associated index by two in the Y dimension.
     */
    public void incrementYTwice() {
        point.incrementY(2);
        index += xExtentTwice;
    }

    /**
     * Decrements the point and associated index by one in the Y dimension.
     */
    public void decrementY() {
        point.decrementY();
        index -= xExtent;
    }

    /**
     * Decrements the point and associated index by two in the Y dimension.
     */
    public void decrementYTwice() {
        point.decrementY(2);
        index -= xExtentTwice;
    }

    /**
     * Increments the point by one in the Z dimension.
     * 
     * <p>The associated index remains unchanged.
     */
    public void incrementZ() {
        point.incrementZ();
    }

    /**
     * Increments the point by two in the Z dimension.
     * 
     * <p>The associated index remains unchanged.
     */
    public void incrementZTwice() {
        point.incrementZ(2);
    }

    /**
     * Decrements the point by one in the Z dimension.
     * 
     * <p>The associated index remains unchanged.
     */
    public void decrementZ() {
        point.decrementZ();
    }
    
    /**
     * Increments the current index state by one, <i>without</i> changing the current point state.
     */
    public void incrementIndexOnly() {
        index++;
    }

    /** Whether to additionally apply the kernel along the Z dimension, as well as X and Y? */
    public boolean isUseZ() {
        return params.isUseZ();
    }

    /**
     * Whether the current point is non-negative in the X-dimension?
     * 
     * @return true iff the condition is fulfilled.
     */
    public boolean nonNegativeX() {
        return point.x() >= 0;
    }

    /**
     * Whether the current point is non-negative in the Y-dimension?
     * 
     * @return true iff the condition is fulfilled.
     */
    public boolean nonNegativeY() {
        return point.y() >= 0;
    }

    /**
     * Whether the current point is less than the image's extent in the X-dimension?
     * 
     * @return true iff the condition is fulfilled.
     */
    public boolean lessThanMaxX() {
        return point.x() < xExtent;
    }

    /**
     * Whether the current point is less than the image's extent in the Y-dimension?
     * 
     * @return true iff the condition is fulfilled.
     */
    public boolean lessThanMaxY() {
        return point.y() < yExtent;
    }

    /**
     * Is the value at the current index in this buffer corresponding to an <i>on</i> state?
     * 
     * @param buffer the buffer containing the value that will be tested.
     * @return true if the value corresponds to an <i>on</i> state.
     */
    public boolean isBufferOn(UnsignedByteBuffer buffer) {
        return binaryValues.isOn(buffer.getRaw(index));
    }

    /**
     * Is the value at the current index in this buffer corresponding to an <i>off</i> state?
     * 
     * @param buffer the buffer containing the value that will be tested.
     * @return true if the value corresponds to an <i>off</i> state.
     */
    public boolean isBufferOff(UnsignedByteBuffer buffer) {
        return binaryValues.isOff(buffer.getRaw(index));
    }

    /**
     * Whether to treat voxels that lie outside the scene as <i>on</i> (if true) or <i>off</i> (if false).
     * 
     * @return true if voxels lying outside the scene should be treated as <i>on</i> in the above circumstance, otherwise they are treated as <i>off</i>.
     */
    public boolean isOutsideOn() {
        return params.isOutsideOn();
    }

    /**
     * True only when voxels outside the scene should <b>not be ignored</i> and considered as <i>off</i>.
     * 
     * @return true iff both conditions above are true.
     */
    public boolean isOutsideOffUnignored() {
        return params.isOutsideOffUnignored();
    }
}
