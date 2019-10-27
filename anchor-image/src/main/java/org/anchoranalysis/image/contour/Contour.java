package org.anchoranalysis.image.contour;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;

/**
 * A path of successively-neighbouring points along the edge of an object
 * 
 * @author FEEHANO
 *
 */
public class Contour {

	private List<Point3f> points = new ArrayList<>();
	
	private static double maxDistToDefinedConnected = 2;
	
	public List<Point3f> getPoints() {
		return points;
	}
	
	public List<Point3i> pointsDiscrete() {
		return PointConverter.convert3i( getPoints(), false);
	}
	
	public boolean isClosed() {
		return points.get(0).distance( points.get(points.size()-1) ) < maxDistToDefinedConnected;
	}
	
	public boolean hasPoint( Point3f pntC ) {
		for (Point3f pnt : getPoints()) {
			if (pnt.equals(pntC)) {
				return true;
			}
		}
		return false;
	}
	
	public Point3f getFirstPoint() {
		return points.get(0);
	}
	
	public Point3f getMiddlePoint() {
		return points.get( points.size()/2 );
	}
	
	public Point3f getLastPoint() {
		return points.get( points.size() -1 );
	}
	
	public boolean connectedTo( Contour c ) {
		
		if ( connectedToFirstPointOf(c) ) {
			return true;
		}
		
		if ( connectedToLastPointOf(c) ) {
			return true;
		}
		
		return false;
	}
	
	
	public boolean connectedToFirstPointOf( Contour c ) {
		
		if (getLastPoint().distance(c.getFirstPoint()) < maxDistToDefinedConnected) {
			return true;
		}
		
		return false;
	}

	public boolean connectedToLastPointOf( Contour c ) {
		
		if (getFirstPoint().distance(c.getLastPoint()) < maxDistToDefinedConnected) {
			return true;
		}
		
		return false;
	}

	public String summaryStr() {
		return String.format("[%s-%s]", points.get(0), points.get(points.size()-1) );
	}
}
