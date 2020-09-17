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
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;

/**
 * Converts voxel buffers to a unsigned 8-bit buffer scaling against a the minimum and maximum constant.
 * 
 * <p>The scaling is linear between these two boundaries.
 * 
 * @author Owen Feehan
 *
 */
public final class ToByteScaleByMinMaxValue extends ToByte {

    private float scale = 0;
    private int subtract = 0;

    public ToByteScaleByMinMaxValue(int minValue, int maxValue) {
        super();
        setMinMaxValues(minValue, maxValue);
    }

    public void setMinMaxValues(int minValue, int maxValue) {
        this.scale = 255.0f / (maxValue - minValue);
        this.subtract = minValue;
    }

    @Override
    protected void convertUnsignedShort(UnsignedShortBuffer in, UnsignedByteBuffer out) {
        out.putFloatClipped( scale * (in.getUnsigned() - subtract) );
    }

    @Override
    protected void convertUnsignedInt(UnsignedIntBuffer in, UnsignedByteBuffer out) {
        out.putFloatClipped( scale * (in.getUnsigned() - subtract) );
    }

    @Override
    protected void convertFloat(FloatBuffer in, UnsignedByteBuffer out) {
        out.putFloatClipped( scale * (in.get() - subtract) );
    }
}
