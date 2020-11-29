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

package org.anchoranalysis.image.voxel.convert;

import java.nio.FloatBuffer;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsRemaining;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinaryWithoutOffset;

/**
 * Converts voxels from one data-type to another.
 *
 * @author Owen Feehan
 * @param <T> destination-type (what the voxels will be converted <b>to</b>)
 */
public abstract class VoxelsConverter<T> {

    /**
     * Creates a new voxels of type {@code T} and copies the voxels from {@code from}.
     *
     * @param from where the voxels are copied from (the source)
     * @param factory a factory that creates {@link Voxels} of tyoe {@code T}.
     * @return a newly created {@link Voxels} with values copied from {@code source}.
     */
    public Voxels<T> convertFrom(VoxelsWrapper from, VoxelsFactoryTypeBound<T> factory) {
        Voxels<T> voxelsOut = factory.createInitialized(from.any().extent());
        try {
            copyFrom(from, voxelsOut);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
        return voxelsOut;
    }

    /**
     * Copies voxels from a source (of any type) to voxels of type {@code T}.
     *
     * @param from where the voxels are copied from (the source)
     * @param to where the voxels are copied to (the destination)
     * @throws OperationFailedException if the extents of {@code from} and {@code to} are not equal.
     */
    public void copyFrom(VoxelsWrapper from, Voxels<T> to) throws OperationFailedException {

        VoxelDataType fromType = from.getVoxelDataType();
        if (fromType.equals(UnsignedByteVoxelType.INSTANCE)) {
            copyFromUnsignedByte(from.asByte(), to);
        } else if (fromType.equals(FloatVoxelType.INSTANCE)) {
            copyFromFloat(from.asFloat(), to);
        } else if (fromType.equals(UnsignedShortVoxelType.INSTANCE)) {
            copyFromUnsignedShort(from.asShort(), to);
        } else if (fromType.equals(UnsignedIntVoxelType.INSTANCE)) {
            copyFromUnsignedInt(from.asInt(), to);
        }
    }

    /**
     * Copies voxels from a source of type @{link UnsignedByteBuffer} to voxels of type {@code T}.
     *
     * @param from where the voxels are copied from (the source)
     * @param to where the voxels are copied to (the destination)
     * @throws OperationFailedException if the extents of {@code from} and {@code to} are not equal.
     */
    public void copyFromUnsignedByte(Voxels<UnsignedByteBuffer> from, Voxels<T> to)
            throws OperationFailedException {
        convertAllSlices(from, to, this::convertUnsignedByte);
    }

    /**
     * Copies voxels from a source of type @{link UnsignedShortBuffer} to voxels of type {@code T}.
     *
     * @param from where the voxels are copied from (the source)
     * @param to where the voxels are copied to (the destination)
     * @throws OperationFailedException if the extents of {@code from} and {@code to} are not equal.
     */
    public void copyFromUnsignedShort(Voxels<UnsignedShortBuffer> from, Voxels<T> to)
            throws OperationFailedException {
        convertAllSlices(from, to, this::convertUnsignedShort);
    }

    /**
     * Copies voxels from a source of type @{link UnsignedIntBuffer} to voxels of type {@code T}.
     *
     * @param from where the voxels are copied from (the source)
     * @param to where the voxels are copied to (the destination)
     * @throws OperationFailedException if the extents of {@code from} and {@code to} are not equal.
     */
    public void copyFromUnsignedInt(Voxels<UnsignedIntBuffer> from, Voxels<T> to)
            throws OperationFailedException {
        convertAllSlices(from, to, this::convertUnsignedInt);
    }

    /**
     * Copies voxels from a source of type @{link FloatBuffer} to voxels of type {@code T}.
     *
     * @param from where the voxels are copied from (the source)
     * @param to where the voxels are copied to (the destination)
     * @throws OperationFailedException if the extents of {@code from} and {@code to} are not equal.
     */
    public void copyFromFloat(Voxels<FloatBuffer> from, Voxels<T> to)
            throws OperationFailedException {
        convertAllSlices(from, to, this::convertFloat);
    }

    /**
     * Copies a value from the current position in a {@link UnsignedByteBuffer} to the current
     * position in a buffer of type {@code T}.
     *
     * @param in the current position of this buffer gives the value to convert, and the position is
     *     incremented.
     * @param out the converted value is written to the current position of this buffer, and the
     *     position is incremented.
     */
    protected abstract void convertUnsignedByte(UnsignedByteBuffer in, T out);

    /**
     * Copies a value from the current position in a {@link UnsignedShortBuffer} to the current
     * position in a buffer of type {@code T}.
     *
     * @param in the current position of this buffer gives the value to convert, and the position is
     *     incremented.
     * @param out the converted value is written to the current position of this buffer, and the
     *     position is incremented.
     */
    protected abstract void convertUnsignedShort(UnsignedShortBuffer in, T out);

    /**
     * Copies a value from the current position in a {@link UnsignedIntBuffer} to the current
     * position in a buffer of type {@code T}.
     *
     * @param in the current position of this buffer gives the value to convert, and the position is
     *     incremented.
     * @param out the converted value is written to the current position of this buffer, and the
     *     position is incremented.
     */
    protected abstract void convertUnsignedInt(UnsignedIntBuffer in, T out);

    /**
     * Copies a value from the current position in a {@link FloatBuffer} to the current position in
     * a buffer of type {@code T}.
     *
     * @param in the current position of this buffer gives the value to convert, and the position is
     *     incremented.
     * @param out the converted value is written to the current position of this buffer, and the
     *     position is incremented.
     */
    protected abstract void convertFloat(FloatBuffer in, T out);

    private <S> void convertAllSlices(
            Voxels<S> in, Voxels<T> out, ProcessBufferBinaryWithoutOffset<S, T> process)
            throws OperationFailedException {

        if (!in.extent().equals(out.extent())) {
            throw new OperationFailedException(
                    String.format(
                            "The extent of the source (%s) is not equal to the destination (%s)",
                            in, out));
        }
        in.extent()
                .iterateOverZ(
                        z ->
                                IterateVoxelsRemaining.withTwoBuffersWithoutOffset(
                                        in.slice(z), out.slice(z), process));
    }
}
