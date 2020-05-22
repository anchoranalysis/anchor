package org.anchoranalysis.image.objmask.factory.unionfind;

import java.nio.Buffer;

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
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.jgrapht.alg.util.UnionFind;

public class ConnectedComponentUnionFind {

	private final int minNumberVoxels;
	
	private final boolean bigNghb;
	
	/**
	 * Constructor
	 * 
	 * @param minNumberVoxels a minimum number of voxels necessary in the connected-component, otherwise it omitted from the output.
	 * @param bigNghb whether to use a smaller or bigger neighbour (in 3D, 6-conn neighbours are used as small)
	 */
	public ConnectedComponentUnionFind(int minNumberVoxels, boolean bigNghb) {
		super();
		this.minNumberVoxels = minNumberVoxels;
		this.bigNghb = bigNghb;
	}	
		
	/**
	 * Converts a binary-voxel-box (byte) into connected components.
	 * 
	 * @param voxels a binary voxel-box to be searched for connected components. It is consumed (modified) during processing.
	 * @return the connected-components derived from the voxel-box
	 * @throws OperationFailedException
	 */
	public ObjMaskCollection deriveConnectedByte(BinaryVoxelBox<ByteBuffer> voxels) throws OperationFailedException {
		ObjMaskCollection omc = new ObjMaskCollection();
		visitRegion(
			voxels,
			omc,
			minNumberVoxels,
			bigNghb,
			new PopulateFromByte()
		);
		return omc;
	}

	/**
	 * Converts a binary-voxel-box (int) into connected components.
	 * 
	 * @param voxels a binary voxel-box to be searched for connected components. It is consumed (modified) during processing.
	 * @return the connected-components derived from the voxel-box
	 * @throws OperationFailedException
	 */
	public ObjMaskCollection deriveConnectedInt(BinaryVoxelBox<IntBuffer> voxels) throws OperationFailedException {
		ObjMaskCollection omc = new ObjMaskCollection();
		visitRegion(
			voxels,
			omc,
			minNumberVoxels,
			bigNghb,
			new PopulateFromInt()
		);
		return omc;
	}
	
	private static <T extends Buffer> void visitRegion(
		BinaryVoxelBox<T> visited,
		ObjMaskCollection omc,
		int minNumberVoxels,
		boolean bigNghb,
		PopulateIndexFromBinary<T> populateIndex
	) throws OperationFailedException {
		
		UnionFind<Integer> unionIndex = new UnionFind<>( new HashSet<Integer>() );
	
		VoxelBox<IntBuffer> indexBuffer = VoxelBoxFactory.instance().getInt().create( visited.extnt() );
		
		SlidingBuffer<IntBuffer> slidingIndex = new SlidingBuffer<>( indexBuffer );
		slidingIndex.init();

		int maxBigIDAdded = populateIndex.populateIndexFromBinary(
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
