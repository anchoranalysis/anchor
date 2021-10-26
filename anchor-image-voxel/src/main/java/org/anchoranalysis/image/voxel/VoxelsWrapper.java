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
 * Wraps a voxels associated with a channel so it can be easily casted to an appropriate data type.
 *
 * @author Owen Feehan
 */
public class VoxelsWrapper {

    private VoxelDataType dataType;
    private Voxels<?> voxels;

    /**
     * Creates to wrap a {@link Voxels} of unspecified type.
     * 
     * @param voxels the voxels to wrap, whose memory is reused without duplication.
     */
    public VoxelsWrapper(Voxels<?> voxels) {
        this.dataType = voxels.dataType();
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

    /** Casts voxels to a particular type */
    @SuppressWarnings("unchecked")
    public <T> Voxels<T> castTo() { // NOSONAR
        return (Voxels<T>) voxels;
    }

    public Voxels<?> match(VoxelDataType match) { // NOSONAR
        if (match.equals(dataType)) {
            return voxels;
        } else {
            throw new IncorrectVoxelTypeException(
                    String.format("User has requested %s from %s voxels", match, dataType));
        }
    }

    @SuppressWarnings("unchecked")
    public Voxels<UnsignedByteBuffer> asByte() {

        if (!dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Voxels do not contain unsigned 8-bit data (byte)");
        }

        return (Voxels<UnsignedByteBuffer>) voxels;
    }

    @SuppressWarnings("unchecked")
    public Voxels<FloatBuffer> asFloat() {

        if (!dataType.equals(FloatVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException("Voxels do not contain float data");
        }

        return (Voxels<FloatBuffer>) voxels;
    }

    @SuppressWarnings("unchecked")
    public Voxels<UnsignedShortBuffer> asShort() {

        if (!dataType.equals(UnsignedShortVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Voxels do not contain unsigned 16-bit data (int)");
        }

        return (Voxels<UnsignedShortBuffer>) voxels;
    }

    @SuppressWarnings("unchecked")
    public Voxels<UnsignedIntBuffer> asInt() {

        if (!dataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Voxels do not contain unsigned 32-bit data (int)");
        }

        return (Voxels<UnsignedIntBuffer>) voxels;
    }

    public VoxelDataType getVoxelDataType() {
        return dataType;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyVoxelsTo(
            BoundingBox from, VoxelsWrapper voxelsDestination, BoundingBox destinationBox) {

        // If the wrapper has the same type, we allow the operation
        if (voxelsDestination.getVoxelDataType().equals(getVoxelDataType())) {
            voxels.extract()
                    .boxCopyTo(from, (Voxels) voxelsDestination.match(dataType), destinationBox);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyVoxelsTo(
            ObjectMask from, VoxelsWrapper voxelsDestination, BoundingBox destinationBox) {

        // If the wrapper has the same type, we allow the operation
        if (voxelsDestination.getVoxelDataType().equals(getVoxelDataType())) {
            voxels.extract()
                    .objectCopyTo(from, (Voxels) voxelsDestination.match(dataType), destinationBox);
        }
    }

    public void subtractFromMaxValue() {
        voxels.arithmetic().subtractFrom((int) getVoxelDataType().maxValue());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void transferSlice(
            int sliceIndexToUpdate,
            VoxelsWrapper sourceVoxels,
            int sliceIndexSource,
            boolean duplicate) {
        if (getVoxelDataType().equals(sourceVoxels.getVoxelDataType())) {
            voxels.replaceSlice(
                    sliceIndexToUpdate,
                    sourceSlice((Voxels) sourceVoxels.any(), sliceIndexSource, duplicate));
        } else {
            throw new IncorrectVoxelTypeException("Voxel types are different");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> VoxelBuffer<T> slice(int z) {
        return (VoxelBuffer<T>) voxels.slice(z);
    }

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
    
    private static <T> VoxelBuffer<T> sourceSlice(
            Voxels<T> sourceVoxels, int sliceIndexSource, boolean duplicate) {
        if (duplicate) {
            return sourceVoxels.slice(sliceIndexSource);
        } else {
            return sourceVoxels.slice(sliceIndexSource).duplicate();
        }
    }
}
