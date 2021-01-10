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

package org.anchoranalysis.image.voxel.object.morphological;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.kernel.morphological.DilationContext;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.morphological.predicate.AcceptIterationPredicate;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Performs morphological erosion operation on an {@link ObjectMask} or {@link BinaryVoxels}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MorphologicalErosion {

    /**
     * Performs a morphological erosion on an {@link ObjectMask}.
     *
     * @param object the object-mask to perform the erosion on.
     * @param iterations how many iterations of erosion to perform.
     * @param useZ whether to use the Z dimension or not during the erosion
     * @return a newly created {@code BinaryVoxels<UnsignedByteBuffer>} showing {@voxels} after the
     *     erosion operation was applied.
     * @throws CreateException
     */
    public static ObjectMask erode(ObjectMask object, int iterations, boolean useZ)
            throws CreateException {
        return erode(object, iterations, useZ, Optional.empty());
    }
    /**
     * Performs a morphological erosion on an {@link ObjectMask} - with a <b>postcondition</b>.
     *
     * @param object the object-mask to perform the erosion on.
     * @param iterations how many iterations of erosion to perform.
     * @param useZ whether to use the Z dimension or not during the erosion
     * @param postcondition conditions applied after each iteration of the erosion, otherwise no
     *     more iterations occur. Note that these are applied on an inverted version of {@code
     *     voxels}.
     * @return a newly created {@code BinaryVoxels<UnsignedByteBuffer>} showing {@voxels} after the
     *     erosion operation was applied.
     * @throws CreateException
     */
    public static ObjectMask erode(
            ObjectMask object,
            int iterations,
            boolean useZ,
            Optional<AcceptIterationPredicate> postcondition)
            throws CreateException {

        ObjectMask objectOut = object.duplicate();

        BinaryVoxels<UnsignedByteBuffer> eroded =
                erodeInternal(
                        objectOut.binaryVoxels(),
                        iterations,
                        useZ,
                        Optional.empty(),
                        postcondition);
        return objectOut.replaceVoxels(eroded.voxels());
    }

    /**
     * Performs a morphological erosion on a {@code BinaryVoxels<UnsignedByteBuffer> voxels}.
     *
     * @param voxels the voxels to perform the erosion on.
     * @param iterations how many iterations of erosion to perform.
     * @param useZ whether to use the Z dimension or not during the erosion
     * @param precondition if defined, a condition which must be satisfied on a <i>voxel</i>, before
     *     any voxel can be dilated.
     * @return a newly created {@code BinaryVoxels<UnsignedByteBuffer>} showing {@voxels} after the
     *     erosion operation was applied.
     * @throws CreateException
     */
    public static BinaryVoxels<UnsignedByteBuffer> erode(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            int iterations,
            boolean useZ,
            Optional<Predicate<Point3i>> precondition)
            throws CreateException {
        return erodeInternal(voxels, iterations, useZ, precondition, Optional.empty());
    }

    private static BinaryVoxels<UnsignedByteBuffer> erodeInternal(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            int iterations,
            boolean useZ,
            Optional<Predicate<Point3i>> precondition,
            Optional<AcceptIterationPredicate> postcondition)
            throws CreateException {

        DilationContext context =
                new DilationContext(
                        OutsideKernelPolicy.AS_ON, useZ, false, precondition, postcondition);

        voxels.invert();
        BinaryVoxels<UnsignedByteBuffer> dilated =
                MorphologicalDilation.dilate(voxels, iterations, context);
        dilated.invert();
        return dilated;
    }
}
