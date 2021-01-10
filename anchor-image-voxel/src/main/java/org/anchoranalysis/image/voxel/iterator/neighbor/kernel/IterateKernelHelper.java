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
package org.anchoranalysis.image.voxel.iterator.neighbor.kernel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsBoundingBox;
import org.anchoranalysis.image.voxel.iterator.predicate.PredicateKernelPointCursor;
import org.anchoranalysis.image.voxel.iterator.process.ProcessKernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.Kernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Routines to iterate a kernel over a set of points in a {@link BinaryVoxels}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class IterateKernelHelper {

    public static void overAll(
            Kernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters params,
            ProcessKernelPointCursor processor) {
        AddLocalSlicesProcessor process =
                new AddLocalSlicesProcessor(kernel, voxels, processor, params.isUseZ());
        IterateVoxelsAll.withCursor(voxels, params, process);
    }

    public static void overBox(
            Kernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params,
            ProcessKernelPointCursor processor)
            throws OperationFailedException {
        checkBoxInsideVoxels(voxels, box);
        AddLocalSlicesProcessor processorWithSlices =
                new AddLocalSlicesProcessor(kernel, voxels, processor, params.isUseZ());
        IterateVoxelsBoundingBox.withCursor(voxels, box, params, processorWithSlices);
    }

    public static boolean overBoxUntil(
            Kernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params,
            PredicateKernelPointCursor predicate)
            throws OperationFailedException {
        checkBoxInsideVoxels(voxels, box);
        AddLocalSlicesPredicate predicateWithSlices =
                new AddLocalSlicesPredicate(kernel, voxels, predicate, params.isUseZ());
        return IterateVoxelsBoundingBox.withCursorUntil(voxels, box, params, predicateWithSlices);
    }

    private static void checkBoxInsideVoxels(
            BinaryVoxels<UnsignedByteBuffer> voxels, BoundingBox box)
            throws OperationFailedException {
        if (!voxels.extent().contains(box)) {
            throw new OperationFailedException(
                    String.format(
                            "Bounding-box (%s) must be contained within extent (%s)",
                            box, voxels.extent()));
        }
    }

    private abstract static class AddSlices {

        private final Kernel kernel;
        private final BinaryVoxels<UnsignedByteBuffer> voxels;
        private final int slicesNeeded;

        protected AddSlices(Kernel kernel, BinaryVoxels<UnsignedByteBuffer> voxels, boolean useZ) {
            this.kernel = kernel;
            this.voxels = voxels;
            slicesNeeded = useZ ? kernel.getSize() : 1;
        }

        protected void notifyZChangeToKernel(int z) {
            kernel.notifyZChange(new LocalSlices(z, slicesNeeded, voxels.voxels()), z);
        }
    }

    private static class AddLocalSlicesProcessor extends AddSlices
            implements ProcessKernelPointCursor {

        private final ProcessKernelPointCursor processor;

        public AddLocalSlicesProcessor(
                Kernel kernel,
                BinaryVoxels<UnsignedByteBuffer> voxels,
                ProcessKernelPointCursor processor,
                boolean useZ) {
            super(kernel, voxels, useZ);
            this.processor = processor;
        }

        @Override
        public void notifyChangeSlice(int z) {
            notifyZChangeToKernel(z);
            processor.notifyChangeSlice(z);
        }

        @Override
        public void process(KernelPointCursor point) {
            processor.process(point);
        }
    }

    private static class AddLocalSlicesPredicate extends AddSlices
            implements PredicateKernelPointCursor {

        private final PredicateKernelPointCursor predicate;

        public AddLocalSlicesPredicate(
                Kernel kernel,
                BinaryVoxels<UnsignedByteBuffer> voxels,
                PredicateKernelPointCursor predicate,
                boolean useZ) {
            super(kernel, voxels, useZ);
            this.predicate = predicate;
        }

        @Override
        public void notifyChangeSlice(int z) {
            notifyZChangeToKernel(z);
            predicate.notifyChangeSlice(z);
        }

        @Override
        public boolean test(KernelPointCursor point) {
            return predicate.test(point);
        }
    }
}
