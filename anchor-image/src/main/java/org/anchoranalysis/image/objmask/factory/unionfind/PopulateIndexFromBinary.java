package org.anchoranalysis.image.objmask.factory.unionfind;

import java.nio.Buffer;
import java.nio.IntBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.jgrapht.alg.util.UnionFind;

abstract class PopulateIndexFromBinary<T extends Buffer> {
	
	public abstract int populateIndexFromBinary(
		BinaryVoxelBox<T> visited,
		VoxelBox<IntBuffer> indexBuffer,
		SlidingBuffer<IntBuffer> slidingIndex,
		UnionFind<Integer> unionIndex,
		boolean bigNghb
	) throws OperationFailedException;
}