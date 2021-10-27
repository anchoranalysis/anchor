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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;

/**
 * Calculates which {@link VoxelDataType} to use when combining two other voxel-data types, but without losing
 * precision.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CombineTypes {

    /**
     * What data-type to use to represent voxels of both {@code type1} and {@code type2}?
     * 
     * <p>Only the following types are supported:
     * 
     * <ul>
     * <li>{@link UnsignedByteVoxelType}
     * <li>{@link UnsignedShortVoxelType}
     * </ul>
     * 
     * @param type1 the first voxel data type.
     * @param type2 the second voxel data type.
     * @return the data-type to use to combine {@code type1} and {@code type2}.
     * @throws CreateException if either {@code type1} or {@code type2} is an unsupported type.
     */
    public static VoxelDataType combineTypes(VoxelDataType type1, VoxelDataType type2)
            throws CreateException {
        if (type1.equals(type2)) {
            return type1;
        } else if (type1.equals(UnsignedByteVoxelType.INSTANCE)
                && type2.equals(UnsignedShortVoxelType.INSTANCE)) {
            return UnsignedShortVoxelType.INSTANCE;
        } else if (type2.equals(UnsignedByteVoxelType.INSTANCE)
                && type1.equals(UnsignedShortVoxelType.INSTANCE)) {
            return UnsignedShortVoxelType.INSTANCE;
        } else {
            throw new CreateException("Only combinations of byte and short are supported");
        }
    }
}
