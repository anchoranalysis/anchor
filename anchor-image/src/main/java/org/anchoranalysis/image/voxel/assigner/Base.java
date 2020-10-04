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
package org.anchoranalysis.image.voxel.assigner;

import java.util.Optional;
import java.util.function.IntPredicate;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsObjectMask;

@RequiredArgsConstructor
abstract class Base<T> implements VoxelsAssigner {

    // START REQUIRED ARGUMENTS
    /** The voxels that are assigned to */
    private final Voxels<T> voxels;

    /** The voxel-value to assign */
    protected final int valueToAssign;
    // END REQUIRED ARGUMENTS

    @Override
    public void toAll() {
        voxels.extent().iterateOverZ(z -> assignToEntireBuffer(voxels.sliceBuffer(z)));
    }

    @Override
    public void toBox(BoundingBox box) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();
        Extent extent = voxels.extent();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            T buffer = voxels.sliceBuffer(z);

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    int offset = extent.offset(x, y);
                    assignAtBufferPosition(buffer, offset);
                }
            }
        }
    }

    @Override
    public void toVoxel(int x, int y, int z) {
        T buffer = voxels.sliceBuffer(z);
        assignAtBufferPosition(buffer, voxels.extent().offset(x, y));
    }

    /**
     * Assigns the constant value at current and all remaining positions in the buffer
     *
     * @param buffer the buffer (set to a particular position)
     */
    protected abstract void assignToEntireBuffer(T buffer);

    /**
     * Assigns the constant value at a particular position in the buffer
     *
     * @param buffer the buffer
     * @param index index of the positoion
     */
    protected abstract void assignAtBufferPosition(T buffer, int index);

    @Override
    public void toObject(ObjectMask object) {
        toObject(object, Optional.empty());
    }

    @Override
    public void toObjectIf(ObjectMask object, IntPredicate voxelPredicate) {
        IterateVoxelsObjectMask.withBuffer(
                object,
                voxels,
                (buffer, offset) -> {
                    int existingValue = buffer.getInt(offset);
                    if (voxelPredicate.test(existingValue)) {
                        assignToBuffer(buffer, offset);
                    }
                });
    }

    @Override
    public boolean toObjectWhile(ObjectMask object, IntPredicate voxelPredicate) {

        // First check if all voxels on the object match the predicate
        if (IterateVoxelsObjectMask.allMatchIntensity(object, voxels, voxelPredicate)) {
            toObject(object, Optional.empty());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void toObject(ObjectMask object, BoundingBox restrictTo) {
        toObject(object, Optional.of(restrictTo));
    }

    @Override
    public void toEitherTwoObjects( // NOSONAR
            ObjectMask object1, ObjectMask object2, BoundingBox restrictTo) {
        toObject(object1, restrictTo);
        toObject(object2, restrictTo);
    }

    /**
     * Sets voxels in a box to a particular value if they match a object-mask (but only a part of
     * the object-mask)
     *
     * <p>Pixels are unchanged if they do not match the mask, or outside the part of the mask that
     * is considered.
     *
     * <p>Bounding boxes can be used to restrict regions in both the mask and destination, but must
     * be equal in volume.
     *
     * @param object the object-mask to restrict where voxels are set
     * @param restrictTo optionally, a restriction on where in the object-mask to process (expressed
     *     in the same coordinates as {@code object}). Its extent must be equal to {@code
     *     boxToBeAssigned}.
     */
    private void toObject(ObjectMask object, Optional<BoundingBox> restrictTo) {
        IterateVoxelsObjectMask.withBuffer(
                object, voxels, restrictTo, this::assignToBuffer);
    }

    private void assignToBuffer(VoxelBuffer<T> buffer, int offset) {
        buffer.putInt(offset, valueToAssign);
    }
}
