/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator.changed;

import java.nio.ByteBuffer;

/**
 * Wraps a {@link ProcessVoxelNeighborAbsolute} as a {@link ProcessChangedPointAbsoluteMasked}
 *
 * @param <T> result-type that can be collected after processing
 */
public final class WrapAbsoluteAsMasked<T> implements ProcessChangedPointAbsoluteMasked<T> {

    private final ProcessVoxelNeighborAbsolute<T> delegate;

    public WrapAbsoluteAsMasked(ProcessVoxelNeighborAbsolute<T> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public void initSource(int sourceVal, int sourceOffsetXY) {
        delegate.initSource(sourceVal, sourceOffsetXY);
    }

    @Override
    public void notifyChangeZ(int zChange, int z, ByteBuffer objectMaskBuffer) {
        delegate.notifyChangeZ(zChange, z);
    }

    @Override
    public boolean processPoint(int xChange, int yChange, int x1, int y1, int objectMaskOffset) {
        return delegate.processPoint(xChange, yChange, x1, y1);
    }

    @Override
    public T collectResult() {
        return delegate.collectResult();
    }
}
