package org.anchoranalysis.image.voxel.iterator.changed;

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


import java.nio.ByteBuffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;

final class WithinMask implements ProcessVoxelNeighbour {

	private final ProcessChangedPointAbsoluteMasked pointProcesser;
	private final ObjMask om;
	private final Extent extnt;
	private final Point3i crnrMin;
	
	private Point3i pnt;
	private Point3i rel;
	
	// Current ByteBuffer for the object mask
	private ByteBuffer bbOM;
	private int z1;
	private byte maskOffVal;
	
	private int offsetXYAtPnt;
	
	public WithinMask(ProcessChangedPointAbsoluteMasked pointProcesser,	ObjMask om) {
		this.pointProcesser = pointProcesser;
		this.om = om;
		this.maskOffVal = om.getBinaryValuesByte().getOffByte();
		this.extnt = om.getVoxelBox().extnt();
		this.crnrMin = om.getBoundingBox().getCrnrMin();
	}
	
	@Override
	public void initSource(Point3i pnt) {
		this.pnt = pnt;
		
		rel = new Point3i(pnt);
		rel.sub(crnrMin);
		
		offsetXYAtPnt = extnt.offsetSlice(rel);
	}


	@Override
	public boolean notifyChangeZ(int zChange) {
		z1 = pnt.getZ() + zChange;
		
		int relZ1 = rel.getZ() + zChange;
		
		if (relZ1<0 || relZ1>=extnt.getZ()) {
			this.bbOM = null;
			return false;
		}
		
		int zRel = z1-crnrMin.getZ();
		this.bbOM = om.getVoxelBox().getPixelsForPlane(zRel).buffer();
		
		pointProcesser.notifyChangeZ(zChange, z1, bbOM);
		return true;
	}

	@Override
	public boolean processPoint(int xChange, int yChange) {

		int x1 = pnt.getX() + xChange;
		int y1 = pnt.getY() + yChange;
		
		int relX1 = rel.getX() + xChange;
		int relY1 = rel.getY() + yChange;
		
		if (relX1<0) {
			return false;
		}
		
		if (relX1>=extnt.getX()) {
			return false;
		}

		if (relY1<0) {
			return false;
		}
		
		if (relY1>=extnt.getY()) {
			return false;
		}

		int offset = offsetXYAtPnt + xChange + (yChange*extnt.getX());
		
		if (bbOM.get(offset)==maskOffVal) {
			return false;
		}
		
		return pointProcesser.processPoint(xChange, yChange,x1,y1,offset);
	}
	
}