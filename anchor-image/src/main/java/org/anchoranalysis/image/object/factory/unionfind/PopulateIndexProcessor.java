package org.anchoranalysis.image.object.factory.unionfind;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
	public void process(Point3i point, T buffer, int offsetSlice) {
		if (bufferReaderWriter.isBufferOn(buffer,offsetSlice,bv,bvb) && bbIndex.get(offsetSlice)==0) {
			
			int nghbLab = mergeWithNgbs.calcMinNghbLabel(
				point,
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
