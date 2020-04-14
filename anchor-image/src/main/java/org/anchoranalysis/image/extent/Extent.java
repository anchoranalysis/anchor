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
import java.util.Arrays;

import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.Tuple3i;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;

/**
 * Width, height etc. of image in 2 or 3 dimensions  
 */
public final class Extent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int sxy;
	
	// Lengths in each dimension
	private Point3i len;
	
	public Extent() {
		this.sxy = 0;
		this.len = new Point3i(0,0,0);
	}
	
	public Extent( Extent src ) {
		this.len = new Point3i(src.len);
		sxy = src.sxy;
	}
	
	public Extent( Point3i len ) {
		this.len = new Point3i(len);
		updateSxy();
	}
	
	public Extent(int x, int y, int z) {
		this.len = new Point3i( x, y, z);
		updateSxy();
	}
	
	public void shrinkBy( int size ) {
		len.sub(
			new Point3i(size,size,size)
		);
		updateSxy();
	}
	
	public void growBy( int size ) {
		len.add(
			new Point3i(size,size,size)
		);
		updateSxy();
	}
	
	/** Collapses the Z dimension i.e. immutably returns a new extent with the same X- and Y- size but Z-size of 1 */
	public Extent flatten() {
		return new Extent(
			new Point3i(len.getX(), len.getY(), 1)
		);
	}
	
	private void updateSxy() {
		this.sxy = len.getX() * len.getY();
	}

	public int getVolume() {
		return sxy * len.getZ();
	}
	
	public int getVolumeXY() {
		return sxy;
	}
	
	/** Calculates the total number of pixel positions needed to represent this bounding box as a pixel array
	 *  This is not the same as volume, both the start and end pixel are included
	 **/
	public int totalNumPixelPositions() {
		return  (len.getX()+1) * (len.getY()+1) * (len.getZ()+1);
	}
	
	public void setSize( int[] size ) {
		len.setX( size[0] );
		len.setY( size[1] );
		len.setZ( size[2] );
		updateSxy();
	}
	
	public int[] getSize() {
		int[] arr = new int[3];
		arr[0] = len.getX();
		arr[1] = len.getY();
		arr[2] = len.getZ();
		return arr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + len.getX();
		result = prime * result + len.getY();
		result = prime * result + len.getZ();
		return result;
	}

	public final int getX() {
		return len.getX();
	}
	

	public final int getY() {
		return len.getY();
	}

	public final int getZ() {
		return len.getZ();
	}
	
	public final int getXEx() {
		return len.getX() + 1;
	}
	
	public final int getYEx() {
		return len.getY() + 1;
	}
	
	public final int getZEx() {
		return len.getZ() + 1;
	}

	public final void setX(int x) {
		len.setX(x);
		updateSxy();
	}

	public final void setY(int y) {
		len.setY(y);
		updateSxy();
	}

	public final void setZ(int z) {
		len.setZ(z);
	}
	
	public final int getValueByDimension( int dimIndex ) {
		switch( dimIndex ) {
		case 0:
			return getX();
		case 1:
			return getY();
		case 2:
			return getZ();
		default:
			assert false;
			return 0;
		}
	}
	
	public Tuple3i asTuple() {
		return len;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Extent other = (Extent) obj;
		
		if (!len.equals(other.len)) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d,%d]",getX(),getY(),getZ());
	}

	// Calculates an offset of an x and y point in terms of this extnt
	public final int offset( int x, int y ) {
		return (y*len.getX()) + x;
	}
	
	// Calculates an offset of an x and y point in terms of this extnt
	//  we should cal
	public final int offset( int x, int y, int z ) {
		return (z *sxy) + (y*getX()) + x;
	}
	
	// Calculates an offset of an x and y point in terms of this extnt
	//  we should cal
	public final int offset( Point3i pnt ) {
		return offset(pnt.getX(), pnt.getY(), pnt.getZ());
	}
	
	// Calculates an offset of an x and y point in terms of this extnt
	public final int offset( Point2i pnt ) {
		return offset(pnt.getX(), pnt.getY(), 0);
	}
	
	// Calculates an offset of an x and y point in terms of this extnt
	public final int offsetSlice( Point3i pnt ) {
		return offset(pnt.getX(), pnt.getY(), 0);
	}
	
	public void setXY( int x, int y ) {
		setX(x);
		setY(y);
	}
	
	public Extent ex() {
		return new Extent( getX()+1, getY()+1, getZ()+1 );
	}
	
	public int[] createArray() {
		int[] arr = new int[3];
		arr[0] = getX();
		arr[1] = getY();
		arr[2] = getZ();
		return arr;
	}
	
	public int[] createOrderedArray() {
		int[] extnts = createArray();
		Arrays.sort( extnts );
		return extnts;
	}

	public boolean containsX( double x ) {
		return x>=0 && x<getX();
	}
	public boolean containsY( double y ) {
		return y>=0 && y<getY();
	}
	
	public boolean containsZ( double z ) {
		return z>=0 && z<getZ();
	}
	
	public boolean containsX( int x ) {
		return x>=0 && x<getX();
	}
	public boolean containsY( int y ) {
		return y>=0 && y<getY();
	}
	
	public boolean containsZ( int z ) {
		return z>=0 && z<getZ();
	}
	
	public boolean contains( Point3d pnt ) {
		return containsX(pnt.getX()) && containsY(pnt.getY()) && containsZ(pnt.getZ());
	}
	
	public boolean contains( Point3i pnt ) {
		return containsX(pnt.getX()) && containsY(pnt.getY()) && containsZ(pnt.getZ());
	}
	
	public boolean contains( int x, int y, int z ) {
		
		if (x < 0) {
			return false;
		}
		
		if (y < 0) {
			return false;
		}
		
		if (z < 0) {
			return false;
		}
		
		if (x >= len.getX()) {
			return false;
		}
		
		if (y >= len.getY()) {
			return false;
		}
		
		if (z >= len.getZ()) {
			return false;
		}
		
		return true;
	}
	
	public boolean contains( BoundingBox bbox ) {
		return contains( bbox.getCrnrMin() ) && contains( bbox.calcCrnrMax() );
	}
	
	public void scaleXYBy( ScaleFactor sf ) {
		setXY(
			ScaleFactorUtilities.multiplyAsInt(sf.getX(), getX()),
			ScaleFactorUtilities.multiplyAsInt(sf.getY(), getY())
		);
	}
	
	public void subtract( Extent e ) {
		this.len.sub( e.asTuple() );
	}
	
	public void divide( int factor ) {
		this.len.div( factor );
	}
	
	/**
	 * Creates a new Extent with each dimension depcreated by one
	 * @return the new extent
	 */
	public Extent createMinusOne() {
		Point3i lenDup = new Point3i(len);
		lenDup.sub(1);	
		return new Extent(lenDup);
	}
}
