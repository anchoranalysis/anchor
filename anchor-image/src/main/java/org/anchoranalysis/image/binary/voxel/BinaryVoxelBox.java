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

package org.anchoranalysis.image.binary.voxel;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A voxel-box that should only contain two voxel-values (representing ON and OFF states)
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@AllArgsConstructor
public abstract class BinaryVoxelBox<T extends Buffer> implements BinaryOnOffSetter {

    /** Voxels that should only have two intensity-values (representing ON and OFF states). This is not checked as a precondition. */
    @Getter @Setter private VoxelBox<T> voxels;
    
    /** Which two intensity values represent OFF and ON states */
    @Getter private BinaryValues binaryValues;

    /**
     * Constructor - creates a voxel box using default binary-values
     * 
     * @param voxels voxels that should only have two intensity-values (representing ON and OFF states). This is not checked as a precondition.
     */
    public BinaryVoxelBox(VoxelBox<T> voxels) {
        this.voxels = voxels;
        this.binaryValues = BinaryValues.getDefault();
    }
    
    /**
     * Changes the OFF state to be the ON state and vice-versa.
     * <p>
     * Only the {@code binaryValues} (acting as an index to the intensity values) is changed; the voxels remain themselves unchanged.
     */
    public void invert() {
        binaryValues = binaryValues.createInverted();
    }
    
    public Extent extent() {
        return voxels.extent();
    }

    public VoxelBuffer<T> getPixelsForPlane(int z) {
        return voxels.getPixelsForPlane(z);
    }

    public boolean hasOnVoxel() {
        return voxels.hasEqualTo(binaryValues.getOnInt());
    }

    public boolean hasOffVoxel() {
        return voxels.hasEqualTo(binaryValues.getOffInt());
    }

    public void copyPixelsTo(BoundingBox sourceBox, VoxelBox<T> destVoxelBox, BoundingBox destBox) {
        voxels.copyPixelsTo(sourceBox, destVoxelBox, destBox);
    }

    public void copyPixelsToCheckMask(
            BoundingBox sourceBox,
            VoxelBox<T> destVoxelBox,
            BoundingBox destBox,
            VoxelBox<ByteBuffer> objectMaskBuffer,
            BinaryValuesByte bvb) {
        voxels.copyPixelsToCheckMask(sourceBox, destVoxelBox, destBox, objectMaskBuffer, bvb);
    }

    public void setPixelsCheckMaskOn(ObjectMask object) {
        voxels.setPixelsCheckMask(object, binaryValues.getOnInt());
    }

    public void setPixelsCheckMaskOff(ObjectMask object) {
        voxels.setPixelsCheckMask(object, binaryValues.getOffInt());
    }

    public abstract BinaryVoxelBox<T> duplicate();

    public abstract BinaryVoxelBox<T> extractSlice(int z) throws CreateException;

    public void setPixelsCheckMask(ObjectMask objectMask, int value, byte objectMaskMatchValue) {
        voxels.setPixelsCheckMask(objectMask, value, objectMaskMatchValue);
    }

    public void setPixelsCheckMask(
            BoundingBox bboxToBeAssigned,
            VoxelBox<ByteBuffer> objectBuffer,
            BoundingBox bboxMask,
            int value,
            byte objectMaskMatchValue) {
        voxels.setPixelsCheckMask(
                bboxToBeAssigned, objectBuffer, bboxMask, value, objectMaskMatchValue);
    }

    public void addPixelsCheckMask(ObjectMask objectMask, int value) {
        voxels.addPixelsCheckMask(objectMask, value);
    }

    public void setPixelsForPlane(int z, VoxelBuffer<T> pixels) {
        voxels.setPixelsForPlane(z, pixels);
    }

    public void setAllPixelsToOn() {
        voxels.setAllPixelsTo(binaryValues.getOnInt());
    }

    public void setPixelsToOn(BoundingBox bbox) {
        voxels.setPixelsTo(bbox, binaryValues.getOnInt());
    }

    public void setAllPixelsToOff() {
        voxels.setAllPixelsTo(binaryValues.getOffInt());
    }

    public void setPixelsToOff(BoundingBox bbox) {
        voxels.setPixelsTo(bbox, binaryValues.getOffInt());
    }

    public int countOn() {
        return voxels.countEqual(binaryValues.getOnInt());
    }

    public int countOff() {
        return voxels.countEqual(binaryValues.getOffInt());
    }
}
