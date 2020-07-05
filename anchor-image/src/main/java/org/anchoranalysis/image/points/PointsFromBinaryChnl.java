package org.anchoranalysis.image.points;

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

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.extent.BoundingBox;

public class PointsFromBinaryChnl {
	
	private PointsFromBinaryChnl() {}
	
	public static List<Point3i> pointsFromChnl( BinaryChnl chnl ) {
		
		List<Point3i> listOut = new ArrayList<>();
		
		PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D(chnl.binaryVoxelBox(),new Point3i(0,0,0),listOut);
		
		return listOut;
	}
	
	public static List<Point2i> pointsFromChnl2D( BinaryChnl chnl ) throws CreateException {
		return PointsFromBinaryVoxelBox.pointsFromVoxelBox2D( chnl.binaryVoxelBox() );
	}
	
	public static List<Point3i> pointsFromChnlInsideBox( BinaryChnl chnl, BoundingBox bbox, int startZ, int skipAfterSuccessiveEmptySlices ) {
		
		List<Point3i> listOut = new ArrayList<>();
		
		PointsFromChnlHelper helper = new PointsFromChnlHelper(
				skipAfterSuccessiveEmptySlices,
				bbox.cornerMin(),
				bbox.calcCornerMax(),
				chnl.getChnl().getVoxelBox().asByte(),
				chnl.getBinaryValues().createByte(),
				startZ,
				listOut
			);
		
		helper.firstHalf();
		
		// Exit early if we start on the first slice
		if (startZ==0) {
			return listOut;
		}
				
		helper.secondHalf();
				
		return listOut;
	}

}
