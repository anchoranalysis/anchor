/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public abstract class Kernel {

    private int size;
    private int sizeHalf;

    // Only use odd sizes
    public Kernel(int size) {
        super();
        this.size = size;
        this.sizeHalf = (size - 1) / 2;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSizeHalf() {
        return sizeHalf;
    }

    public int getYMin(Point3i point) {
        return Math.max(point.getY() - getSizeHalf(), 0);
    }

    public int getYMax(Point3i point, Extent extent) {
        return Math.min(point.getY() + getSizeHalf(), extent.getY() - 1);
    }

    public int getXMin(Point3i point) {
        return Math.max(point.getX() - getSizeHalf(), 0);
    }

    public int getXMax(Point3i point, Extent extent) {
        return Math.min(point.getX() + getSizeHalf(), extent.getX() - 1);
    }

    public abstract void init(VoxelBox<ByteBuffer> in);

    public abstract void notifyZChange(LocalSlices inSlices, int z);
}
