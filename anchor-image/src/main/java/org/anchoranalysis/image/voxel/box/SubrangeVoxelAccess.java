package org.anchoranalysis.image.voxel.box;

import java.nio.Buffer;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

class SubrangeVoxelAccess<BufferType extends Buffer> implements IPixelsForPlane<BufferType> {

	private int zRel;
	private Extent extent;
	private BoundedVoxelBox<BufferType> src;
	
	public SubrangeVoxelAccess(int zRel, Extent extent,	BoundedVoxelBox<BufferType> src) {
		super();
		this.zRel = zRel;
		this.extent = extent;
		this.src = src;
	}

	@Override
	public void setPixelsForPlane(int z, VoxelBuffer<BufferType> pixels) {
		src.getVoxelBox().setPixelsForPlane(z+zRel, pixels);
	}

	@Override
	public VoxelBuffer<BufferType> getPixelsForPlane(int z) {
		return src.getVoxelBox().getPixelsForPlane(z+zRel);
	}

	@Override
	public Extent extent() {
		return extent;
	}
}