package org.anchoranalysis.image.extent;

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


import java.io.Serializable;

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.scale.ScaleFactor;


/**
 * The resolution of an image i.e. what a single voxel represents in physical units (meters) in x, y, z
 *
 */
public class ImageRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Stores in Metres
	private Point3d res;
	
	public ImageRes() {
		super();
		this.res = new Point3d(1,1,1);
	}
	
	public ImageRes( ImageRes src ) {
		super();
		this.res = new Point3d(src.res);
	}
	
	public ImageRes duplicateFlattenZ( int prevZSize ) {
		ImageRes dup = duplicate();
		dup.setZ( dup.getZ() *prevZSize );
		return dup;
	}
	
	public ImageRes duplicate() {
		return new ImageRes(this);
	}
	
	public double getX() {
		return res.getX();
	}
	
	public double getY() {
		return res.getY();
	}
	
	public double getZ() {
		return res.getZ();
	}
	
	public double unitVolume() {
		return getX() * getY() * getZ();
	}
	
	public double unitArea() {
		return getX() * getY();
	}
	
	public void set( double[] res ) {
		this.res.setX( res[0] );
		this.res.setY( res[1] );
		this.res.setZ( res[2] );
	}
	
	public void setX( double val ) {
		res.setX( val );
	}
	
	public void setY( double val ) {
		res.setY( val );
	}
	
	public void setZ( double val ) {
		res.setZ( val );
	}
	
	public void scaleX( double ratio ) {
		res.setX( res.getX() * ratio );
	}
	
	public void scaleY( double ratio ) {
		res.setY( res.getY() * ratio );
	}
	
	public void scaleXY( ScaleFactor sf ) {
		scaleX( sf.getX() );
		scaleY( sf.getY() );
	}
	
	private double max2D() {
		return Math.max(res.getX(), res.getY());
	}
	
	private double min2D() {
		return Math.min(res.getX(), res.getY());
	}
	
	public double max( boolean do3D ) {
		
		if (do3D) {
			return Math.max(
				max2D(),
				res.getZ()
			);
		} else {
			return max2D();
		}
	}
	
	public double min( boolean do3D ) {
		if (do3D) {
			return Math.min(
				min2D(),
				res.getZ()
			);
		} else {
			return min2D();
		}
	}
	
	public double distanceSq( Point3i pnt1, Point3i pnt2 ) {
		
		double sx = (double) pnt1.getX() - pnt2.getX();
		double sy = (double) pnt1.getY() - pnt2.getY();
		double sz = (double) pnt1.getZ() - pnt2.getZ();
		
		sx *= getX();
		sy *= getY();
		sz *= getZ();
		
		return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2); 
	}
	
	public double distanceSq( Point3d pnt1, Point3d pnt2 ) {
		
		double sx = pnt1.getX() - pnt2.getX();
		double sy = pnt1.getY() - pnt2.getY();
		double sz = pnt1.getZ() - pnt2.getZ();
		
		sx *= getX();
		sy *= getY();
		sz *= getZ();
		
		return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2); 
	}
	
	public double distance( Point3d pnt1, Point3d pnt2 ) {
		return Math.sqrt( distanceSq(pnt1, pnt2) );
	}
	
	public double distance( Point3i pnt1, Point3i pnt2 ) {
		return Math.sqrt( distanceSq(pnt1, pnt2) );
	}
	
	
	public double distSqZRel( Point3i pnt1, Point3i pnt2 ) {
		
		int sx = pnt1.getX() - pnt2.getX();
		int sy = pnt1.getY() - pnt2.getY();
		int sz = pnt1.getZ() - pnt2.getZ();
		
		double szAdj = getZRelRes() * sz;
		
		return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(szAdj, 2); 
	}
	
	public double distZRel( Point3d pnt1, Point3d pnt2 ) {
		return Math.sqrt( distSqZRel(pnt1, pnt2) );
	}
	
	public double distSqZRel( Point3d pnt1, Point3d pnt2 ) {
		
		double sx = pnt1.getX() - pnt2.getX();
		double sy = pnt1.getY() - pnt2.getY();
		double sz = pnt1.getZ() - pnt2.getZ();
		
		sz = sz * getZRelRes();
		
		return Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2); 
	}
	
	public double convertVolume( double val ) {
		double div = res.getX() * res.getY() * res.getZ();
		return val * div;
	}
	
	public double convertArea( double val ) {
		double div = res.getX() * res.getY();
		return val * div;
	}
	
	// Assumes X and Y has constant res, and gives the relative resolution of Z
	public double getZRelRes() {
		return getZ() / getX();
	}

	@Override
	public String toString() {
		return String.format("[%6.3e,%6.3e,%6.3e]", res.getX(), res.getY(), res.getZ());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((res == null) ? 0 : res.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageRes other = (ImageRes) obj;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		return true;
	}

	public final double getValueByDimension(int dimIndex) {
		return res.getValueByDimension(dimIndex);
	}

	public final double getValueByDimension(AxisType axisType) {
		return res.getValueByDimension(axisType);
	}
}
