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

package org.anchoranalysis.image.voxel.binary.values;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;

/**
 * Two values representing {@code int} binary states in an unsigned-byte buffer e.g. 0 for OFF and 1
 * for ON
 *
 * <p>This class is <i>immutable</i>.
 *
 * <p>See {@link BinaryValuesByte} for an equivalent class that stores these states as {@code byte}
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode
@AllArgsConstructor
public final class BinaryValues {

    /** Default values to use, if not otherwise specified */
    private static final BinaryValues DEFAULT = new BinaryValues(0, 255);

    /** The integer representation of the value for OFF */
    @Getter private final int offInt;

    /** The integer representation of the value for ON */
    @Getter private final int onInt;

    public BinaryValuesByte createByte() {
        if (offInt > 255) {
            throw new IncorrectVoxelTypeException("offInt must be <= 255");
        }
        if (onInt > 255) {
            throw new IncorrectVoxelTypeException("onInt must be <= 255");
        }
        return new BinaryValuesByte(offInt, onInt);
    }

    /** Default values to use, if not otherwise specified */
    public static BinaryValues getDefault() {
        return DEFAULT;
    }

    /** Inverts the values so OFF becomes ON, and vice-versa */
    public BinaryValues createInverted() {
        return new BinaryValues(onInt, offInt);
    }
}
