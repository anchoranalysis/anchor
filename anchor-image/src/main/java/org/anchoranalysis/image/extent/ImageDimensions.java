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
 * 
 * <p>This class is IMMUTABLE</p>.
 */
public final class ImageDimensions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ImageResolution res;
	private final Extent extent;
	
	/** Construct with an explicit extent and default resolution (1.0 for each dimension)*/
	public ImageDimensions(Extent extent) {
		this(extent, new ImageResolution());
	}

	/** Construct with an explicit extent and resolution */
	public ImageDimensions( Extent extent, ImageResolution res ) {
		this.extent = extent;
		this.res = res;
	}
	
	public ImageDimensions scaleXYTo( int x, int y ) {
		Extent extentScaled = new Extent(
			x,
			y,
			extent.getZ()
		); 
		ScaleFactor sf = ScaleFactorUtilities.calcRelativeScale(extent, extentScaled);
		return new ImageDimensions(
			extentScaled,
			res.scaleXY(sf)
		);
	}
	
	public ImageDimensions scaleXYBy( ScaleFactor sf ) {
		return new ImageDimensions(
			extent.scaleXYBy(sf),
			res.scaleXY(sf)
		);
	}
		
	public ImageDimensions duplicateChangeZ(int z) {
		return new ImageDimensions(
			extent.duplicateChangeZ(z),
			res
		);
	}
	
	public ImageDimensions duplicateChangeRes(ImageResolution resToAssign) {
		return new ImageDimensions(
			extent,
			resToAssign
		);
	}
	
	public long getVolume() {
		return extent.getVolume();
	}
	
	public int getVolumeXY() {
		return extent.getVolumeXY();
	}

	public int getX() {
		return extent.getX();
	}

	public int getY() {
		return extent.getY();
	}

	public int getZ() {
		return extent.getZ();
	}

	public int offset(int x, int y) {
		return extent.offset(x, y);
	}

	public int offset(int x, int y, int z) {
		return extent.offset(x, y, z);
	}

	public Extent getExtent() {
		return extent;
	}
	
	public boolean contains( Point3d pnt ) {
		return extent.contains(pnt);
	}
	
	public boolean contains( Point3i pnt ) {
		return extent.contains(pnt);
	}
	
	public boolean equals( ImageDimensions obj ) {
		return extent.equals(obj.extent);
	}

	public final int offset(Point3i pnt) {
		return extent.offset(pnt);
	}
	
	public final int offsetSlice(Point3i pnt) {
		return extent.offsetSlice(pnt);
	}

	public ImageResolution getRes() {
		return res;
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
		ImageDimensions other = (ImageDimensions) obj;
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
