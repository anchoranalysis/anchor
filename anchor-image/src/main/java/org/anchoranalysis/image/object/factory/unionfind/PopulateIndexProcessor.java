package org.anchoranalysis.image.object.factory.unionfind;

import java.nio.Buffer;
import java.nio.IntBuffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.iterator.ProcessVoxelSliceBuffer;

class PopulateIndexProcessor<T extends Buffer> implements ProcessVoxelSliceBuffer<T> {
	
	private VoxelBox<IntBuffer> indexBuffer;
	private MergeWithNghbs mergeWithNgbs;
	private BinaryValues bv;
	private BinaryValuesByte bvb;
	private final BufferReadWrite<T> bufferReaderWriter;
	
	private IntBuffer bbIndex;
	private int count=1;
	
	public PopulateIndexProcessor(
		BinaryVoxelBox<T> visited,
		VoxelBox<IntBuffer> indexBuffer,
		MergeWithNghbs mergeWithNgbs,
		BufferReadWrite<T> bufferReaderWriter
	) {
		this.indexBuffer = indexBuffer;
		this.mergeWithNgbs = mergeWithNgbs;
		this.bufferReaderWriter = bufferReaderWriter;
		
		bv = visited.getBinaryValues();
		bvb = bv.createByte();
	}
	
	@Override
	public void notifyChangeZ(int z) {
		bbIndex = indexBuffer.getPixelsForPlane(z).buffer();
		if (z!=0) {
			mergeWithNgbs.shift();
		}
	}		
	
	@Override
	public void process(Point3i pnt, T buffer, int offsetSlice) {
		if (bufferReaderWriter.isBufferOn(buffer,offsetSlice,bv,bvb) && bbIndex.get(offsetSlice)==0) {
			
			int nghbLab = mergeWithNgbs.calcMinNghbLabel(
				pnt,
				0,
				offsetSlice
			);
			if (nghbLab==-1) {
				bufferReaderWriter.putBufferCnt(buffer, offsetSlice, count);
				bbIndex.put(offsetSlice, count);
				mergeWithNgbs.addElement(count);
				count++;
			} else {
				bbIndex.put(offsetSlice, nghbLab);
			}
		}
	}

	public int getCount() {
		return count;
	}
}