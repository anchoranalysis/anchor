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

package org.anchoranalysis.image.voxel.convert;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;

/**
 * Converts voxel buffers to a unsigned 8-bit buffer linearly scaling against the maximum constant
 * value.
 * 
 * <p>The scaling occurs so that the full 8-bit range of values is supported.
 *
 * @author Owen Feehan
 */
public final class ToByteScaleByMaxValue extends ToByte {

    private float scale;

    /**
     * Creates with the maximum-value which existing values are scaled against.
     * 
     * @param maxValue the maximum-value that will be represented in the scaled-values.
     */
    public ToByteScaleByMaxValue(int maxValue) {
        setMaxValue(maxValue);
    }

    /**
     * Assigns the maximum-value that will be represented in the scaled-values.
     * 
     * @param maxValue the maximum-value.
     */
    public void setMaxValue(long maxValue) {
        this.scale = 255.0f / maxValue;
    }

    @Override
    protected void convertUnsignedShort(UnsignedShortBuffer in, UnsignedByteBuffer out) {
        out.putFloatClamped(scale * in.getUnsigned());
    }

    @Override
    protected void convertUnsignedInt(UnsignedIntBuffer in, UnsignedByteBuffer out) {
        out.putFloatClamped(scale * in.getUnsigned());
    }

    @Override
    protected void convertFloat(FloatBuffer in, UnsignedByteBuffer out) {
        out.putFloatClamped(scale * in.get());
    }
}
