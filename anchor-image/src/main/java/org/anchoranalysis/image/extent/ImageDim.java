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
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;

/**
 * The dimensions of an image (in voxels), together with the image resolution
 */
public class ImageDim implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ImageRes res;
	private Extent extent;
	
	public ImageDim() {
		this.extent = new Extent();
		this.res = new ImageRes();
	}
	
	public ImageDim( Extent extent, ImageRes res ) {
		this.extent = new Extent(extent);
		this.res = new ImageRes( res );
	}
	
	public ImageDim( ImageDim dim ) {
		this.extent = new Extent( dim.extent );
		this.res = new ImageRes( dim.res );
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits( this.res.getX() );
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits( this.res.getY() );
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits( this.res.getZ() );
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageDim other = (ImageDim) obj;
		if (!equalsLongBits( this.res.getX(), other.res.getX() )) {
			return false;
		}
		if (!equalsLongBits( this.res.getY(), other.res.getY() )) {
			return false;
		}
		if (!equalsLongBits( this.res.getZ(), other.res.getZ() )) {
			return false;
		}
		return true;
	}
	
	public void scaleXYTo( int x, int y ) {
		
		double xChange = ((double) extent.getX() ) / x; 
		double yChange = ((double) extent.getY() ) / y;
				
		extent.setXY(x,y);
		
		res.scaleXY(xChange, yChange);
	}
	
	public void scaleXYBy( double xFactor, double yFactor ) {
				
		extent.scaleXYBy(xFactor, yFactor);
		
		res.scaleXY(xFactor, yFactor);
	}
	
	public int getVolume() {
		return extent.getVolume();
	}
	
	public int getVolumeXY() {
		return extent.getVolumeXY();
	}

	public final int getX() {
		return extent.getX();
	}

	public final int getY() {
		return extent.getY();
	}

	public final int getZ() {
		return extent.getZ();
	}

	public final int offset(int x, int y) {
		return extent.offset(x, y);
	}

	public final int offset(int x, int y, int z) {
		return extent.offset(x, y, z);
	}

	public final void setX(int arg0) {
		extent.setX(arg0);
	}

	public final void setY(int arg0) {
		extent.setY(arg0);
	}

	public final void setZ(int arg0) {
		extent.setZ(arg0);
	}

	public Extent getExtnt() {
		return extent;
	}
	
	public boolean contains( Point3d pnt ) {
		return extent.contains(pnt);
	}
	
	public boolean contains( Point3i pnt ) {
		return extent.contains(pnt);
	}
	
	public boolean equals( ImageDim obj ) {
		return extent.equals( obj.extent );
	}

	public final int offset(Point3i pnt) {
		return extent.offset(pnt);
	}

	public ImageRes getRes() {
		return res;
	}

	public void setRes(ImageRes res) {
		this.res = res;
	}

	public boolean contains(BoundingBox bbox) {
		return extent.contains(bbox);
	}

	@Override
	public String toString() {
		return extent.toString();
	}
		
	private static boolean equalsLongBits( double a, double b ) {
		return Double.doubleToLongBits(a)==Double.doubleToLongBits(b);
	}
}
