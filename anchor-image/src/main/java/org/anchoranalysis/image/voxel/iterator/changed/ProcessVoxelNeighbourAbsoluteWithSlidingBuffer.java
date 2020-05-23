package org.anchoranalysis.image.voxel.iterator.changed;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public abstract class ProcessVoxelNeighbourAbsoluteWithSlidingBuffer implements ProcessVoxelNeighbourAbsolute {

	private final SlidingBuffer<?> rbb;
	private final Extent extent;
	
	private VoxelBuffer<?> bb;
	protected int zChange;
	protected int sourceVal;	
	private int sourceOffsetXY;
	
	
	protected ProcessVoxelNeighbourAbsoluteWithSlidingBuffer(SlidingBuffer<?> rbb) {
		this.rbb = rbb;
		this.extent = rbb.extnt();
	}
	
	/**
	 * The value and offset for the source point (around which we process neighbours)
	 * 
	 * @param sourceVal the value of the source pixel
	 * @param sourceOffsetXY the offset of the source pixel in XY
	 */
	public void initSource(int sourceVal, int sourceOffsetXY) {
		this.sourceOffsetXY = sourceOffsetXY;
		this.sourceVal = sourceVal;
	}
	
	@Override
	public void notifyChangeZ(int zChange, int z) {
		bb = rbb.bufferRel(zChange);
		this.zChange = zChange;
	}
	
	protected int offset(int xChange, int yChange) {
		return extent.offset(xChange, yChange);
	}

	protected int changedOffset(int xChange, int yChange) {
		return sourceOffsetXY + extent.offset(xChange, yChange);
	}
	
	protected int getInt(int index) {
		return bb.getInt(index);
	}
	
	protected void putInt(int index, int valToAssign) {
		bb.putInt(index, valToAssign);
	}
	
	protected int getInt(int xChange, int yChange) {
		return bb.getInt(
			changedOffset(xChange,yChange)
		);
	}

	protected int getzChange() {
		return zChange;
	}

	protected int getSourceVal() {
		return sourceVal;
	}
}
