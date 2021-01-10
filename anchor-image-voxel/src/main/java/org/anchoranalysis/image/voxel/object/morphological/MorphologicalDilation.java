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
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.kernel.morphological.DilationContext;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.morphological.predicate.AcceptIterationPredicate;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MorphologicalDilation {

    /**
     * Dilates an object-mask, growing the bounding-box as necessary.
     *
     * @param object the object to dilate
     * @param extent if present, restricts the object to remain within certain bounds
     * @param useZ whether to perform dilation in 2D or 3D
     * @param iterations number of dilations to perform
     * @return a newly created object-mask with bounding-box grown in relevant directions by {@code
     *     iterations}
     * @throws CreateException
     */
    public static ObjectMask createDilatedObject(
            ObjectMask object,
            Optional<Extent> extent,
            boolean useZ,
            int iterations,
            boolean bigNeighborhood)
            throws CreateException {

        Point3i grow =
                useZ
                        ? new Point3i(iterations, iterations, iterations)
                        : new Point3i(iterations, iterations, 0);

        DilationContext context =
                new DilationContext(
                        OutsideKernelPolicy.IGNORE_OUTSIDE,
                        useZ,
                        bigNeighborhood,
                        Optional.empty());

        try {
            ObjectMask objectGrown = object.growBuffer(grow, grow, extent);
            BinaryVoxels<UnsignedByteBuffer> dilated =
                    dilate(objectGrown.binaryVoxels(), iterations, context);
            return objectGrown.replaceVoxels(dilated.voxels());
        } catch (OperationFailedException e) {
            throw new CreateException("Cannot grow object-mask", e);
        }
    }

    /**
     * Performs a morphological dilation operation.
     *
     * @param voxels input-voxels
     * @param iterations number of dilations
     * @param context additional parameters for influencing how dilation occurs.
     * @return a new buffer containing the results of the dilation-operations
     * @throws CreateException
     */
    public static BinaryVoxels<UnsignedByteBuffer> dilate(
            BinaryVoxels<UnsignedByteBuffer> voxels, int iterations, DilationContext context)
            throws CreateException {

        BinaryKernel kernel = context.createKernel();
        Optional<AcceptIterationPredicate> postcondition = context.getPostcondition();

        for (int i = 0; i < iterations; i++) {
            BinaryVoxels<UnsignedByteBuffer> next =
                    ApplyKernel.apply(kernel, voxels, context.getKernelApplication());

            try {
                if (postcondition.isPresent() && !postcondition.get().acceptIteration(next)) {
                    break;
                }
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }

            voxels = next;
        }
        return voxels;
    }
}
