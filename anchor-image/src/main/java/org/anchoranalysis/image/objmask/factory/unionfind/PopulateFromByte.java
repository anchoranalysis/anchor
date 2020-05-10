package org.anchoranalysis.image.objmask.factory.unionfind;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.jgrapht.alg.util.UnionFind;

class PopulateFromByte extends PopulateIndexFromBinary<ByteBuffer> {
	
	@Override
	public int populateIndexFromBinary(
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
}