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
import java.util.function.Consumer;

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;

/**
 * Width, height etc. of image in 2 or 3 dimensions
 * 
 * <p>This class is IMMUTABLE</p>
 */
public final class Extent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int sxy;
	
	// Lengths in each dimension
	private final ReadableTuple3i len;
	
	public Extent() {
		this( new Point3i(0,0,0) );
	}
	
	public Extent(int x, int y, int z) {
		this( new Point3i( x, y, z) );
	}
	
	/**
	 * Constructor
	 * 
	 * <p>The point will be taken ownership by the extent, and should not be modified thereafter.</p>
	 * 
	 * @param len the length of each axis
	 */
	private Extent(ReadableTuple3i len) {
		this.len = len;
		this.sxy = len.getX() * len.getY();
	}

	public int getVolumeAsInt() {
		long volume = getVolume();
		if (volume>Integer.MAX_VALUE) {
			throw new AnchorFriendlyRuntimeException("The volume cannot be expressed as an int, as it is higher than the maximum bound");
		}
		return (int) volume;
	}
	
	public long getVolume() {
		return ((long) sxy) * len.getZ();
	}
	
	public boolean isEmpty() {
		return (sxy==0) || (len.getZ()==0);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + len.getX();
		result = prime * result + len.getY();
		result = prime * result + len.getZ();
		return result;
	}

	public int getX() {
		return len.getX();
	}

	public int getY() {
		return len.getY();
	}

	public int getZ() {
		return len.getZ();
	}
	
	public int getXEx() {
		return len.getX() + 1;
	}
	
	public int getYEx() {
		return len.getY() + 1;
	}
	
	public int getZEx() {
		return len.getZ() + 1;
	}
		
	public int getValueByDimension(int dimIndex) {
		return len.getValueByDimension(dimIndex);
	}
	
	public int getValueByDimension(AxisType axis) {
		return len.getValueByDimension(axis);
	}
		
	/**
	 * Exposes the extent as a tuple.
	 * 
	 * <p>IMPORTANT! This class is designed to be IMMUTABLE, so this tuple should be treated as read-only, and never modified.</p>
	 * 
	 * @return the extent's width, height, depth as a tuple
	 */
	public ReadableTuple3i asTuple() {
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

	// Calculates an offset of an x and y point in terms of this extent
	public final int offset( int x, int y ) {
		return (y*len.getX()) + x;
	}
	
	// Calculates an offset of an x and y point in terms of this extent
	//  we should cal
	public final int offset( int x, int y, int z ) {
		return (z *sxy) + (y*getX()) + x;
	}
	
	// Calculates an offset of an x and y point in terms of this extent
	//  we should cal
	public final int offset( Point3i pnt ) {
		return offset(pnt.getX(), pnt.getY(), pnt.getZ());
	}
	
	// Calculates an offset of an x and y point in terms of this extent
	public final int offset( Point2i pnt ) {
		return offset(pnt.getX(), pnt.getY(), 0);
	}
	
	// Calculates an offset of an x and y point in terms of this extent
	public final int offsetSlice( Point3i pnt ) {
		return offset(pnt.getX(), pnt.getY(), 0);
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
		int[] extents = createArray();
		Arrays.sort( extents );
		return extents;
	}
	
	public Extent duplicateChangeZ(int z) {
		return new Extent(
			len.getX(),
			len.getY(),
			z
		);
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
	
	public boolean contains( ReadableTuple3i pnt ) {
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
		return contains( bbox.getCornerMin() ) && contains( bbox.calcCornerMax() );
	}
	
	public Extent scaleXYBy( ScaleFactor sf ) {
		return immutablePointOperation( p-> {
			p.setX(
				ScaleFactorUtilities.scaleQuantity(sf.getX(), getX())
			);
			p.setY(
				ScaleFactorUtilities.scaleQuantity(sf.getY(), getY())
			);			
		});
	}
	
	public Extent subtract(ReadableTuple3i toSubtract) {
		return new Extent(
			Point3i.immutableSubtract(len, toSubtract)
		);
	}
	
	public Extent divide( int factor ) {
		return immutablePointOperation( p->p.divideBy(factor) );
	}
	
	/**
	 * Creates a new Extent with each dimension decreased by one
	 * @return the new extent
	 */
	public Extent createMinusOne() {
		return immutablePointOperation( p->p.subtract(1) );
	}
	
	public Extent growBy(int toAdd) {
		return growBy(
			new Point3i(toAdd,toAdd,toAdd)
		);
	}
	
	public Extent growBy(ReadableTuple3i toAdd) {
		return new Extent(
			Point3i.immutableAdd(len, toAdd)
		);
	}
	
	/** Collapses the Z dimension i.e. returns a new extent with the same X- and Y- size but Z-size of 1 */
	public Extent flattenZ() {
		return new Extent(
			new Point3i(len.getX(), len.getY(), 1)
		);
	}
	
	private Extent immutablePointOperation( Consumer<Point3i> pointOperation ) {
		Point3i lenDup = new Point3i(len);
		pointOperation.accept(lenDup);	
		return new Extent(lenDup);
	}
}
