package org.anchoranalysis.image.voxel.iterator.changed;

import java.nio.ByteBuffer;

/** Wraps a {@link ProcessVoxelNeighbourAbsolute} as a {@link ProcessChangedPointAbsoluteMasked} */
public final class WrapAbsoluteAsMasked implements ProcessChangedPointAbsoluteMasked {

	private final ProcessVoxelNeighbourAbsolute delegate;
	
	public WrapAbsoluteAsMasked(ProcessVoxelNeighbourAbsolute delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public boolean processPoint(int xChange, int yChange, int x1, int y1, int objectMaskOffset) {
		return delegate.processPoint(xChange, yChange, x1, y1);
	}

	@Override
	public void notifyChangeZ(int zChange, int z, ByteBuffer objectMaskBuffer) {
		delegate.notifyChangeZ(zChange, z);
	}
}
