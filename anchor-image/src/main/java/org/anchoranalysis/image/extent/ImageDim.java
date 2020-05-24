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
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;

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
	
	/** Construct with default extent (0 for each dimension) and resolution (1.0 for each dimension) */
	public ImageDim() {
		this.extent = new Extent();
		this.res = new ImageRes();
	}

	/** Construct with an explicit extent and resolution */
	public ImageDim( Extent extent, ImageRes res ) {
		this.extent = new Extent(extent);
		this.res = new ImageRes( res );
	}
	
	/** Calculates image-dimensions for x,y,z using default resolution (1.0 for each dimension) */
	public ImageDim( int x, int y, int z ) {
		this.extent = new Extent(x, y, z);
		this.res = new ImageRes();
	}
	
	/** Copy constructor */
	public ImageDim( ImageDim dim ) {
		this.extent = new Extent( dim.extent );
		this.res = new ImageRes( dim.res );
	}
	
	public void scaleXYTo( int x, int y ) {
		
		ScaleFactor sf = ScaleFactorUtilities.calcRelativeScale(
			extent,
			new Extent(x,y,0)
		);
				
		extent.setXY(x,y);
		
		res.scaleXY(sf);
	}
	
	public void scaleXYBy( ScaleFactor sf ) {
				
		extent.scaleXYBy(sf);
		
		res.scaleXY(sf);
	}
	
	public long getVolume() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extent == null) ? 0 : extent.hashCode());
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
		ImageDim other = (ImageDim) obj;
		if (extent == null) {
			if (other.extent != null)
				return false;
		} else if (!extent.equals(other.extent))
			return false;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		return true;
	}
}
