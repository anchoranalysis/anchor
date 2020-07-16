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

package org.anchoranalysis.image.voxel.box;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

// Wraps a VoxelBox associated with an ImgChnl so it can be casted to
//  an appropriate data type
public class VoxelBoxWrapper {

    private VoxelDataType voxelDataType;
    private VoxelBox<? extends Buffer> voxelBox;

    public VoxelBoxWrapper(VoxelBox<? extends Buffer> voxelBox) {
        super();
        this.voxelDataType = voxelBox.dataType();
        this.voxelBox = voxelBox;
    }

    public static VoxelBoxWrapper wrap(VoxelBox<? extends Buffer> voxelBox) {
        return new VoxelBoxWrapper(voxelBox);
    }

    // Returns a VoxelBox that is not cast to any specific buffer type
    public VoxelBox<? extends Buffer> any() {
        return voxelBox;
    }

    public VoxelBox<? extends Buffer> match(VoxelDataType match) {
        if (match.equals(voxelDataType)) {
            return voxelBox;
        } else {
            throw new IncorrectVoxelDataTypeException(
                    String.format(
                            "User has requested %s from a %s VoxelBox", match, voxelDataType));
        }
    }

    @SuppressWarnings("unchecked")
    public VoxelBox<ByteBuffer> asByte() {

        if (!voxelDataType.equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "VoxelBox does not contain unsigned 8-bit data (byte)");
        }

        return (VoxelBox<ByteBuffer>) voxelBox;
    }

    @SuppressWarnings("unchecked")
    public VoxelBox<FloatBuffer> asFloat() {

        if (!voxelDataType.equals(VoxelDataTypeFloat.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException("VoxelBox does not contain float data");
        }

        return (VoxelBox<FloatBuffer>) voxelBox;
    }

    @SuppressWarnings("unchecked")
    public VoxelBox<ShortBuffer> asShort() {

        if (!voxelDataType.equals(VoxelDataTypeUnsignedShort.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "VoxelBox does not contain unsigned 16-bit data (int)");
        }

        return (VoxelBox<ShortBuffer>) voxelBox;
    }

    @SuppressWarnings("unchecked")
    public VoxelBox<IntBuffer> asInt() {

        if (!voxelDataType.equals(VoxelDataTypeUnsignedInt.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "VoxelBox does not contain unsigned 32-bit data (int)");
        }

        return (VoxelBox<IntBuffer>) voxelBox;
    }

    /**
     * If already byte -> no change If not already byte -> create new empty byte buffer
     *
     * @param inputBuffer
     * @param alwaysDuplicate
     * @return
     */
    public VoxelBox<ByteBuffer> asByteOrCreateEmpty(boolean alwaysDuplicate) {
        VoxelBox<ByteBuffer> boxOut;

        // If the input-channel is Byte then we do it in-place
        // Otherwise we create a new voxelbox
        if (!alwaysDuplicate && getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            boxOut = asByte();
        } else {
            boxOut = VoxelBoxFactory.getByte().create(any().extent());
        }

        return boxOut;
    }

    public VoxelDataType getVoxelDataType() {
        return voxelDataType;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyPixelsTo(
            BoundingBox sourceBox, VoxelBoxWrapper destVoxelBox, BoundingBox destBox) {

        // If the wrapper has the same type, we allow the operation
        if (destVoxelBox.getVoxelDataType().equals(getVoxelDataType())) {
            voxelBox.copyPixelsTo(sourceBox, (VoxelBox) destVoxelBox.match(voxelDataType), destBox);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void copyPixelsToCheckMask(
            BoundingBox sourceBox,
            VoxelBoxWrapper destVoxelBox,
            BoundingBox destBox,
            VoxelBox<ByteBuffer> objectMaskBuffer,
            BinaryValuesByte maskBV) {

        // If the wrapper has the same type, we allow the operation
        if (destVoxelBox.getVoxelDataType().equals(getVoxelDataType())) {
            voxelBox.copyPixelsToCheckMask(
                    sourceBox,
                    (VoxelBox) destVoxelBox.match(voxelDataType),
                    destBox,
                    objectMaskBuffer,
                    maskBV);
        }
    }

    public void subtractFromMaxValue() {
        voxelBox.subtractFrom((int) getVoxelDataType().maxValue());
    }

    public void transferPixelsForPlane(int z, VoxelBoxWrapper src, boolean duplicate) {
        transferPixelsForPlane(z, src, z, duplicate);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void transferPixelsForPlane(int z, VoxelBoxWrapper src, int zSrc, boolean duplicate) {
        if (getVoxelDataType().equals(src.getVoxelDataType())) {
            voxelBox.transferPixelsForPlane(z, (VoxelBox) src.any(), zSrc, duplicate);
        } else {
            throw new IncorrectVoxelDataTypeException("Voxel types are different");
        }
    }
}
