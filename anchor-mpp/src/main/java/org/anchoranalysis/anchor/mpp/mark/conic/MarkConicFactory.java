package org.anchoranalysis.anchor.mpp.mark.conic;

/*
 * #%L
 * anchor-mpp
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


import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.image.orientation.Orientation3DEulerAngles;

import com.google.common.base.Preconditions;

public class MarkConicFactory {
	
	private MarkConicFactory() {}
	
	public static Mark createMarkFromPoint(Point3i point, int size, boolean do3D) {
		return createMarkFromPoint(
			PointConverter.doubleFromInt(point),
			size,
			do3D
		);
	}
	
	public static Mark createMarkFromPoint(Point3d point, int size, boolean do3D) {
		Preconditions.checkArgument(size>0);
		Preconditions.checkArgument(do3D || point.getZ()==0);
		
		if (do3D) {
			MarkEllipsoid me = new MarkEllipsoid();
			me.setMarksExplicit(point, new Orientation3DEulerAngles(), new Point3d(size,size,size) );
			return me;
		} else {
			MarkEllipse me = new MarkEllipse();
			me.setMarksExplicit(point, new Orientation2D(), new Point2d(size,size) );
			return me;
		}
	}

}
