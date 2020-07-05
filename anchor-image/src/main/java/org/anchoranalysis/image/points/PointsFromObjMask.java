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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.outline.FindOutline;

public class PointsFromObjMask {
	
	private PointsFromObjMask() {}
	
	/**
	 * A list of points from the entire object-mask
	 * 
	 * @param om
	 * @return
	 * @throws CreateException
	 */
	public static List<Point3i> pntsFromMask( ObjectMask om ) {
		List<Point3i> pts = new ArrayList<>();
		PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D( om.binaryVoxelBox(), om.getBoundingBox().cornerMin(), pts );
		return pts;
	}
	
	/**
	 * A list of points from the entire object-mask
	 * 
	 * @param om
	 * @return
	 */
	public static List<Point3d> pntsFromMaskDouble( ObjectMask om ) {
		List<Point3d> pts = new ArrayList<>();
		PointsFromBinaryVoxelBox.addPointsFromVoxelBox3DDouble(
			om.binaryVoxelBox(),
			om.getBoundingBox().cornerMin(),
			pts
		);
		return pts;
	}
	
	/**
	 * A list of points from the outline of an object-mask
	 * 
	 * @param om
	 * @return
	 * @throws CreateException
	 */
	public static List<Point3i> pntsFromMaskOutline( ObjectMask om ) throws CreateException {
		List<Point3i> pts = new ArrayList<>();
		ObjectMask outline = FindOutline.outline(om, 1, false, true);
		PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D( outline.binaryVoxelBox(), outline.getBoundingBox().cornerMin(), pts );
		return pts;
	}
		
	public static Set<Point3i> pntsFromMaskOutlineSet( ObjectMask om ) throws CreateException {

		Set<Point3i> pts = new HashSet<>();
		
		ObjectMask outline = FindOutline.outline(om, 1, false, false);
		PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D( outline.binaryVoxelBox(), outline.getBoundingBox().cornerMin(), pts );
		// Now get all the points on the outline
		
		return pts;
	}
}
