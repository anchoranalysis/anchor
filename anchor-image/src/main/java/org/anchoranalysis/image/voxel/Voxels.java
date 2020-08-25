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

package org.anchoranalysis.image.voxel;

import java.nio.Buffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.pixelsforslice.PixelsForSlice;

/**
 * A box (3-dimensions) with voxel-data.
 *
 * <p>This class is almost <i>immutable</i> the exception being the buffers containing intensity
 * values which can be modified.
 *
 * <p>All operations that can modify the state (i.e. <i>mutable</i> operations) are provided via the
 * {@link #assignValue} or {@link #arithmetic()} or {@link #replaceSlice} or {@link #slice} or
 * {@link #sliceBuffer} methods. Other operations are all <i>immutable</i>.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class Voxels<T extends Buffer> {

    @Getter private final PixelsForSlice<T> slices;
    @Getter private final VoxelsFactoryTypeBound<T> factory;

    /** Methods to manipulate the voxel-values via arithmetic */
    @Getter private final VoxelsArithmetic arithmetic;

    /** Methods to read/copy/duplicate the voxel-values */
    public abstract VoxelsExtracter<T> extract();

    /**
     * Provides a means to assign a constant-value to some or all of the voxels
     *
     * @param valueToAssign
     * @return a newly instantiated object to perform assignments to this voxels object
     */
    public abstract VoxelsAssigner assignValue(int valueToAssign);

    public VoxelDataType dataType() {
        return factory.dataType();
    }

    public VoxelBuffer<T> slice(int z) {
        return slices.slice(z);
    }

    public T sliceBuffer(int z) {
        return slice(z).buffer();
    }

    public Extent extent() {
        return slices.extent();
    }

    public Voxels<T> duplicate() {
        Voxels<T> bufferAccess = factory.createInitialized(slices().extent());

        for (int z = 0; z < extent().z(); z++) {
            bufferAccess.replaceSlice(z, slice(z).duplicate());
        }

        return bufferAccess;
    }

    /**
     * Are the voxels identical to another voxels (deep equals)?
     *
     * @param other the other voxels to compare with
     * @return true if the size, data-type and each voxel-value of both are identical
     */
    @SuppressWarnings("unchecked")
    public boolean equalsDeep(Voxels<?> other) {

        if (!factory.dataType().equals(other.factory().dataType())) {
            return false;
        }

        if (!extent().equals(other.extent())) {
            return false;
        }

        extent().iterateOverZUntil(
                        z -> {
                            T buffer1 = sliceBuffer(z);
                            T buffer2 = (T) other.sliceBuffer(z);

                            while (buffer1.hasRemaining()) {

                                if (!areBufferValuesEqual(buffer1, buffer2)) {
                                    return false;
                                }
                            }

                            assert (!buffer2.hasRemaining());
                            return true;
                        });

        return true;
    }

    /**
     * Assigns a new buffer for a slice
     *
     * <p>This is a <b>mutable</b> operation.
     *
     * @param sliceIndexToUpdate slice-index to update
     * @param bufferToAssign buffer to assign
     */
    public void replaceSlice(int sliceIndexToUpdate, VoxelBuffer<T> bufferToAssign) {
        slices().replaceSlice(sliceIndexToUpdate, bufferToAssign);
    }

    /**
     * Checks if the current values from <i>two buffers are equal</i>
     *
     * <p>(i.e. by calling {@code get()} on the buffer)
     *
     * @param buffer1 provides first-value to compare
     * @param buffer2 provides second-value to compare
     * @return true iff the current values from both buffers are equal to each other
     */
    protected abstract boolean areBufferValuesEqual(T buffer1, T buffer2);
}
