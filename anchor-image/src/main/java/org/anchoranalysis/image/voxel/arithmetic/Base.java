package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Base class for implementing voxel-arithmetic with a buffer of a particular type
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class Base<T extends Buffer> implements VoxelsArithmetic {

    // START REQUIRED ARGUMENTS
    /** The extent of the voxels on which arithmetic is to be performed */
    private final Extent extent;

    /** A buffer for a particular slice index (set at the initial position in the buffer) */
    private final IntFunction<T> bufferForSlice;
    // END REQUIRED ARGUMENTS

    @Override
    public void multiplyBy(double factor) {

        if (factor == 1) {
            return;
        }

        for (int z = 0; z < extent.z(); z++) {
            multiplyBuffer(bufferForSlice.apply(z), factor);
        }
    }

    @Override
    public void subtractFrom(int valueToSubtractFrom) {

        for (int z = 0; z < extent.z(); z++) {
            subtractFromBuffer(bufferForSlice.apply(z), valueToSubtractFrom);
        }
    }

    @Override
    public void addTo(ObjectMask object, int valueToBeAdded) {

        BoundingBox box = object.boundingBox();

        byte maskOnByte = object.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calculateCornerMaxExclusive();
        for (int z = box.cornerMin().z(); z < pointMax.z(); z++) {

            T pixels = bufferForSlice.apply(z);
            ByteBuffer pixelsMask = object.sliceBufferGlobal(z);

            for (int y = box.cornerMin().y(); y < pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x < pointMax.x(); x++) {

                    if (pixelsMask.get() == maskOnByte) {
                        addToBufferIndex(pixels, extent.offset(x, y), valueToBeAdded);
                    }
                }
            }
        }
    }

    @Override
    public void multiplyBy(ObjectMask object, double factor) {

        BoundingBox box = object.boundingBox();

        byte maskOnByte = object.binaryValuesByte().getOnByte();

        ReadableTuple3i pointMax = box.calculateCornerMax();
        for (int z = box.cornerMin().z(); z <= pointMax.z(); z++) {

            T pixels = bufferForSlice.apply(z);
            ByteBuffer pixelsMask = object.sliceBufferGlobal(z);

            for (int y = box.cornerMin().y(); y <= pointMax.y(); y++) {
                for (int x = box.cornerMin().x(); x <= pointMax.x(); x++) {

                    if (pixelsMask.get() == maskOnByte) {
                        int index = extent.offset(x, y);

                        multiplyByBufferIndex(pixels, index, factor);
                    }
                }
            }
        }
    }

    /**
     * Subtracts the voxel at the current position in a buffer from a constant i.e. by calling
     * {@code get()}
     *
     * <p>Note the buffer's position will be advanced by one after this call.
     *
     * @param buffer the buffer, which must have its position set to the first item
     * @param valueToSubtractFrom what to subtract from
     */
    protected abstract void subtractFromBuffer(T buffer, int valueToSubtractFrom);

    /**
     * Multiplies the voxel at the current position in a buffer i.e. by calling {@code get()}
     *
     * <p>Note the buffer's position will be advanced by one after this call.
     *
     * @param buffer the buffer, which must have its position set to the first item
     * @param factor what to multiply the voxel by
     */
    protected abstract void multiplyBuffer(T buffer, double factor);

    /**
     * Multiplies the voxel at a particular position in a buffer
     *
     * @param buffer the buffer
     * @param index the index in the position of the voxel to change
     * @param factor what to multiply the voxel by
     */
    protected abstract void multiplyByBufferIndex(T buffer, int index, double factor);

    /**
     * Adds a constant to the voxel at a particular position in a buffer
     *
     * @param buffer the buffer
     * @param index the index in the position of the voxel to change
     * @param valueToBeAdded constant to be added
     */
    protected abstract void addToBufferIndex(T buffer, int index, int valueToBeAdded);
}
