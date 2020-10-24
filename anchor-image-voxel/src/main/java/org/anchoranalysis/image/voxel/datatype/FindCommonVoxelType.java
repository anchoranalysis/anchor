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

import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;

/**
 * Finds a common voxel-data type to represent two types.
 *
 * <p>This usually either the class themselves or a type that is minimally larger, and can represent
 * both types without loss of precision.
 *
 * <p>An exception is the float-type that takes precedence over all others, and this may lead to
 * loss of precision.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindCommonVoxelType {

    /**
     * Finds a common type to represent (ideally without loss of precision} of a stream of types.
     *
     * @param stream a stream of types to find a common representation for.
     * @return a type that can represent all types (think of it as a minimal superset of all types),
     *     or {@link Optional#empty} if the stream has no elements.
     */
    public static Optional<VoxelDataType> commonType(Stream<VoxelDataType> stream) {
        return stream.reduce(FindCommonVoxelType::commonType);
    }
    /**
     * Finds a common type to represent (ideally without loss of precision} both {@code first} and
     * {@code second}.
     *
     * @param first the first-type that must be represented in the common type
     * @param second the second-type that must be represented in the common type
     * @return a type that can represent both (think of it as a minimal superset of both types).
     */
    public static VoxelDataType commonType(VoxelDataType first, VoxelDataType second) {

        // If either type is non-integral, then use a float as the common type
        if (!first.isInteger() || !second.isInteger()) {
            return FloatVoxelType.INSTANCE;
        }

        // Then take which ever type has the highest number of bits
        // This assumes, whether signed or unsigned, a type with a higher
        // number of bits, can contain one of a lower number.
        // This holds true with a unsigned/signed 8, 16, 32 combinations.
        if (first.numberBits() > second.numberBits()) {
            return first;
        }

        if (first.numberBits() < second.numberBits()) {
            return second;
        }

        // This this point the number-of-bits must be equal of both types,
        //  so compare the signed vs unsigned
        if (first.isUnsigned() == second.isUnsigned()) {
            // Then both seem to be the same type, so use either arbitrarily.
            return first;
        } else {
            // Otherwise the next highest signed integer type must be used
            // to contain both the unsigned and signed buffers of smaller bits

            // As signed types aren't properly implemented yet, this situation
            // should not occur.
            throw new AnchorImpossibleSituationException();
        }
    }
}
