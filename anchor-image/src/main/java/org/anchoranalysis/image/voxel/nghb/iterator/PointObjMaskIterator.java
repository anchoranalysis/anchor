package org.anchoranalysis.image.voxel.nghb.iterator;

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
import org.anchoranalysis.image.voxel.nghb.IProcessAbsolutePointObjectMask;

public class PointObjMaskIterator extends PointIterator {

	private IProcessAbsolutePointObjectMask pointProcesser;
	private final ObjMask om;
	private final Extent extnt;
	private Point3i crnrMin;
	
	private int pntX;
	private int pntY;
	private int pntZ;

	private int relX;
	private int relY;
	private int relZ;
	
	// Current ByteBuffer for the object mask
	private ByteBuffer bbOM;
	private int z1;
	private byte maskOffVal;
	
	private int offsetXYAtPnt;
	
	public PointObjMaskIterator(IProcessAbsolutePointObjectMask pointProcesser,
			ObjMask om) {
		this.pointProcesser = pointProcesser;
		this.om = om;
		this.maskOffVal = om.getBinaryValuesByte().getOffByte();
		this.extnt = om.getVoxelBox().extnt();
		this.crnrMin = om.getBoundingBox().getCrnrMin();
	}
	
	@Override
	public void initPnt( int pntX, int pntY, int pntZ ) {
		this.pntX = pntX;
		this.pntY = pntY;
		this.pntZ = pntZ;
		
		this.relX = pntX - crnrMin.getX();
		this.relY = pntY - crnrMin.getY();
		this.relZ = pntZ - crnrMin.getZ();
		
		offsetXYAtPnt = extnt.offset(relX, relY);
	}


	@Override
	public boolean notifyChangeZ(int zChange) {
		z1 = pntZ + zChange;
		
		int relZ1 = relZ + zChange;
		
		if (relZ1<0 || relZ1>=extnt.getZ()) {
			this.bbOM = null;
			return false;
		}
		
		int zRel = z1-crnrMin.getZ();
		this.bbOM = om.getVoxelBox().getPixelsForPlane(zRel).buffer();
		
		pointProcesser.notifyChangeZ(zChange, z1, bbOM);
		
		//offsetXYCurrent = 0;
		
		return true;
	}

	@Override
	public boolean processPoint(int xChange, int yChange) {

		int x1 = pntX + xChange;
		int y1 = pntY + yChange;
		
		int relX1 = relX + xChange;
		int relY1 = relY + yChange;
		
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
		//offsetXYCurrent
		
		//int offset = extnt.offset(relX1, relY1);
		
		if (bbOM.get(offset)==maskOffVal) {
			return false;
		}
		
		return pointProcesser.processPoint(xChange, yChange,x1,y1,offset);
	}
	
}