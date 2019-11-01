package ch.ethz.biol.cell.imageprocessing.objmask.provider.assignobjstocfg;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkEllipsoid;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objmask.ObjMask;

public class RslvdEllipsoid {
	private MarkEllipsoid mark;
	private ObjMask objMask;
	private boolean include;
	private RslvdObjMaskList assignedObjs = new RslvdObjMaskList();
	private double maxRadius;
	private ImageDim dim;
	
	public RslvdEllipsoid(MarkEllipsoid mark, ImageDim dim, RegionMembershipWithFlags rm, BinaryValuesByte bvb ) {
		super();
		this.mark = mark;
		this.objMask = mark.calcMask(dim, rm, bvb ).getMask();
		this.include = true;
		this.dim = dim;
		
		ImageRes res = dim.getRes();
		double maxRes = threeWayMax( res.getX(), res.getY(), res.getZ() );
		
		Point3d radii = mark.getRadii();
		maxRadius = threeWayMax( radii.getX(), radii.getY(), radii.getZ() ) * maxRes;
	}
	
	private static double threeWayMax( double a, double b, double c ) {
		return Math.max( Math.max(a, b), c );
	}
	
	@SuppressWarnings("unused")
	private static double threeWayMin( double a, double b, double c ) {
		return Math.min( Math.min(a, b), c );
	}
	
	public boolean contains( RslvdObjMask rom ) {
		return objMask.contains( rom.getCenterInt() );
	}
	
	public void setAsExcluded() {
		this.include = false;
	}
	
	public void assignObj( RslvdObjMask rom ) {
		assignedObjs.add(rom);
	}
	
	public boolean isIncluded() {
		return include;
	}

	public RslvdObjMaskList getAssignedObjs() {
		return assignedObjs;
	}

	public MarkEllipsoid getMark() {
		return mark;
	}

	public ObjMask getObjMask() {
		return objMask;
	}

	// NOTE DONE IN pixels, ignoring z-distance
	// As our minimum-bound we take the distance to a sphere (of maximum radius) around the centre point of the ellipsoid
	public double minBoundDistTo( Point3d pnt ) {
		// Distance between tow center points
		double distCenters = dim.getRes().distance( mark.getPos(), pnt );
		return Math.max( distCenters - maxRadius, 0.0);
	}
	
	// We take the minimum of the distances to each possible point
	public double distTo( Point3d pnt ) {
		
		if (assignedObjs.size()==0) {
			return Double.NaN;
		}
		
		double min = Double.MAX_VALUE;
		
		for( RslvdObjMask rom : assignedObjs ) {
			double distSq = dim.getRes().distanceSq(rom.getCenter(), pnt);
			
			if (distSq<min) {
				min = distSq;
			}
		}
		
		return Math.sqrt(min);
	}
	
	public boolean atBorder() {
		return objMask.getBoundingBox().atBorder(dim);
	}
	
	public boolean atBorderXY() {
		return objMask.getBoundingBox().atBorderXY(dim);
	}
}
