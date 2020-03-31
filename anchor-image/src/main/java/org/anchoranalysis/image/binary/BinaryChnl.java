package org.anchoranalysis.image.binary;

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


import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

// An image that supporting certain binary operations
public class BinaryChnl {

	private Chnl chnl;

	private final BinaryValues binaryValues;
	
	public BinaryChnl(Chnl chnl, BinaryValues binaryValuesIn) {
		super();
		this.chnl = chnl;
		
		if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.instance)) {
			throw new IncorrectVoxelDataTypeException("Only unsigned 8-bit data type is supported for BinaryChnl");
		}
		
		this.binaryValues = binaryValuesIn.duplicate();
	}
	
	public BinaryChnl( BinaryVoxelBox<ByteBuffer> vb, ImageRes res, ChnlFactorySingleType factory ) {
		this.chnl = factory.create(vb.getVoxelBox(), res);
		this.binaryValues = vb.getBinaryValues();
	}

	public ImageDim getDimensions() {
		return chnl.getDimensions();
	}
	
	public VoxelBox<ByteBuffer> getVoxelBox() {
		try {
			return chnl.getVoxelBox().asByte();
		} catch (IncorrectVoxelDataTypeException e) {
			throw new IncorrectVoxelDataTypeException("Associated imgChnl does contain have unsigned 8-bit data (byte)");
		}
	}
	
	public BinaryVoxelBox<ByteBuffer> binaryVoxelBox() throws CreateException {
		return new BinaryVoxelBoxByte( getVoxelBox(), new BinaryValues(binaryValues) );
	}

	
	// Creates a new object each time, this is probably in efficient
	// TODO restructure
	// We use the assert to avoid throwing a new exception, but this must be wrong
	public boolean isPointOn( Point3i pnt ) {
		
		BinaryValuesByte bvb = binaryValues.createByte();
		
		ByteBuffer bb = getVoxelBox().getPixelsForPlane(pnt.getZ()).buffer();
		
		int offset = getVoxelBox().extnt().offset( pnt.getX(), pnt.getY() );
		return bb.get(offset)==bvb.getOnByte();
	}
	
	public BinaryValues getBinaryValues() {
		return binaryValues;
	}

	public BinaryChnl duplicate() {
		return new BinaryChnl( chnl.duplicate(), binaryValues.duplicate() );
	}

	public Chnl getChnl() {
		return chnl;
	}

	public void setChnl(Chnl chnl) {
		this.chnl = chnl;
	}
	
	// Creates a mask from the binaryChnl, avoiding creating a new buffer if possible
	public ObjMask createMaskAvoidNew( BoundingBox bbox ) throws CreateException {
		assert( chnl.getDimensions().contains(bbox) );
		return new ObjMask( bbox, chnl.getVoxelBox().asByte().createBufferAvoidNew(bbox ), binaryValues);
	}
	
	// Creates a mask from the binaryChnl, avoiding creating a new buffer if possible
	public ObjMask createMaskAlwaysNew( BoundingBox bbox ) throws CreateException {
		assert( chnl.getDimensions().contains(bbox) );
		return new ObjMask( bbox, chnl.getVoxelBox().asByte().createBufferAlwaysNew(bbox ), binaryValues);
	}

	public BinaryChnl maxIntensityProj() {
		return new BinaryChnl( chnl.maxIntensityProj(), binaryValues.duplicate() );
	}
	
	public boolean hasHighValues() {
		return chnl.hasEqualTo( binaryValues.getOnInt() );
	}
	
	public int countHighValues() {
		return chnl.countEqualTo( binaryValues.getOnInt() );
	}
	
	public BinaryChnl scaleXY(double ratioX, double ratioY, Interpolator interpolator) throws OperationFailedException {

		if (ratioX==1.0 && ratioY==1.0) {
			// Nothing to do
			return this;
		}
		
		Chnl scaled = this.chnl.scaleXY(ratioX, ratioY, interpolator);
		
		BinaryChnl binaryChnl = new BinaryChnl(
			scaled,
			binaryValues.duplicate()
		);
		
		// We threshold to make sure it's still binary
		{
			int thresholdVal = (binaryValues.getOnInt() + binaryValues.getOffInt()) /2;
		
			try {
				VoxelBoxThresholder.thresholdForLevel(
					binaryChnl.getVoxelBox(),
					thresholdVal,
					binaryChnl.getBinaryValues().createByte()
				);
			} catch (CreateException e) {
				throw new OperationFailedException(e);
			}
			
		}
		
		return binaryChnl;
	}

	public BinaryChnl extractSlice(int z) {
		return new BinaryChnl( chnl.extractSlice(z), binaryValues );
	}

	public void replaceBy(BinaryVoxelBox<ByteBuffer>  bvb)
			throws IncorrectImageSizeException {
		chnl.getVoxelBox().asByte().replaceBy(bvb.getVoxelBox());
	}

}
