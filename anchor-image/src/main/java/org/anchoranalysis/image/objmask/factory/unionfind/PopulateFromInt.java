package org.anchoranalysis.image.objmask.factory.unionfind;

import java.nio.IntBuffer;

import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.jgrapht.alg.util.UnionFind;

class PopulateFromInt extends PopulateIndexFromBinary<IntBuffer> {

	@Override
	public int populateIndexFromBinary(
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
}