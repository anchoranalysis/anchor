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

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.voxel.Voxels;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsAssignerFactory {

    /**
     * Create voxels-assigner for {@link UnsignedByteBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createByte(Voxels<UnsignedByteBuffer> voxels, int valueToAssign) {
        return new ByteImplementation(voxels, valueToAssign);
    }

    /**
     * Create voxels-assigner for {@link ShortBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createShort(Voxels<ShortBuffer> voxels, int valueToAssign) {
        return new ShortImplementation(voxels, valueToAssign);
    }

    /**
     * Create voxels-assigner for {@link FloatBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createFloat(Voxels<FloatBuffer> voxels, int valueToAssign) {
        return new FloatImplementation(voxels, valueToAssign);
    }

    /**
     * Create voxels-assigner for {@link IntBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createInt(Voxels<IntBuffer> voxels, int valueToAssign) {
        return new IntImplementation(voxels, valueToAssign);
    }

    /**
     * Shifts all coordinates BACKWARDS before passing to another {@link VoxelsAssigner}
     *
     * <p>This is useful for translating from global coordinates to relative coordinates e.g.
     * translating the global coordinate systems used in {@code BoundedVoxels} to relative
     * coordinates for underlying voxel buffer.
     *
     * @param voxelsAssigner the delegate where the shifted coordinates are passed to
     * @param shift how much to shift back by
     * @return a newly created assigner that performs a shift after calling the existing assigner
     */
    public static VoxelsAssigner shiftBackBy(VoxelsAssigner voxelsAssigner, ReadableTuple3i shift) {
        return new ShiftBackwardsBy(voxelsAssigner, shift);
    }
}
