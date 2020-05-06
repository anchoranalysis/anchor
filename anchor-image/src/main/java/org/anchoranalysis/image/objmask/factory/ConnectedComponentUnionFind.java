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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.jgrapht.alg.util.UnionFind;

class ConnectedComponentUnionFind {


	public static void visitRegionByte( BinaryVoxelBox<ByteBuffer> visited, ObjMaskCollection omc, int minNumberVoxels, boolean bigNghb ) throws OperationFailedException {
	
		UnionFind<Integer> unionIndex = new UnionFind<Integer>( new HashSet<Integer>() );
	
		VoxelBox<IntBuffer> indexBuffer = VoxelBoxFactory.instance().getInt().create( visited.extnt() );
		
		SlidingBuffer<IntBuffer> slidingIndex = new SlidingBuffer<>( indexBuffer );
		slidingIndex.init();

		int maxBigIDAdded = populateIndexFromBinaryByte(
			visited,
			indexBuffer,
			slidingIndex,
			unionIndex,
			bigNghb
		);
		
		processIndexBuffer(
			maxBigIDAdded,
			unionIndex,
			indexBuffer,
			omc,
			minNumberVoxels
		);
	}
	
	
	public static void visitRegionInt( BinaryVoxelBox<IntBuffer> visited, ObjMaskCollection omc, int minNumberVoxels, boolean bigNghb ) throws OperationFailedException {
	
		UnionFind<Integer> unionIndex = new UnionFind<Integer>( new HashSet<Integer>() );
	
		VoxelBox<IntBuffer> indexBuffer = VoxelBoxFactory.instance().getInt().create( visited.extnt() );
		
		SlidingBuffer<IntBuffer> slidingIndex = new SlidingBuffer<>( indexBuffer );
		slidingIndex.init();

		int maxBigIDAdded = populateIndexFromBinaryInt(
			visited,
			indexBuffer,
			slidingIndex,
			unionIndex,
			bigNghb
		);
		
		processIndexBuffer(
			maxBigIDAdded,
			unionIndex,
			indexBuffer,
			omc,
			minNumberVoxels
		);
	}
	
	private static int populateIndexFromBinaryByte(
		BinaryVoxelBox<ByteBuffer> visited,
		VoxelBox<IntBuffer> indexBuffer,
		SlidingBuffer<IntBuffer> slidingIndex,
		UnionFind<Integer> unionIndex,
		boolean bigNghb
	) throws OperationFailedException {
		boolean do3D = indexBuffer.extnt().getZ()>1;
		
		MergeWithNghbs mergeWithNgbs = new MergeWithNghbs(slidingIndex, unionIndex, do3D, bigNghb);
	
		int cnt = 1;
		BinaryValuesByte bvb = visited.getBinaryValues().createByte();
		Extent extnt = visited.extnt();
		for (int z=0; z<extnt.getZ(); z++) {
			
			ByteBuffer bbBinary = visited.getVoxelBox().getPixelsForPlane(z).buffer();
			IntBuffer bbIndex = indexBuffer.getPixelsForPlane(z).buffer();
			
			int offset = 0;
			
			for (int y=0; y<extnt.getY(); y++) {
				for (int x=0; x<extnt.getX(); x++) {
					
					if (bbBinary.get(offset)==bvb.getOnByte() && bbIndex.get(offset)==0) {
						
						int nghbLab = mergeWithNgbs.calcMinNghbLabel(x, y, z, offset);
						if (nghbLab==-1) {
							bbBinary.put(offset, (byte) cnt );
							bbIndex.put(offset, cnt);
							unionIndex.addElement(cnt);
							cnt++;
						} else {
							bbIndex.put(offset, nghbLab);
						}
					}
					
					offset++;
				}
				
			}
			
			slidingIndex.shift();
		}
		return cnt-1;
	
	}
	
	
	private static int populateIndexFromBinaryInt(
			BinaryVoxelBox<IntBuffer> visited,
			VoxelBox<IntBuffer> indexBuffer,
			SlidingBuffer<IntBuffer> slidingIndex,
			UnionFind<Integer> unionIndex,
			boolean bigNghb
		) {
			boolean do3D = indexBuffer.extnt().getZ()>1;
			
			MergeWithNghbs mergeWithNgbs = new MergeWithNghbs(slidingIndex, unionIndex, do3D, bigNghb);
			
			int cnt = 1;
			BinaryValues bv = visited.getBinaryValues();
			Extent extnt = visited.extnt();
			for (int z=0; z<extnt.getZ(); z++) {
				
				IntBuffer bbBinary = visited.getVoxelBox().getPixelsForPlane(z).buffer();
				IntBuffer bbIndex = indexBuffer.getPixelsForPlane(z).buffer();
				
				int offset = 0;
				
				for (int y=0; y<extnt.getY(); y++) {
					for (int x=0; x<extnt.getX(); x++) {
						
						if (bbBinary.get(offset)==bv.getOnInt() && bbIndex.get(offset)==0) {
							
							int nghbLab = mergeWithNgbs.calcMinNghbLabel(x, y, z, offset);
							if (nghbLab==-1) {
								bbBinary.put(offset, (byte) cnt );
								bbIndex.put(offset, cnt);
								unionIndex.addElement(cnt);
								cnt++;
							} else {
								bbIndex.put(offset, nghbLab);
							}
						}
						
						offset++;
					}
					
				}
				
				slidingIndex.shift();
			}
			return cnt-1;

		}
	
