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

package org.anchoranalysis.image.voxel.datatype;

/**
 * Base class for voxel-data-types that are <b>unsigned</b> and <b>integral</b>.
 *
 * @author Owen Feehan
 */
public abstract class UnsignedVoxelType extends VoxelDataType {

    /**
     * Construct for a particular number of bits, with a unique identifier, and with bounds on the
     * values.
     *
     * @param numberBits the number of bits required to represent a voxel.
     * @param typeIdentifier a string to uniquely and compactly describe this type.
     * @param maxValue the maximum value this type can represent.
     */
    protected UnsignedVoxelType(int numberBits, String typeIdentifier, long maxValue) {
        super(numberBits, typeIdentifier, maxValue, 0);
    }

    @Override
    public final boolean isInteger() {
        return true;
    }

    @Override
    public final boolean isUnsigned() {
        return true;
    }
}
