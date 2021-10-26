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

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Stores memory buffers representing image voxels, without explicit typing of buffers.
 * 
 * <p>This is a convenience class to avoid using a templated parameter in {@link Voxels}.
 * 
 * <p>It stores the voxels in a <a href="https://en.wikipedia.org/wiki/Strong_and_weak_typing">weakly-typed manner</a>, and gives convenience methods to convert to the desired type.
 * 
 * <p>An {@link IncorrectVoxelTypeException} is thrown when incorrect assumptions are made about the type.
 *
 * @author Owen Feehan
 */
public class VoxelsUntyped {

    private Voxels<?> voxels;

    /**
     * Creates to wrap a {@link Voxels} of unspecified type.
     * 
     * @param voxels the voxels to wrap, whose memory is reused without duplication.
     */
    public VoxelsUntyped(Voxels<?> voxels) {
        this.voxels = voxels;
    }

    /** 
     * Exposes without any specific buffer type.
     *
     * @return the current object, without typing on the buffer.
     */
    public Voxels<?> any() { // NOSONAR
        return voxels;
    }

    /**
     * Do the voxels have a data-type that is equal to {@code match}?
     * 
     * @param match the data-type the voxel must equal.
     * @return true iff the voxel data-type is equal.
     */
    public Voxels<?> checkIdenticalDataType(VoxelDataType match) { // NOSONAR
        if (match.equals(voxels.dataType())) {
            return voxels;
        } else {
            throw new IncorrectVoxelTypeException(
                    String.format("Incompatible data-type %s has been requested from voxels with data-type %s", match, voxels.dataType()));
        }
    }

    /**
     * Casts to use a {@link UnsignedByteBuffer} if the voxels contain this data-type, otherwise throws a {@link IncorrectVoxelTypeException}.
     * 
     * @return the cast voxels.
     */
    @SuppressWarnings("unchecked")
    public Voxels<UnsignedByteBuffer> asByte() {

        if (!voxels.dataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Voxels do not contain unsigned 8-bit data (byte)");
        }

        return (Voxels<UnsignedByteBuffer>) voxels;
    }

