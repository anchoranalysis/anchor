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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The type of data one voxel represents in an image-channel.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@Accessors(fluent = true)
public abstract class VoxelDataType {

    /** The number of bits required to represent a voxel. */
    @Getter private int numberBits;

    /** A string to uniquely and compactly describe this type. */
    private String typeIdentifier;

    /** The maximum value this type can represent. */
    @Getter private long maxValue;

    /** The minimum value this type can represent. */
    @Getter private long minValue;

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof VoxelDataType)) {
            return false;
        }

        VoxelDataType otherCasted = (VoxelDataType) obj;

        if (isInteger() != otherCasted.isInteger()) {
            return false;
        }

        if (isUnsigned() != otherCasted.isUnsigned()) {
            return false;
        }

        return numberBits() == otherCasted.numberBits();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(isInteger())
                .append(isUnsigned())
                .append(numberBits())
                .toHashCode();
    }

    public abstract boolean isInteger();

    public abstract boolean isUnsigned();

    @Override
    public final String toString() {
        return typeIdentifier;
    }
}
