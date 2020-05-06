package org.anchoranalysis.image.objmask.ops;



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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.BinaryChnlInverter;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;


/** 
 * Merges one or more ObjMasks into a single mask
 **/
public class ObjMaskMerger {
	
	private ObjMaskMerger() {
	}
	
	public static ObjMask merge( ObjMask om1, ObjMask om2 ) {

		// If we don't have identical binary values, we invert the second one
		if (!om2.getBinaryValues().equals(om1.getBinaryValues())) {
			// We assume it's always 255/0 or 0/255
			assert (om2.getBinaryValues().createInverted().equals(om1.getBinaryValues()));
			om2 = BinaryChnlInverter.invertObjMaskDuplicate(om2);
		}
		
		BoundingBox bbox = BoundingBox.union( om1.getBoundingBox(), om2.getBoundingBox() );
		
		ObjMask omOut = new ObjMask(
			bbox,
			VoxelBoxFactory.instance().getByte().create(
				bbox.extnt()
			)
		);
		
		copyPixelsCheckMask(om1, omOut, bbox);
		copyPixelsCheckMask(om2, omOut, bbox);
		
		return omOut;
		
	}
		
	public static BoundingBox mergeBBoxFromObjs( ObjMaskCollection objs ) throws OperationFailedException {
		
		if (objs.isEmpty()) {
			throw new OperationFailedException("At least one object must exist in the collection");
		}
		
		BoundingBox bbox = null;
		
		for( ObjMask om : objs ) {
			if (bbox==null) {
				bbox = new BoundingBox( om.getBoundingBox() );
			} else {
				bbox.union(om.getBoundingBox());
			}
		}
		
		return bbox;
	}
	
	public static ObjMask merge( ObjMaskCollection objs ) throws OperationFailedException {
		
		if (objs.size()==0) {
			throw new OperationFailedException("There must be at least one object");
		}
		
		if (objs.size()==1) {
			return objs.get(0).duplicate();	// So we are always guaranteed to have a new object
		}
		
		BoundingBox bbox = mergeBBoxFromObjs(objs);
		
		ObjMask omOut = new ObjMask( bbox, VoxelBoxFactory.instance().getByte().create(bbox.extnt()) );
		
		BinaryValues bv = null;
		for( ObjMask om : objs ) {
			
			if (bv!=null) {
				if (!om.getBinaryValues().equals(bv)) {
					throw new OperationFailedException("Cannot merge. Incompatible binary values among object-collection");
				}
			} else {
				bv = om.getBinaryValues();
			}
			
			copyPixelsCheckMask( om, omOut, bbox );
		}
		
		return omOut;
	}
	

	private static void copyPixelsCheckMask( ObjMask omSrc, ObjMask omDest, BoundingBox bbox ) {
		
		Point3i pntDest = omSrc.getBoundingBox().relPosTo( bbox );
		Extent e = omSrc.getBoundingBox().extnt();
		
		omSrc.getVoxelBox().copyPixelsToCheckMask(
			new BoundingBox(e),
			omDest.getVoxelBox(),
			new BoundingBox(pntDest, e),
			omSrc.getVoxelBox(),
			omSrc.getBinaryValuesByte()
		);
	}
}
