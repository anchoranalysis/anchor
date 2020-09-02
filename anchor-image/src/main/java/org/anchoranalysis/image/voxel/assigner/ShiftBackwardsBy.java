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

import java.util.function.IntPredicate;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Shifts all coordinates BACKWARDS before passing to another {@link VoxelsAssigner}
 *
 * <p>This is useful for translating from global coordinates to relative coordinates e.g.
 * translating the global coordinate systems used in {@code BoundedVoxels} to relative coordinates
 * for underlying voxel buffer.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class ShiftBackwardsBy implements VoxelsAssigner {

    /** The delegate where the shifted coordinates are passed to */
    private final VoxelsAssigner voxelsAssigner;

    /** How much to shift back by */
    private final ReadableTuple3i shift;

    @Override
    public void toVoxel(int x, int y, int z) {
        voxelsAssigner.toVoxel(x - shift.x(), y - shift.y(), z - shift.z());
    }

    @Override
    public void toBox(BoundingBox box) {
        voxelsAssigner.toBox(shift(box));
    }

    @Override
    public void toAll() {
        voxelsAssigner.toAll();
    }

    @Override
    public void toObject(ObjectMask object) {
        voxelsAssigner.toObject(shift(object));
    }

    @Override
    public boolean toObject(ObjectMask object, IntPredicate voxelPredicate) {
        return voxelsAssigner.toObject(shift(object), voxelPredicate);
    }

    @Override
    public void toObject(ObjectMask object, BoundingBox restrictTo) {
        voxelsAssigner.toObject(shift(object), shift(restrictTo));
    }

    @Override
    public void toEitherTwoObjects(ObjectMask object1, ObjectMask object2, BoundingBox restrictTo) {
        voxelsAssigner.toEitherTwoObjects(shift(object1), shift(object2), shift(restrictTo));
    }

    private BoundingBox shift(BoundingBox box) {
        return box.shiftBackBy(shift);
    }

    private ObjectMask shift(ObjectMask object) {
        return object.shiftBackBy(shift);
    }
}
