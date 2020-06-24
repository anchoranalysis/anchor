package org.anchoranalysis.image.binary.voxel;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type
 */
public abstract class BinaryVoxelBox<T extends Buffer> implements BinaryHighLowSetter {

	private VoxelBox<T> voxelBox;
	
	private BinaryValues binaryValues;
	
	public BinaryVoxelBox(VoxelBox<T> voxelBox, BinaryValues bv) {
		super();
		this.voxelBox = voxelBox;
		this.binaryValues = bv;
	}

	public Extent extent() {
		return voxelBox.extent();
	}

	public VoxelBox<T> getVoxelBox() {
		return voxelBox;
	}
	
	public void invert() {
		binaryValues = binaryValues.createInverted();
	}

	public BinaryValues getBinaryValues() {
		return binaryValues;
	}

	public VoxelBuffer<T> getPixelsForPlane(int z) {
		return voxelBox.getPixelsForPlane(z);
	}
	
	public boolean hasOnVoxel() {
		return voxelBox.hasEqualTo( binaryValues.getOnInt() );
	}
	
	public boolean hasOffVoxel() {
		return voxelBox.hasEqualTo( binaryValues.getOffInt() );
	}

	public void copyPixelsTo(BoundingBox sourceBox,
			VoxelBox<T> destVoxelBox, BoundingBox destBox) {
		voxelBox.copyPixelsTo(sourceBox, destVoxelBox, destBox);
	}

	public void copyPixelsToCheckMask(BoundingBox sourceBox,
			VoxelBox<T> destVoxelBox, BoundingBox destBox,
			VoxelBox<ByteBuffer> objMaskBuffer, BinaryValuesByte maskBV) {
		voxelBox.copyPixelsToCheckMask(sourceBox, destVoxelBox, destBox,
				objMaskBuffer, maskBV);
	}

	public void setPixelsCheckMaskOn(ObjectMask om) {
		voxelBox.setPixelsCheckMask(om, binaryValues.getOnInt() );
	}
	
	public void setPixelsCheckMaskOff(ObjectMask om) {
		voxelBox.setPixelsCheckMask(om, binaryValues.getOffInt() );
	}
	
	public abstract BinaryVoxelBox<T> duplicate();
	
	public abstract BinaryVoxelBox<T> extractSlice(int z) throws CreateException;

	public void setPixelsCheckMask(ObjectMask om, int value, byte maskMatchValue) {
		voxelBox.setPixelsCheckMask(om, value, maskMatchValue);
	}

	public void setPixelsCheckMask(BoundingBox bboxToBeAssigned,
			VoxelBox<ByteBuffer> objMaskBuffer, BoundingBox bboxMask, int value,
			byte maskMatchValue) {
		voxelBox.setPixelsCheckMask(bboxToBeAssigned, objMaskBuffer,
				bboxMask, value, maskMatchValue);
	}

	public void addPixelsCheckMask(ObjectMask mask, int value) {
		voxelBox.addPixelsCheckMask(mask, value);
	}

	public void setPixelsForPlane(int z, VoxelBuffer<T> pixels) {
		voxelBox.setPixelsForPlane(z, pixels);
	}

	public void setAllPixelsToOn() {
		voxelBox.setAllPixelsTo(binaryValues.getOnInt());
	}
	
	public void setPixelsToOn( BoundingBox bbox ) {
		voxelBox.setPixelsTo(bbox, binaryValues.getOnInt());
	}
	
	public void setAllPixelsToOff() {
		voxelBox.setAllPixelsTo(binaryValues.getOffInt());
	}
	
	public void setPixelsToOff( BoundingBox bbox ) {
		voxelBox.setPixelsTo(bbox, binaryValues.getOffInt());
	}

	public void setVoxelBox(VoxelBox<T> voxelBox) {
		this.voxelBox = voxelBox;
	}

	public int countOn() {
		return voxelBox.countEqual(binaryValues.getOnInt());
	}
	
	public int countOff() {
		return voxelBox.countEqual(binaryValues.getOffInt());
	}

}