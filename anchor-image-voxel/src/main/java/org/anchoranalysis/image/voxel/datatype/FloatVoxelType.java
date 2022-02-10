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
 * A 32-bit voxel data-type representing floating-point numbers, as per the Java primitive
 * <b>float</b> type.
 *
 * @author Owen Feehan
 */
public class FloatVoxelType extends VoxelDataType {

    /** How many bits to represent this voxel-type. */
    public static final int BIT_DEPTH = 32;

    /** Minimum supported value for the type. */
    public static final long MAX_VALUE = VoxelDataType.VALUE_NOT_COMPATIBLE;

    /** Maximum supported value for the type. */
    public static final long MIN_VALUE = VoxelDataType.VALUE_NOT_COMPATIBLE;

    /** A singleton instance of the type. */
    public static final FloatVoxelType INSTANCE = new FloatVoxelType();

    private FloatVoxelType() {
        super(BIT_DEPTH, "float", MAX_VALUE, MIN_VALUE);
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    @Override
    public boolean isUnsigned() {
        return false;
    }
}
