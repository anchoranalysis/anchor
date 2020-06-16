package org.anchoranalysis.image.objectmask.ops;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

public class BinaryChnlFromObjs {

	/** We look for space IN objects, and create channel to display it */
	public static BinaryChnl createFromObjs( ObjectMaskCollection masks, ImageDim sd, BinaryValues outVal ) throws CreateException {
		return createChnlObjMaskCollectionHelper(
			masks,
			sd,
			outVal,
			outVal.getOffInt(),
			outVal.createByte().getOnByte()
		);
	}
	

	/** We look for space NOT in the objects, and create channel to display it */
	public static BinaryChnl createFromNotObjs( ObjectMaskCollection objs, ImageDim sd, BinaryValues outVal ) throws CreateException {
		return createChnlObjMaskCollectionHelper(
			objs,
			sd,
			outVal,
			outVal.getOnInt(),
			outVal.createByte().getOffByte()
		);
	}
	
	// We look for the values that are NOT on the masks
	private static BinaryChnl createChnlObjMaskCollectionHelper(
		ObjectMaskCollection masks,
		ImageDim dim,
		BinaryValues outVal,
		int initialState,
		byte objState
	) throws CreateException {
		
		Channel chnlNew = ChannelFactory.instance().createEmptyInitialised(dim, VoxelDataTypeUnsignedByte.instance);
		VoxelBox<ByteBuffer> vbNew = chnlNew.getVoxelBox().asByte();
		
		if (outVal.getOnInt()!=0) {
			vbNew.setAllPixelsTo( initialState );
		}
		
		writeChnlObjMaskCollection( vbNew, masks, objState );
		
		return new BinaryChnl(chnlNew, outVal);
	}
		
	// nullVal is assumed to be 0
	private static void writeChnlObjMaskCollection( VoxelBox<ByteBuffer> vb, ObjectMaskCollection masks, byte outVal ) {
		
		for (ObjectMask objMask : masks) {
			writeObjMaskToVoxelBox(objMask, vb, outVal);
		}
	}
	
	private static void writeObjMaskToVoxelBox( ObjectMask mask, VoxelBox<ByteBuffer> voxelBoxOut, byte outValByte ) {
		
		BoundingBox bbox = mask.getBoundingBox();
		
		ReadableTuple3i maxGlobal = bbox.calcCrnrMax();
		Point3i pntGlobal = new Point3i();
		Point3i pntLocal = new Point3i();
		
		byte maskOn = mask.getBinaryValuesByte().getOnByte();
		
		pntLocal.setZ(0);
		for (pntGlobal.setZ(bbox.getCrnrMin().getZ()); pntGlobal.getZ() <=maxGlobal.getZ(); pntGlobal.incrementZ(), pntLocal.incrementZ()) {
			
			ByteBuffer maskIn = mask.getVoxelBox().getPixelsForPlane(pntLocal.getZ()).buffer();
			
			ByteBuffer pixelsOut = voxelBoxOut.getPlaneAccess().getPixelsForPlane(pntGlobal.getZ()).buffer();
			writeToBufferMasked(
				maskIn,
				pixelsOut,
				voxelBoxOut.extent(),
				bbox.getCrnrMin(),
				pntGlobal,
				maxGlobal,
				maskOn,
				outValByte
			);
		}
	}
	
	private static void writeToBufferMasked(
		ByteBuffer maskIn,
		ByteBuffer pixelsOut,
		Extent extentOut,
		ReadableTuple3i crnrMin,
		Point3i pntGlobal,
		ReadableTuple3i maxGlobal,
		byte maskOn,
		byte outValByte
	) {
		
		for (pntGlobal.setY(crnrMin.getY()); pntGlobal.getY() <= maxGlobal.getY(); pntGlobal.incrementY() ) {
			
			for (pntGlobal.setX(crnrMin.getX()); pntGlobal.getX() <= maxGlobal.getX(); pntGlobal.incrementX() ) {	

				if (maskIn.get()!=maskOn) {
					continue;
				}
				
				int indexGlobal = extentOut.offset(pntGlobal.getX(), pntGlobal.getY());
				pixelsOut.put(indexGlobal,outValByte);
			}
		}
		
	}
	
}
