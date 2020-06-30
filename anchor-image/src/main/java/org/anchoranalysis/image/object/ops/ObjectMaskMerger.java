package org.anchoranalysis.image.object.ops;



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
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;


/** 
 * Merges one or more {@link ObjectMask}s into a single mask
 **/
public class ObjectMaskMerger {
	
	private ObjectMaskMerger() {}
	
	/**
	 * Merges two objects together
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * <p>The merged box has a minimal bounding-box to fit both objects.</p>
	 * 
	 * <p>Even if the two existing object-masks do not intersect or touch,
	 * a single merged mask is nevertheless created.</p>
	 * 
	 * <p>It assumes that the binary-values of the merges are always 255 and 0, or 0 and 255</p>.
	 * 
	 * @param first first-object to merge
	 * @param second second-object to merge
	 * @return first and second merged together
	 */
	public static ObjectMask merge( ObjectMask first, ObjectMask second ) {

		// If we don't have identical binary values, we invert the second one
		if (!second.getBinaryValues().equals(first.getBinaryValues())) {
			// We assume it's always 255/0 or 0/255
			assert (second.getBinaryValues().createInverted().equals(first.getBinaryValues()));
			second = BinaryChnlInverter.invertObjMaskDuplicate(second);
		}
		
		BoundingBox bbox = first.getBoundingBox().union().with(second.getBoundingBox() );
		
		ObjectMask out = new ObjectMask(
			bbox,
			VoxelBoxFactory.getByte().create(
				bbox.extent()
			)
		);
		copyPixelsCheckMask(first, out, bbox);
		copyPixelsCheckMask(second, out, bbox);
		return out;
	}
		
	
	/**
	 * Merges all the bounding boxes of a collection of objects.
	 * 
	 * @param objs objects whose bounding-boxes are merged
	 * @return a bounding-box just large enough to include all the bounding-boxes of the objects
	 * @throws OperationFailedException if the {@code objs} parameter is empty
	 */
	public static BoundingBox mergeBoundingBoxes( ObjectCollection objs ) throws OperationFailedException {
		
		if (objs.isEmpty()) {
			throw new OperationFailedException("At least one object must exist in the collection");
		}
		
		BoundingBox bbox = null;
		
		for( ObjectMask om : objs ) {
			if (bbox==null) {
				bbox = om.getBoundingBox();
			} else {
				bbox = bbox.union().with(om.getBoundingBox());
			}
		}
		
		return bbox;
	}
	
	/**
	 * Merges all the objects together that are found in a collection
	 * 
	 * @param objs objects to be merged
	 * @return a newly created merged version of all the objects, with a bounding-box just big enough to include all the existing mask bounding-boxes
	 * @throws OperationFailedException if any two objects with different binary-values are merged.
	 */
	public static ObjectMask merge( ObjectCollection objs ) throws OperationFailedException {
		
		if (objs.size()==0) {
			throw new OperationFailedException("There must be at least one object");
		}
		
		if (objs.size()==1) {
			return objs.get(0).duplicate();	// So we are always guaranteed to have a new object
		}
		
		BoundingBox bbox = mergeBoundingBoxes(objs);
		
		ObjectMask omOut = new ObjectMask( bbox, VoxelBoxFactory.getByte().create(bbox.extent()) );
		
		BinaryValues bv = null;
		for( ObjectMask om : objs ) {
			
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
	

	private static void copyPixelsCheckMask( ObjectMask omSrc, ObjectMask omDest, BoundingBox bbox ) {
		
		Point3i pntDest = omSrc.getBoundingBox().relPosTo( bbox );
		Extent e = omSrc.getBoundingBox().extent();
		
		omSrc.getVoxelBox().copyPixelsToCheckMask(
			new BoundingBox(e),
			omDest.getVoxelBox(),
			new BoundingBox(pntDest, e),
			omSrc.getVoxelBox(),
			omSrc.getBinaryValuesByte()
		);
	}
}
