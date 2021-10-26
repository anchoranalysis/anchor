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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.slice.SliceBufferIndex;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * A box (3-dimensions) with voxel-data.
 *
 * <p>This class is almost <i>immutable</i>, with the exception of the buffers containing intensity
 * values which can be modified.
 *
 * <p>All operations that can modify the state (i.e. <i>mutable</i> operations) are provided via the
 * {@link #assignValue} or {@link #arithmetic()} or {@link #replaceSlice} or {@link #slice} or
 * {@link #sliceBuffer} methods. Other operations are all <i>immutable</i>.
 * 
 * <p>See {@link VoxelsUntyped} for a similar class that exposes the voxel-data type as a run-time accessible
 * field.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class Voxels<T> {

    /** The maximum number of rows/columns/slices to show in {@link #toString}. */
    private static final Extent MAX_IN_TO_STRING = new Extent(100, 100, 5);

    /**
     * An index mapping slice of voxels (in the z dimension) to a particular buffer with the
     * corresponding voxel intensities.
     */
    @Getter private final SliceBufferIndex<T> slices;

    /** A factory for creating voxels with a particular buffer-type. */
    @Getter private final VoxelsFactoryTypeBound<T> factory;

    /** Interface that allows manipulation of voxel intensities via arithmetic operations. */
    @Getter private final VoxelsArithmetic arithmetic;

    /**
     * Interface that allows read/copy/duplication operations to be performed regarding the voxels
     * intensities.
     *
     * @return the interface.
     */
    public abstract VoxelsExtracter<T> extract();

    /**
     * Interface that allows assignment of a particular value to all or subsets of the voxels.
     *
     * @param valueToAssign the value to assign.
     * @return the interface.
     */
    public abstract VoxelsAssigner assignValue(int valueToAssign);

    /**
     * The underlying data-type of the voxels, represented by a {@link VoxelDataType} instance.
     *
     * @return an instance of {@link VoxelDataType}.
     */
    public VoxelDataType dataType() {
        return factory.dataType();
    }

    /**
     * A {@link VoxelBuffer} corresponding to a particular z-slice.
     *
     * @param z the index (beginning at 0) of all z-slices.
     * @return the corresponding buffer for {@code z}.
     */
    public VoxelBuffer<T> slice(int z) {
        return slices.slice(z);
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
        return slice(z).buffer();
    }

    /**
     * The size of the voxels across three dimensions.
     *
     * @return the size.
     */
    public Extent extent() {
        return slices.extent();
    }

    /**
     * A deep-copy.
     *
     * @return newly created deep-copy.
     */
    public Voxels<T> duplicate() {
        Voxels<T> out = factory.createInitialized(slices().extent());

        extent().iterateOverZ(z -> out.replaceSlice(z, slice(z).duplicate()));

        return out;
    }

    /**
     * Are the voxels identical to another voxels (deep equals)?
     *
     * @param other the other voxels to compare with.
     * @return true if the size, data-type and each voxel-value of both are identical.
     */
    public boolean equalsDeep(Voxels<?> other) {

        if (!factory.dataType().equals(other.factory().dataType())) {
            return false;
        }

        if (!extent().equals(other.extent())) {
            return false;
        }

        return extent().iterateOverZUntil(z -> sliceBuffer(z).equals(other.sliceBuffer(z)));
    }

    /**
     * Assigns a new buffer for a slice.
     *
     * <p>This is a <b>mutable</b> operation.
     *
     * @param sliceIndexToUpdate slice-index to update.
     * @param bufferToAssign buffer to assign.
     */
    public void replaceSlice(int sliceIndexToUpdate, VoxelBuffer<T> bufferToAssign) {
        slices().replaceSlice(sliceIndexToUpdate, bufferToAssign);
    }

    /***
     * Print a description and the the first {link #MAX_NUMBER_ROWS_COLUMNS_IN_TO_STRING} rows and columns as values.
     */
    @Override
    public String toString() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(output)) {
            stream.printf("The voxels have dimensionality: %s%n", extent());

            Extent bounds = extent().minimum(MAX_IN_TO_STRING);
            bounds.iterateOverZ(sliceIndex -> printSlice(stream, sliceIndex, bounds));
        }
        return output.toString();
    }

    private void printSlice(PrintStream stream, int sliceIndex, Extent bounds) {
        stream.printf(
                "Showing slice %d, the first %d rows and %d columns:%n",
                sliceIndex, bounds.y(), bounds.x());
        Point3i point = new Point3i(0, 0, sliceIndex);
        for (point.setY(0); point.y() < bounds.y(); point.incrementY()) {
            for (point.setX(0); point.x() < bounds.x(); point.incrementX()) {
                stream.printf("%03d ", this.extract().voxel(point));
            }
            stream.println();
        }
    }
}
