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
import org.anchoranalysis.spatial.Extent;
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
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class Voxels<T> {

    /** The maximum number of rows/columns/slices to show in {@link #toString}. */
    private static final Extent MAX_IN_TO_STRING = new Extent(100, 100, 5);

    @Getter private final SliceBufferIndex<T> slices;
    @Getter private final VoxelsFactoryTypeBound<T> factory;

    /** Methods to manipulate the voxel-values via arithmetic. */
    @Getter private final VoxelsArithmetic arithmetic;

    /** Methods to read/copy/duplicate the voxel-values. */
    public abstract VoxelsExtracter<T> extract();

    /**
     * Provides a means to assign a constant-value to some or all of the voxels.
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
     * @param other the other voxels to compare with
     * @return true if the size, data-type and each voxel-value of both are identical
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
     * @param sliceIndexToUpdate slice-index to update
     * @param bufferToAssign buffer to assign
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
