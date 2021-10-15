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

package org.anchoranalysis.image.voxel.binary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.slice.SliceBufferIndex;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Like {@link Voxels} but should only contain two distinct intensity-values representing <i>on</i>
 * and <i>off</i>. states.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@AllArgsConstructor
@Accessors(fluent = true)
public abstract class BinaryVoxels<T> implements BinaryOnOffSetter {

    /**
     * Voxels that should only have two intensity-values (representing <i>on</i> and <i>off</i>
     * states). This is not checked as a precondition.
     */
    @Getter private final Voxels<T> voxels;

    /** Which two intensity values represent <i>off</i> and <i>on</i> states. */
    @Getter private BinaryValues binaryValues;

    /**
     * Changes the <i>off</i> state to be the <i>on</i> state and vice-versa.
     *
     * <p>Only the {@code binaryValues} (acting as an index to the intensity values) is changed; the
     * voxels remain themselves unchanged.
     */
    public void invert() {
        binaryValues = binaryValues.createInverted();
    }

    /**
     * The size of the voxels across three dimensions.
     *
     * @return the size.
     */
    public Extent extent() {
        return voxels.extent();
    }

    /**
     * At least one voxel exists with an <i>on</i> value.
     *
     * @return true iff at least one such voxel exists.
     */
    public boolean hasOnVoxel() {
        return voxels.extract().voxelsEqualTo(binaryValues.getOnInt()).anyExists();
    }

    /**
     * At least one voxel exists with an <i>off</i> value.
     *
     * @return true iff at least one such voxel exists.
     */
    public boolean hasOffVoxel() {
        return voxels.extract().voxelsEqualTo(binaryValues.getOffInt()).anyExists();
    }

    public abstract BinaryVoxels<T> duplicate();

    public BinaryVoxels<T> extractSlice(int z) throws CreateException {
        return binaryVoxelsFor(extract().slice(z), binaryValues());
    }

    protected abstract BinaryVoxels<T> binaryVoxelsFor(Voxels<T> slice, BinaryValues binaryValues);

    public void updateSlice(int z, VoxelBuffer<T> buffer) {
        voxels.replaceSlice(z, buffer);
    }

    public VoxelsAssigner assignOn() {
        return voxels.assignValue(binaryValues.getOnInt());
    }

    public VoxelsAssigner assignOff() {
        return voxels.assignValue(binaryValues.getOffInt());
    }

    public int countOn() {
        return voxels.extract().voxelsEqualTo(binaryValues.getOnInt()).count();
    }

    public int countOff() {
        return voxels.extract().voxelsEqualTo(binaryValues.getOffInt()).count();
    }

    /**
     * A buffer corresponding to a particular z-slice.
     *
     * <p>This buffer is either a NIO or other classes that wraps the underlying array storing voxel
     * intensities.
     *
     * @param z the index (beginning at 0) of all z-slices.
     * @return the corresponding buffer for {@code z}.
     */
    public T sliceBuffer(int z) {
        return voxels.sliceBuffer(z);
    }

    /**
     * A {@link VoxelBuffer} corresponding to a particular z-slice.
     *
     * @param z the index (beginning at 0) of all z-slices.
     * @return the corresponding buffer for {@code z}.
     */
    public VoxelBuffer<T> slice(int z) {
        return voxels.slice(z);
    }

    /**
     * Interface that allows read/copy/duplication operations to be performed regarding the voxels
     * intensities.
     *
     * @return the interface.
     */
    public VoxelsExtracter<T> extract() {
        return voxels.extract();
    }

    /**
     * An index mapping slice of voxels (in the z dimension) to a particular buffer with the
     * corresponding voxel intensities.
     *
     * @return the index.
     */
    public SliceBufferIndex<T> slices() {
        return voxels.slices();
    }

    @Override
    public String toString() {
        return voxels.toString();
    }
}
