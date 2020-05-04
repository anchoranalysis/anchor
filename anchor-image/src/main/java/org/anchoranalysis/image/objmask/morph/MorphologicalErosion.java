package org.anchoranalysis.image.objmask.morph;

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
import java.util.Optional;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.morph.MorphologicalDilation.IAcceptIteration;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class MorphologicalErosion {

	public static ObjMask createErodedObjMask(
		ObjMask om,
		Extent extent,
		boolean do3D,
		int iterations,
		boolean outsideAtThreshold,
		IAcceptIteration acceptConditionsDilation // NB applied on an inverted-version of the binary buffer!!!
	) throws CreateException {
		
		ObjMask omOut;
		
		// TODO
		// We can make this more efficient, then remaking a mask needlessly
		//  by having a smarter "isOutside" check in the Erosion routine
		if (outsideAtThreshold==false) {
			// If we want to treat the outside of the image as if it's at a threshold, then
			//  we put an extra 1-pixel border around the object-mask, so that there's always
			//  whitespace around the object-mask, so long as it exists in the image scene
			BoundingBox bbox = om.getVoxelBoxBounded().dilate(
				do3D,
				Optional.of(extent)
			);
			omOut = om.createIntersectingMaskAlwaysNew(bbox);
			
		} else {
			omOut = om.duplicate();
		}
		
		BinaryVoxelBox<ByteBuffer> eroded = erode(
			omOut.binaryVoxelBox(),
			do3D,
			iterations,
			null,
			0,
			outsideAtThreshold,
			acceptConditionsDilation
		);
		omOut.setVoxelBox(eroded.getVoxelBox());
		return omOut;
	}
	
	/**
	 * Performs a morphological erosion by dilating an inverted version of the object
	 * 
	 * @param bvb
	 * @param do3D
	 * @param iterations
	 * @param backgroundVb
	 * @param minIntensityValue
	 * @param outsideAtThreshold
	 * @param acceptConditionsDilation conditions applied on each iteration of the erosion N.B. but applied on an inverted-version when passes to Dilate
	 * @return
	 * @throws CreateException
	 */
	public static BinaryVoxelBox<ByteBuffer> erode(
			BinaryVoxelBox<ByteBuffer> bvb,
			boolean do3D,
			int iterations,
			VoxelBox<ByteBuffer> backgroundVb,
			int minIntensityValue,
			boolean outsideAtThreshold,
			IAcceptIteration acceptConditionsDilation // NB applied on an inverted-version of the binary buffer!!!
		) throws CreateException {

			bvb.invert();
			BinaryVoxelBox<ByteBuffer> dilated = MorphologicalDilation.dilate(
				bvb,
				do3D,
				iterations,
				backgroundVb,
				minIntensityValue,
				false,
				outsideAtThreshold,
				acceptConditionsDilation,
				false
			);
			dilated.invert();
			return dilated;
		}
}