	// Assumes unionFind begins at 1
	private static Set<Integer> setFromUnionFind( int maxValue, UnionFind<Integer> unionIndex ) {
		TreeSet<Integer> set = new TreeSet<Integer>();
		for( int i=1; i<=maxValue; i++) {
			set.add( unionIndex.find(i) );
		}
		return set;
	}
	
	// Maps the set of integers to a sequence of integers starting at 1
	private static Map<Integer,Integer> mapValuesToContiguousSet( Set<Integer> setIDs ) {
		// We create a map between big ID and small ID
		Map<Integer,Integer> mapIDOrdered = new TreeMap<Integer,Integer>();
		int cnt = 1;
		for( Integer id : setIDs ) {
			mapIDOrdered.put(id,cnt);
			cnt++;
		}
		return mapIDOrdered;
	}
	
	
	
	private static class BoundingBoxWithCount {
	
		private static Extent extnt1 = new Extent(1,1,1);
		
		private BoundingBox BoundingBox;
		private int cnt = 0;
		
		public void add( Point3i pnt ) {
			if (BoundingBox==null) {
				BoundingBox = new BoundingBox( pnt, extnt1 );
			} else {
				BoundingBox.add(pnt);
			}
			cnt++;
		}

		public int getCnt() {
			return cnt;
		}

		public BoundingBox getBoundingBox() {
			return BoundingBox;
		}
	}
	
	private static BoundingBoxWithCount[] createBBoxArray( int size ) {
		BoundingBoxWithCount[] bboxArr = new BoundingBoxWithCount[size];
		for( int i=0; i<bboxArr.length; i++) {
			bboxArr[i] = new BoundingBoxWithCount();
		}
		return bboxArr;
	}
	
	private static void addPntsAndAssignNewIDs(
		VoxelBox<IntBuffer> indexBuffer,
		UnionFind<Integer> unionIndex,
		Map<Integer,Integer> mapIDOrdered,
		BoundingBoxWithCount[] bboxArr
	) {
		
		Point3i pnt = new Point3i();
		Extent extnt = indexBuffer.extnt();
		for (pnt.setZ(0); pnt.getZ()<extnt.getZ(); pnt.incrZ()) {
			
			IntBuffer bbIndex = indexBuffer.getPixelsForPlane(pnt.getZ()).buffer();
			
			int offset = 0;
			
			for (pnt.setY(0); pnt.getY()<extnt.getY(); pnt.incrY()) {
				for (pnt.setX(0); pnt.getX()<extnt.getX(); pnt.incrX()) {
					
					int idBig = bbIndex.get(offset); 
					if (idBig!=0) {
						
						Integer idSmall = mapIDOrdered.get( unionIndex.find(idBig) );
						
						BoundingBoxWithCount bbox = bboxArr[ idSmall-1 ];
						bbox.add(pnt);
						
						bbIndex.put(offset, idSmall);
					}
					offset++;
				}
				
			}
		}
	}
	
	private static ObjMaskCollection extractMasksInto(
		BoundingBoxWithCount[] bboxArr,
		Map<Integer,Integer> mapIDOrdered,
		VoxelBox<IntBuffer> indexBuffer,
		UnionFind<Integer> unionIndex,
		int minNumberVoxels,
		ObjMaskCollection omc
	) {
		
		for( Integer bigID : mapIDOrdered.keySet()) {
			int smallID = mapIDOrdered.get(bigID);
			
			BoundingBoxWithCount bboxWithCnt = bboxArr[smallID-1];
			
			if (bboxWithCnt.getCnt()>=minNumberVoxels) {
				ObjMask om = indexBuffer.equalMask(bboxWithCnt.getBoundingBox(), smallID);
				omc.add(om);
			}
		}
		return omc;
	}
	
	private static void processIndexBuffer(
		int maxBigIDAdded,
		UnionFind<Integer> unionIndex,
		VoxelBox<IntBuffer> indexBuffer,
		ObjMaskCollection omc,
		int minNumberVoxels
	) {
		Set<Integer> primaryIDs = setFromUnionFind( maxBigIDAdded, unionIndex );
		
		Map<Integer,Integer> mapIDOrdered = mapValuesToContiguousSet( primaryIDs );
		
		BoundingBoxWithCount[] bboxArr = createBBoxArray( mapIDOrdered.size() );
		
		addPntsAndAssignNewIDs(
			indexBuffer,
			unionIndex,
			mapIDOrdered,
			bboxArr
		);
		
		extractMasksInto(
			bboxArr,
			mapIDOrdered,
			indexBuffer,
			unionIndex,
			minNumberVoxels,
			omc
		);
	}
	
}
