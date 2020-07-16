/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer;

import java.nio.Buffer;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * @author Owen Feehan
 * @param <T> nuffer-type
 */
public abstract class VoxelBuffer<T extends Buffer> {

    public abstract VoxelDataType dataType();

    public abstract T buffer();

    public abstract VoxelBuffer<T> duplicate();

    // Gets the underlying buffer-item converted to an int
    public abstract int getInt(int index);

    public abstract void putInt(int index, int val);

    public abstract void putByte(int index, byte val);

    public abstract int size();

    public void transferFrom(int destIndex, VoxelBuffer<T> src) {
        transferFrom(destIndex, src, destIndex);
    }

    public void transferFromConvert(int destIndex, VoxelBuffer<?> src, int srcIndex) {
        int val = src.getInt(srcIndex);
        putInt(destIndex, val);
    }

    @Override
    public String toString() {
        Histogram h = HistogramFactory.create(this);
        return h.toString();
    }

    public abstract void transferFrom(int destIndex, VoxelBuffer<T> src, int srcIndex);
}