    /**
     * Casts to use a {@link UnsignedShortBuffer} if the voxels contain this data-type, otherwise throws a {@link IncorrectVoxelTypeException}.
     * 
     * @return the cast voxels.
     */
    @SuppressWarnings("unchecked")
    public Voxels<UnsignedShortBuffer> asShort() {

        if (!voxels.dataType().equals(UnsignedShortVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Voxels do not contain unsigned 16-bit data (int)");
        }

        return (Voxels<UnsignedShortBuffer>) voxels;
    }

    /**
     * Casts to use a {@link UnsignedIntBuffer} if the voxels contain this data-type, otherwise throws a {@link IncorrectVoxelTypeException}.
     * 
     * @return the cast voxels.
     */
    @SuppressWarnings("unchecked")
    public Voxels<UnsignedIntBuffer> asInt() {

        if (!voxels.dataType().equals(UnsignedIntVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Voxels do not contain unsigned 32-bit data (int)");
        }

        return (Voxels<UnsignedIntBuffer>) voxels;
    }
    
    /**
     * Casts to use a {@link FloatBuffer} if the voxels contain this data-type, otherwise throws a {@link IncorrectVoxelTypeException}.
     * 
     * @return the cast voxels.
     */
    @SuppressWarnings("unchecked")
    public Voxels<FloatBuffer> asFloat() {

        if (!voxels.dataType().equals(FloatVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException("Voxels do not contain float data");
        }

        return (Voxels<FloatBuffer>) voxels;
    }

    /**
     * The underlying data-type of the voxels, represented by a {@link VoxelDataType} instance.
     *
     * @return an instance of {@link VoxelDataType}.
     */
    public VoxelDataType getVoxelDataType() {
        return voxels.dataType();
    }

    /**
     * Copies the voxels into a {@code destination}, but only those voxels inside a bounding-box.
     * 
     * @param boxSource the bounding-box relative to the source voxels (the current voxels - from where we copy from).
     * @param destination the voxels we copy into.
     * @param boxDestination the bounding-box relative to the destination voxels.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyVoxelsTo(
            BoundingBox boxSource, VoxelsUntyped destination, BoundingBox boxDestination) {
        voxels.extract()
                .boxCopyTo(boxSource, destinationVoxelsChecked(destination), boxDestination);
    }

    /**
     * Copies the voxels into a {@code destination}, but only those voxels inside an {@link ObjectMask}.
     * 
     * @param objectSource the object-mask relative to the source voxels, from where we copy from.
     * @param destination the voxels we copy into.
     * @param boxDestination the bounding-box relative to the destination voxels.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyVoxelsTo(
            ObjectMask objectSource, VoxelsUntyped destination, BoundingBox boxDestination) {
        voxels.extract()
                .objectCopyTo(objectSource, destinationVoxelsChecked(destination), boxDestination);
    }
    
    /**
     * Subtracts all voxel-values from the maximum value associated with the data-type.
     *
     * <p>i.e. each voxel value {@code v} is updated to become {@code maxDataTypeValue - v}
     */
    public void subtractFromMaxValue() {
        int maxValue = (int) getVoxelDataType().maxValue();
        voxels.arithmetic().subtractFrom(maxValue);
    }

    /**
     * Copies one particular z-slice of voxels from a source into the current voxels.
     * 
     * <p>The existing z-slice is replaced.
     *
     * @param sliceIndexToUpdate slice-index to update in the current voxels.
     * @param sourceVoxels voxels to copy a particular z-slice from.
     * @param sliceIndexSource the z-slice in {@code sourceVoxels} to copy from.
     * @param duplicate if true, the source slice is duplicated before being assigned. Otherwise it is reused.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void replaceSlice(
            int sliceIndexToUpdate,
            VoxelsUntyped sourceVoxels,
            int sliceIndexSource,
            boolean duplicate) {
        checkMatchingDataTypes(sourceVoxels.getVoxelDataType(), getVoxelDataType());
        voxels.replaceSlice(
                sliceIndexToUpdate,
                sourceSlice((Voxels) sourceVoxels.any(), sliceIndexSource, duplicate));
    }

    /**
     * A {@link VoxelBuffer} corresponding to a particular z-slice.
     *
     * @param z the index (beginning at 0) of all z-slices.
     * @return the corresponding buffer for {@code z}.
     */
    @SuppressWarnings("unchecked")
    public <T> VoxelBuffer<T> slice(int z) {
        return (VoxelBuffer<T>) voxels.slice(z);
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
     * Interface that allows manipulation of voxel intensities via arithmetic operations.
     *
     * @return the interface.
     */
    public VoxelsArithmetic arithmetic() {
        return voxels.arithmetic();
    }

    /**
     * Interface that allows assignment of a particular value to all or subsets of the voxels.
     *
     * @param valueToAssign the value to assign.
     * @return the interface.
     */
    public VoxelsAssigner assignValue(int valueToAssign) {
        return voxels.assignValue(valueToAssign);
    }

    /**
     * Interface that allows read/copy/duplication operations to be performed regarding the voxels
     * intensities.
     *
     * @return the interface.
     */
    public VoxelsExtracter<?> extract() { // NOSONAR
        return voxels.extract();
    }
    
    /**
     * Converts a {@link VoxelsUntyped} to a raw-type but only if it has the expected data-type.
     * 
     * <p>Otherwise throws a {@link IncorrectVoxelTypeException}.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Voxels destinationVoxelsChecked(VoxelsUntyped destination) {    // NOSONAR
        checkMatchingDataTypes(getVoxelDataType(), destination.getVoxelDataType());
        return destination.checkIdenticalDataType(voxels.dataType());
    }
    
    private static void checkMatchingDataTypes(VoxelDataType source, VoxelDataType destination) {
        if (!destination.equals(source)) {
            throw new IncorrectVoxelTypeException(
                    String.format("Voxel data-types do not match in source (%s) and destination (%s).", source, destination));
        }        
    }
    
    /** A particular z-slice from {@code sourceVoxels}, maybe duplicated. */
    private static <T> VoxelBuffer<T> sourceSlice(
            Voxels<T> sourceVoxels, int sliceIndex, boolean duplicate) {
        VoxelBuffer<T> slice = sourceVoxels.slice(sliceIndex); 
        if (duplicate) {
            return slice;
        } else {
            return slice.duplicate();
        }
    }
}
