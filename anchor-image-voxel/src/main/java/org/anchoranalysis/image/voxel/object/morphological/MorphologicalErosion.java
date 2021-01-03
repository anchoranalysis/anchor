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
import org.anchoranalysis.image.voxel.kernel.morphological.DilationKernelFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.morphological.predicate.AcceptIterationPredicate;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MorphologicalErosion {

    public static ObjectMask createErodedObject(
            ObjectMask object,
            Optional<Extent> extent,
            boolean do3D,
            int iterations,
            boolean outsideAtThreshold,
            Optional<AcceptIterationPredicate>
                    acceptConditionsDilation // NB applied on an inverted-version of the binary
            // buffer!!!
            ) throws CreateException {

        ObjectMask objectOut;

        // TODO
        // We can make this more efficient, than remaking an object-mask needlessly
        //  by having a smarter "isOutside" check in the Erosion routine
        if (!outsideAtThreshold) {
            // If we want to treat the outside of the image as if it's at a threshold, then
            //  we put an extra 1-pixel border around the object-mask, so that there's always
            //  whitespace around the object-mask, so long as it exists in the image scene
            BoundingBox box = object.boundedVoxels().dilate(do3D, extent);
            objectOut = object.regionIntersecting(box);

        } else {
            objectOut = object.duplicate();
        }

        BinaryVoxels<UnsignedByteBuffer> eroded =
                erode(
                        objectOut.binaryVoxels(),
                        do3D,
                        iterations,
                        Optional.empty(),
                        0,
                        outsideAtThreshold,
                        acceptConditionsDilation);
        return objectOut.replaceVoxels(eroded.voxels());
    }

    /**
     * Performs a morphological erosion by dilating an inverted version of the object
     *
     * @param binaryValues
     * @param do3D
     * @param iterations
     * @param backgroundVb
     * @param minIntensityValue
     * @param outsideAtThreshold
     * @param acceptConditionsDilation conditions applied on each iteration of the erosion N.B. but
     *     applied on an inverted-version when passes to Dilate
     * @return
     * @throws CreateException
     */
    public static BinaryVoxels<UnsignedByteBuffer> erode(
            BinaryVoxels<UnsignedByteBuffer> binaryValues,
            boolean do3D,
            int iterations,
            Optional<Voxels<UnsignedByteBuffer>> backgroundVb,
            int minIntensityValue,
            boolean outsideAtThreshold,
            Optional<AcceptIterationPredicate>
                    acceptConditionsDilation // NB applied on an inverted-version of the binary
            // buffer!!!
            ) throws CreateException {

        binaryValues.invert();
        BinaryVoxels<UnsignedByteBuffer> dilated =
                MorphologicalDilation.dilate(
                        binaryValues,
                        iterations,
                        backgroundVb,
                        minIntensityValue,
                        acceptConditionsDilation,
                        new DilationKernelFactory(do3D, outsideAtThreshold, false));
        dilated.invert();
        return dilated;
    }
}
