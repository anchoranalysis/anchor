package org.anchoranalysis.image.objmask.factory;

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
import java.nio.IntBuffer;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.nghb.BigNghb;
import org.anchoranalysis.image.voxel.nghb.IProcessAbsolutePoint;
import org.anchoranalysis.image.voxel.nghb.IProcessAbsolutePointObjectMask;
import org.anchoranalysis.image.voxel.nghb.Nghb;
import org.anchoranalysis.image.voxel.nghb.SmallNghb;
import org.anchoranalysis.image.voxel.nghb.iterator.PointExtntIterator;
import org.anchoranalysis.image.voxel.nghb.iterator.PointIterator;
import org.anchoranalysis.image.voxel.nghb.iterator.PointObjMaskIterator;
import org.jgrapht.alg.util.UnionFind;

class MergeWithNghbs {

	private boolean do3D = false;
	
	private PointTester pt;
	private PointIterator pointIterator;

	private Nghb nghb;

	// Without mask
	public MergeWithNghbs( SlidingBuffer<IntBuffer> slidingIndex, UnionFind<Integer> unionIndex, boolean do3D, boolean bigNghb ) {
		this.do3D = do3D;
		
		nghb = bigNghb ? new BigNghb() : new SmallNghb();
		
		this.pt = new PointTester(slidingIndex,unionIndex);
		this.pointIterator = new PointExtntIterator(slidingIndex.extnt(), pt);
	}
	
	// Masked
	public MergeWithNghbs( SlidingBuffer<IntBuffer> slidingIndex, UnionFind<Integer> unionIndex, boolean do3D, boolean bigNghb, ObjMask om ) {
		this.do3D = do3D;

		nghb = bigNghb ? new BigNghb() : new SmallNghb();
		
		this.pt = new PointTester(slidingIndex,unionIndex);
		this.pointIterator = new PointObjMaskIterator(pt, om);
	}
	
	private static class PointTester implements IProcessAbsolutePoint, IProcessAbsolutePointObjectMask {
		
		private int indxBuffer;
		
		private int minLabel;
		
		private IntBuffer bbIndex;
		
		private Extent extnt;
		
		private SlidingBuffer<IntBuffer> slidingIndex;
		private UnionFind<Integer> unionIndex;
		
		public PointTester( SlidingBuffer<IntBuffer> slidingIndex, UnionFind<Integer> unionIndex ) {
			this.slidingIndex = slidingIndex;
			this.unionIndex = unionIndex;
			extnt = slidingIndex.extnt();
		}

		public void initPnt( int indxBuffer ) {
			this.indxBuffer = indxBuffer;
			minLabel = -1;
		}


		@Override
		public boolean processPoint( int xChange, int yChange, int x1, int y1) {
			
			int indxChange = extnt.offset(xChange, yChange);
			int indxNew = indxBuffer + indxChange;

			int indexVal = bbIndex.get( indxNew );
			
			if (indexVal==0) {
				return false;
			}
			
			if(minLabel==-1) {
				minLabel = indexVal;
			} else {
				
				if(indexVal!=minLabel) {
					
					unionIndex.union(minLabel, indexVal);
					
					if( indexVal < minLabel ) {
						minLabel = indexVal;
					}
				}
				
			}
			return true;
		}
		
		@Override
		public boolean processPoint(int xChange, int yChange, int x1, int y1,
				int objectMaskOffset) {
			return processPoint(xChange, yChange, x1, y1);
		}


		@Override
		public void notifyChangeZ(int zChange, int z1) {
			this.bbIndex = slidingIndex.bufferRel(zChange).buffer();
			assert(bbIndex!=null);
		}

		@Override
		public void notifyChangeZ(int zChange, int z1,
				ByteBuffer objectMaskBuffer) {
			notifyChangeZ(zChange, z1);
		}

		public int getMinLabel() {
			assert(minLabel!=0);
			return minLabel;
		}

		

	}
	
	// Calculates the minimum label of the neighbours, making sure to merge any different values
	//   -1 indicates that there is no indexed neighbour
	public int calcMinNghbLabel( int x, int y, int z, int indxBuffer ) {
		
		this.pointIterator.initPnt(x, y, z);
		this.pt.initPnt(indxBuffer);
		
		nghb.processAllPointsInNghb(do3D, pointIterator);

		return pt.getMinLabel();
	}
}
