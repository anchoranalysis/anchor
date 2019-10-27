package org.anchoranalysis.image.seed;

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

import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;

public class SeedCenterPoint extends Seed {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Point3d center = new Point3d();
	private BinaryValuesByte bvb;

	SeedCenterPoint(Point3i center) {
		this(
			PointConverter.doubleFromInt(center)
		);
	}

	SeedCenterPoint(Point3d center) {
		this(center, BinaryValuesByte.getDefault());
	}

	SeedCenterPoint(Point3d center, BinaryValuesByte bvb) {
		super();
		this.center = center;
		this.bvb = bvb;
	}

	public ObjMask createMask() {
		return createCentrePointMask(this.center, bvb );
	}
	
	public Point3d getCenter() {
		return center;
	}

	public void setCenter(Point3d center) {
		this.center = center;
	}
	
	public void scaleXY( double scale ) throws OptionalOperationUnsupportedException {
		this.center.setX( this.center.getX() * scale );
		this.center.setY( this.center.getY() * scale );
	}
	
	
	public void flattenZ() throws OptionalOperationUnsupportedException {
		this.center.setZ(0);
	}
	
	public Seed duplicate() {
		SeedCenterPoint seedNew = new SeedCenterPoint( new Point3d( this.center ) );
		return seedNew;
	}

	@Override
	public void growToZ(int z) throws OptionalOperationUnsupportedException {
		this.center.setZ( z/2 );
		
	}

	@Override
	public boolean equalsDeep(Seed other) {
		if (other instanceof SeedCenterPoint) {
			SeedCenterPoint otherCast = (SeedCenterPoint) other;
			return center.equals(otherCast.center);
		} else {
			return false;
		}
	}
	
	private static ObjMask createCentrePointMask( Point3d pnt, BinaryValuesByte bv ) {
		
		BoundingBox bbox = new BoundingBox(
			new Point3i(pnt),
			new Extent(1,1,1)
		);
		ObjMask om = new ObjMask(bbox);
		om.getVoxelBox().getPixelsForPlane(0).buffer().put(0, bv.getOnByte() );
		return om;
	}
}
