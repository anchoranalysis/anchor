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
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;
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
    public void copyPixelsTo(
            BoundingBox sourceBox, VoxelsWrapper voxelsDestination, BoundingBox destBox) {

        // If the wrapper has the same type, we allow the operation
        if (voxelsDestination.getVoxelDataType().equals(getVoxelDataType())) {
            voxels.copyPixelsTo(sourceBox, (Voxels) voxelsDestination.match(dataType), destBox);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyPixelsToCheckMask(
            BoundingBox sourceBox,
            VoxelsWrapper voxelsDestination,
            BoundingBox destBox,
            Voxels<ByteBuffer> objectMaskBuffer,
            BinaryValuesByte bvb) {

        // If the wrapper has the same type, we allow the operation
        if (voxelsDestination.getVoxelDataType().equals(getVoxelDataType())) {
            voxels.copyPixelsToCheckMask(
                    sourceBox,
                    (Voxels) voxelsDestination.match(dataType),
                    destBox,
                    objectMaskBuffer,
                    bvb);
        }
    }

    public void subtractFromMaxValue() {
        voxels.subtractFrom((int) getVoxelDataType().maxValue());
    }

    public void transferPixelsForPlane(int z, VoxelsWrapper src, boolean duplicate) {
        transferPixelsForPlane(z, src, z, duplicate);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void transferPixelsForPlane(int z, VoxelsWrapper src, int zSrc, boolean duplicate) {
        if (getVoxelDataType().equals(src.getVoxelDataType())) {
            voxels.transferPixelsForPlane(z, (Voxels) src.any(), zSrc, duplicate);
        } else {
            throw new IncorrectVoxelDataTypeException("Voxel types are different");
        }
    }
}
