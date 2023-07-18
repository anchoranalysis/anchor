/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats.copyconvert.toshort;

import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;

/**
 * Convert to an <i>unsigned short</i> buffer, given an <i>unsigned short</i> source buffer.
 *
 * @author Owen Feehan
 */
public final class UnsignedShortFromUnsignedShort extends ToUnsignedShort {

    @Override
    protected short convertValue(short value) {

        int valueAsInt = value;

        // Make positive
        if (valueAsInt < 0) {
            valueAsInt += (UnsignedShortVoxelType.MAX_VALUE_INT + 1);
        }

        if (valueAsInt > UnsignedShortVoxelType.MAX_VALUE_INT) {
            valueAsInt = UnsignedShortVoxelType.MAX_VALUE_INT;
        }
        if (valueAsInt < 0) {
            valueAsInt = 0;
        }

        return (short) valueAsInt;
    }
}
