package org.anchoranalysis.image.objmask.ops;

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
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeByte;

public class BinaryChnlFromObjs {

	/** We look for space IN objects, and create channel to display it */
	public static BinaryChnl createFromObjs( ObjMaskCollection masks, ImageDim sd, BinaryValues outVal ) throws CreateException {
		return createChnlObjMaskCollectionHelper(
			masks,
			sd,
			outVal,
			outVal.getOffInt(),
			outVal.createByte().getOnByte()
		);
	}
	

	/** We look for space NOT in the objects, and create channel to display it */
	public static BinaryChnl createFromNotObjs( ObjMaskCollection objs, ImageDim sd, BinaryValues outVal ) throws CreateException {
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
		ObjMaskCollection masks,
		ImageDim sd,
		BinaryValues outVal,
		int initialState,
		byte objState
	) throws CreateException {
		
		ImageDim newSd = new ImageDim( sd );
		
		Chnl chnlNew = ChnlFactory.instance().createEmptyInitialised(newSd, VoxelDataTypeByte.instance);
		VoxelBox<ByteBuffer> vbNew = chnlNew.getVoxelBox().asByte();
		
		if (outVal.getOnInt()!=0) {
			vbNew.setAllPixelsTo( initialState );
		}
		
		writeChnlObjMaskCollection( vbNew, masks, objState );
		
		return new BinaryChnl(chnlNew, outVal);
	}
		
	// nullVal is assumed to be 0
	private static void writeChnlObjMaskCollection( VoxelBox<ByteBuffer> vb, ObjMaskCollection masks, byte outVal ) {
		
		for (ObjMask objMask : masks) {
			writeObjMaskToVoxelBox(objMask, vb, outVal);
		}
	}
	
	private static void writeObjMaskToVoxelBox( ObjMask mask, VoxelBox<ByteBuffer> voxelBoxOut, byte outValByte ) {
		
		BoundingBox bbox = mask.getBoundingBox();
		
		Point3i maxGlobal = bbox.calcCrnrMax();
		Point3i pntGlobal = new Point3i();
		Point3i pntLocal = new Point3i();
		
		byte maskOn = mask.getBinaryValuesByte().getOnByte();
		
		pntLocal.setZ(0);
		for (pntGlobal.setZ(bbox.getCrnrMin().getZ()); pntGlobal.getZ() <=maxGlobal.getZ(); pntGlobal.incrZ(), pntLocal.incrZ()) {
			
			ByteBuffer maskIn = mask.getVoxelBox().getPixelsForPlane(pntLocal.getZ()).buffer();
			ByteBuffer pixelsOut = voxelBoxOut.getPlaneAccess().getPixelsForPlane(pntGlobal.getZ()).buffer();
			
			for (pntGlobal.setY(bbox.getCrnrMin().getY()); pntGlobal.getY() <= maxGlobal.getY(); pntGlobal.incrY() ) {
			
				for (pntGlobal.setX(bbox.getCrnrMin().getX()); pntGlobal.getX() <= maxGlobal.getX(); pntGlobal.incrX() ) {	

					if (maskIn.get()!=maskOn) {
						continue;
					}
					
					int indexGlobal = voxelBoxOut.extnt().offset(pntGlobal.getX(), pntGlobal.getY());
					pixelsOut.put(indexGlobal,outValByte);
				}
			}
		}
	}
	
}
