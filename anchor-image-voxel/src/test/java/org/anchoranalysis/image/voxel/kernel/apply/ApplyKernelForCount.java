/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.kernel.apply;

import java.util.function.BiFunction;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.Kernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.ObjectOnVoxelsHelper;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Applies a {@link Kernel} to a created {@link BinaryVoxels} showing an {@link ObjectMask}
 * resulting in a count.
 *
 * @author Owen Feehan
 * @param <T> kernel-type
 */
public abstract class ApplyKernelForCount<T extends Kernel> {

    /**
     * Applies the kernel and determines the resulting count.
     *
     * @param object the object to impose on the {@link BinaryVoxels}
     * @param extentScene the size of the {@link BinaryVoxels}
     * @param params parameters to use when applying.
     * @param createKernel a function to create the {@link Kernel}.
     * @return the count.
     */
    public int apply(
            ObjectMask object,
            Extent extentScene,
            KernelApplicationParameters params,
            BiFunction<ObjectMask, Extent, T> createKernel) {
        return applyToVoxelsAndCount(
                createKernel.apply(object, extentScene),
                createBinaryVoxels(object, extentScene),
                params);
    }

    /**
     * Applies the kernel to a {@link BinaryVoxels} and determines the resulting count.
     *
     * @param kernel the kernel to apply
     * @param voxels the binary-voxels
     * @param params parameters to use when applying.
     * @return the count.
     */
    protected abstract int applyToVoxelsAndCount(
            T kernel, BinaryVoxels<UnsignedByteBuffer> voxels, KernelApplicationParameters params);

    private static BinaryVoxels<UnsignedByteBuffer> createBinaryVoxels(
            ObjectMask object, Extent extentScene) {
        return ObjectOnVoxelsHelper.createVoxelsWithObject(object, extentScene, true);
    }
}
