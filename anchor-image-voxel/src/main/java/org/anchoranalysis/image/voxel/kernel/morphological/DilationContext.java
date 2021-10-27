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
package org.anchoranalysis.image.voxel.kernel.morphological;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.ConditionalKernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.object.morphological.predicate.AcceptIterationPredicate;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Additional parameters for influencing a morphologicla dilation opperation.
 *
 * @author Owen Feehan
 */
public class DilationContext {

    /** How the kernel is applied to the scene. */
    @Getter private final KernelApplicationParameters kernelApplication;

    /** If true, a big neighborhood is used 2D-plane (8-connected instead of 4-connected), but not in
     * Z-direction (remains always 2-connected). */
    private final boolean bigNeighborhood;

    /**
     * If defined, a condition which must be satisfied on a <i>voxel</i>, before any voxel can be
     * dilated.
     */
    private final Optional<Predicate<Point3i>> precondition;

    /**
     * If defined, a condition which must be satisfied <i>after</i> an iteration occurs, otherwise
     * no more iterations occur.
     */
    @Getter private final Optional<AcceptIterationPredicate> postcondition;

    /**
     * Create <i>without</i> a post-condition.
     * 
     * @param outsideKernelPolicy how to handle voxels whose neighbors are outside the scene.
     * @param useZ if true, the dilation also occurs in the Z-dimension, otherwise in the XY-dimensions only.
     * @param bigNeighborhood if true, a big neighborhood is used 2D-plane (8-connected instead of 4-connected), but not in
     * Z-direction (remains always 2-connected).
     * @param precondition if defined, a condition which must be satisfied on a <i>voxel</i>, before any voxel can be
     * dilated.
     */
    public DilationContext(
            OutsideKernelPolicy outsideKernelPolicy,
            boolean useZ,
            boolean bigNeighborhood,
            Optional<Predicate<Point3i>> precondition) {
        this(outsideKernelPolicy, useZ, bigNeighborhood, precondition, Optional.empty());
    }

    /**
     * Create <i>with</i> a post-condition.
     * 
     * @param outsideKernelPolicy how to handle voxels whose neighbors are outside the scene.
     * @param useZ if true, the dilation also occurs in the Z-dimension, otherwise in the XY-dimensions only.
     * @param bigNeighborhood if true, a big neighborhood is used 2D-plane (8-connected instead of 4-connected), but not in
     * Z-direction (remains always 2-connected).
     * @param precondition if defined, a condition which must be satisfied on a <i>voxel</i>, before any voxel can be
     * dilated.
     * @param postcondition if defined, a condition which must be satisfied <i>after</i> an iteration occurs, otherwise
     * no more iterations occur.
     */
    public DilationContext(
            OutsideKernelPolicy outsideKernelPolicy,
            boolean useZ,
            boolean bigNeighborhood,
            Optional<Predicate<Point3i>> precondition,
            Optional<AcceptIterationPredicate> postcondition) {
        this.kernelApplication = new KernelApplicationParameters(outsideKernelPolicy, useZ);
        this.bigNeighborhood = bigNeighborhood;
        this.precondition = precondition;
        this.postcondition = postcondition;
    }

    /**
     * Creates a kernel for performing the dilation.
     *
     * @return the kernel that performs the dilation.
     */
    public BinaryKernel createKernel() {

        BinaryKernel kernelDilation = new DilationKernel(bigNeighborhood);

        if (precondition.isPresent()) {
            return new ConditionalKernel(kernelDilation, precondition.get());
        } else {
            return kernelDilation;
        }
    }
}
