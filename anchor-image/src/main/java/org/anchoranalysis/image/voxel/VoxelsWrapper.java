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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

/**
 * Wraps a voxels associated with a channel so it can be easily casted to an appropriate data type
 * 
 * @author Owen Feehan
 *
 */
public class VoxelsWrapper {

    private VoxelDataType dataType;
    private Voxels<? extends Buffer> voxels;

    public VoxelsWrapper(Voxels<? extends Buffer> voxels) {
        super();
        this.dataType = voxels.dataType();
        this.voxels = voxels;
    }

    public static VoxelsWrapper wrap(Voxels<? extends Buffer> voxels) {
        return new VoxelsWrapper(voxels);
    }

    // Returns voxels that are not cast to any specific buffer type
    public Voxels<? extends Buffer> any() { // NOSONAR
        return voxels;
    }

    public Voxels<? extends Buffer> match(VoxelDataType match) {       // NOSONAR
        if (match.equals(dataType)) {
            return voxels;
        } else {
            throw new IncorrectVoxelDataTypeException(
                    String.format(
                            "User has requested %s from %s voxels", match, dataType));
        }
    }

    @SuppressWarnings("unchecked")
    public Voxels<ByteBuffer> asByte() {

        if (!dataType.equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "Voxels do not contain unsigned 8-bit data (byte)");
        }

        return (Voxels<ByteBuffer>) voxels;
    }

    @SuppressWarnings("unchecked")
    public Voxels<FloatBuffer> asFloat() {

        if (!dataType.equals(VoxelDataTypeFloat.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException("Voxels do not contain float data");
        }

        return (Voxels<FloatBuffer>) voxels;
    }

    @SuppressWarnings("unchecked")
    public Voxels<ShortBuffer> asShort() {

        if (!dataType.equals(VoxelDataTypeUnsignedShort.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "Voxels do not contain unsigned 16-bit data (int)");
        }

        return (Voxels<ShortBuffer>) voxels;
    }

    @SuppressWarnings("unchecked")
    public Voxels<IntBuffer> asInt() {

        if (!dataType.equals(VoxelDataTypeUnsignedInt.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "Voxels do not contain unsigned 32-bit data (int)");
        }

        return (Voxels<IntBuffer>) voxels;
    }

    /**
     * If already byte -> no change If not already byte -> create new empty byte buffer
     *
     * @param inputBuffer
     * @param alwaysDuplicate
     * @return
     */
    public Voxels<ByteBuffer> asByteOrCreateEmpty(boolean alwaysDuplicate) {
        Voxels<ByteBuffer> boxOut;

        // If the input-channel is Byte then we do it in-place
        // Otherwise we create new voxels
        if (!alwaysDuplicate && getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            boxOut = asByte();
        } else {
            boxOut = VoxelsFactory.getByte().createInitialized(any().extent());
        }

        return boxOut;
    }

    public VoxelDataType getVoxelDataType() {
        return dataType;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyVoxelsTo(
            BoundingBox from, VoxelsWrapper voxelsDestination, BoundingBox destinationBox) {

        // If the wrapper has the same type, we allow the operation
        if (voxelsDestination.getVoxelDataType().equals(getVoxelDataType())) {
            voxels.extracter().boxCopyTo(from, (Voxels) voxelsDestination.match(dataType), destinationBox);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyVoxelsTo(
            ObjectMask from,
            VoxelsWrapper voxelsDestination,
            BoundingBox destinationBox
    ) {

        // If the wrapper has the same type, we allow the operation
        if (voxelsDestination.getVoxelDataType().equals(getVoxelDataType())) {
            voxels.extracter().objectCopyTo(
                    from,
                    (Voxels) voxelsDestination.match(dataType),
                    destinationBox);
        }
    }

    public void subtractFromMaxValue() {
        voxels.arithmetic().subtractFrom((int) getVoxelDataType().maxValue());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void transferSlice(int sliceIndexToUpdate, VoxelsWrapper sourceVoxels, int sliceIndexSource, boolean duplicate) {
        if (getVoxelDataType().equals(sourceVoxels.getVoxelDataType())) {
            voxels.replaceSlice(sliceIndexToUpdate, sourceSlice((Voxels) sourceVoxels.any(), sliceIndexSource, duplicate) );
        } else {
            throw new IncorrectVoxelDataTypeException("Voxel types are different");
        }
    }
        
    private static <T extends Buffer> VoxelBuffer<T> sourceSlice(Voxels<T> sourceVoxels, int sliceIndexSource, boolean duplicate) {
        if (duplicate) {
            return sourceVoxels.slice(sliceIndexSource);
        } else {
            return sourceVoxels.slice(sliceIndexSource).duplicate();
        }
    }

    public VoxelBuffer<? extends Buffer> slice(int z) { // NOSONAR
        return voxels.slice(z);
    }

    public Extent extent() {
        return voxels.extent();
    }

    public VoxelsArithmetic arithmetic() {
        return voxels.arithmetic();
    }

    public VoxelsAssigner assignValue(int valueToAssign) {
        return voxels.assignValue(valueToAssign);
    }

    public VoxelsExtracter<? extends Buffer> extracter() {  // NOSONAR
        return voxels.extracter();
    }
}
