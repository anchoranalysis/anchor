/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer;

import java.nio.Buffer;
import lombok.Getter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * Contains the {@link ByteBuffer} for the current slice, the current slice minus 1, and the current
 * slice plus 1
 *
 * <p>Can then be shifted (incremented) across all z-slices
 *
 * @param <T> buffer-type
 */
public final class SlidingBuffer<T extends Buffer> {

    @Getter private final VoxelBox<T> voxelBox;

    @Getter private VoxelBuffer<T> center;

    @Getter private VoxelBuffer<T> plusOne;

    @Getter private VoxelBuffer<T> minusOne;

    private int sliceNumber = -1;

    public SlidingBuffer(VoxelBox<T> voxelBox) {
        super();
        this.voxelBox = voxelBox;
        seek(0); // We start off on slice 0 always
    }

    /** Seeks a particular slice */
    public void seek(int sliceIndexToSeek) {

        if (sliceIndexToSeek == sliceNumber) {
            return;
        }

        sliceNumber = sliceIndexToSeek;
        minusOne = null;
        center = voxelBox.getPixelsForPlane(sliceNumber);

        if ((sliceNumber - 1) >= 0) {
            minusOne = voxelBox.getPixelsForPlane(sliceNumber - 1);
        }

        if ((sliceNumber + 1) < voxelBox.extent().getZ()) {
            plusOne = voxelBox.getPixelsForPlane(sliceNumber + 1);
        }
    }

    /** Increments the slice number by one */
    public void shift() {
        minusOne = center;
        center = plusOne;

        sliceNumber++;

        if ((sliceNumber + 1) < voxelBox.extent().getZ()) {
            plusOne = voxelBox.getPixelsForPlane(sliceNumber + 1);
        } else {
            plusOne = null;
        }
    }

    public VoxelBuffer<T> bufferRel(int rel) {
        switch (rel) {
            case 1:
                return plusOne;
            case 0:
                return center;
            case -1:
                return minusOne;
            default:
                return voxelBox.getPixelsForPlane(sliceNumber + rel);
        }
    }

    public Extent extent() {
        return voxelBox.extent();
    }
}
