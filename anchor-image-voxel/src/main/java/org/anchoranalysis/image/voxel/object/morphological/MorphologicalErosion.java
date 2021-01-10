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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.kernel.morphological.DilationKernelFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.morphological.predicate.AcceptIterationPredicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MorphologicalErosion {

    public static ObjectMask createErodedObject(
            ObjectMask object,
            boolean do3D,
            int iterations,
            Optional<AcceptIterationPredicate>
                    acceptConditionsDilation // NB applied on an inverted-version of the binary
            // buffer!!!
            ) throws CreateException {

        ObjectMask objectOut = object.duplicate();

        BinaryVoxels<UnsignedByteBuffer> eroded =
                erode(
                        objectOut.binaryVoxels(),
                        iterations,
                        Optional.empty(),
                        0,
                        do3D,
                        acceptConditionsDilation);
        return objectOut.replaceVoxels(eroded.voxels());
    }

    /**
     * Performs a morphological erosion on a {@code BinaryVoxels<UnsignedByteBuffer> voxels}.
     *
     * @param voxels the voxels to perform the ersion on
     * @param iterations how many iterations of erosion
     * @param background
     * @param minIntensityValue
     * @param useZ whether to use the Z dimension or not during the erosion
     * @param acceptConditionsDilation conditions applied on each iteration of the erosion N.B. but
     *     applied on an inverted-version when passes to Dilate
     * @return
     * @throws CreateException
     */
    public static BinaryVoxels<UnsignedByteBuffer> erode(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            int iterations,
            Optional<Voxels<UnsignedByteBuffer>> background,
            int minIntensityValue,
            boolean useZ,
            Optional<AcceptIterationPredicate>
                    acceptConditionsDilation // NB applied on an inverted-version of the binary
            // buffer!!!
            ) throws CreateException {

        KernelApplicationParameters parameters = new KernelApplicationParameters(
                OutsideKernelPolicy.AS_ON,
                useZ
        );
        voxels.invert();
        BinaryVoxels<UnsignedByteBuffer> dilated =
                MorphologicalDilation.dilate(
                        voxels,
                        iterations,
                        background,
                        minIntensityValue,
                        acceptConditionsDilation,
                        new DilationKernelFactory(
                                parameters, false)
                        );
        dilated.invert();
        return dilated;
    }
}
