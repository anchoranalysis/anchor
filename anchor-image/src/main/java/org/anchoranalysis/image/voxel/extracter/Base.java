package org.anchoranalysis.image.voxel.extracter;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.interpolator.InterpolateUtilities;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.ExtentMatchHelper;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsPredicate;
import org.anchoranalysis.image.voxel.VoxelsWrapper;

@AllArgsConstructor
abstract class Base<T extends Buffer> implements VoxelsExtracter<T> {

    // START REQUIRED ARGUMENTS
    /** The voxels to extract from */
    protected final Voxels<T> voxels;
    // END REQUIRED ARGUMENTS

    @Override
    public int voxel(ReadableTuple3i point) {
        T buffer = voxels.slices().slice(point.z()).buffer();
        int offset = voxels.slices().extent().offsetSlice(point);
        return voxelAtBufferIndex(buffer, offset);
    }

    @Override
    public Voxels<T> region(BoundingBox box, boolean reuseIfPossible) {
        if (reuseIfPossible) {
            return regionAvoidNewIfPossible(box);
        } else {
            return regionAlwaysNew(box);
        }
    }

    @Override
    public Voxels<T> slice(int sliceIndex) {
        Voxels<T> bufferAccess =
                voxels.factory().createInitialized(voxels.extent().duplicateChangeZ(1));
        bufferAccess.slices().replaceSlice(0, voxels.slices().slice(sliceIndex));
        return bufferAccess;
    }

    @Override
    public void boxCopyTo(
            BoundingBox sourceBox, Voxels<T> voxelsDestination, BoundingBox destinationBox) {

        ExtentMatchHelper.checkExtentMatch(sourceBox, destinationBox);

        ReadableTuple3i sourceStart = sourceBox.cornerMin();
        ReadableTuple3i sourceEnd = sourceBox.calculateCornerMax();

        Point3i relativePosition = destinationBox.relativePositionTo(sourceBox);

        Extent extent = voxels.extent();

        for (int z = sourceStart.z(); z <= sourceEnd.z(); z++) {

            T srcArr = voxels.sliceBuffer(z);
            T destArr = voxelsDestination.sliceBuffer(z + relativePosition.z());

            for (int y = sourceStart.y(); y <= sourceEnd.y(); y++) {
                for (int x = sourceStart.x(); x <= sourceEnd.x(); x++) {

                    int srcIndex = extent.offset(x, y);
                    int destIndex =
                            voxelsDestination
                                    .extent()
                                    .offset(x + relativePosition.x(), y + relativePosition.y());

                    copyBufferIndexTo(srcArr, srcIndex, destArr, destIndex);
                }
            }
        }
    }

    @Override
    public void objectCopyTo(
            ObjectMask object, Voxels<T> voxelsDestination, BoundingBox destinationBox) {

        ExtentMatchHelper.checkExtentMatch(object.boundingBox(), destinationBox);

        ReadableTuple3i sourceStart = object.boundingBox().cornerMin();
        ReadableTuple3i sourceEnd = object.boundingBox().calculateCornerMax();

        Point3i relativePosition = destinationBox.relativePositionTo(object.boundingBox());

        BinaryValuesByte bvb = object.binaryValuesByte();

        Extent extent = voxels.extent();

        for (int z = sourceStart.z(); z <= sourceEnd.z(); z++) {

            T srcArr = voxels.sliceBuffer(z);
            T destArr = voxelsDestination.sliceBuffer(z + relativePosition.z());

            ByteBuffer maskBuffer = object.sliceBufferGlobal(z);

            for (int y = sourceStart.y(); y <= sourceEnd.y(); y++) {
                for (int x = sourceStart.x(); x <= sourceEnd.x(); x++) {

                    int srcIndex = extent.offset(x, y);
                    int destIndex =
                            voxelsDestination
                                    .extent()
                                    .offset(x + relativePosition.x(), y + relativePosition.y());

                    if (maskBuffer.get() == bvb.getOnByte()) {
                        copyBufferIndexTo(srcArr, srcIndex, destArr, destIndex);
                    }
                }
            }
        }
    }

    @Override
    public Voxels<T> resizedXY(int sizeX, int sizeY, Interpolator interpolator) {

        Extent extentResized = new Extent(sizeX, sizeY, voxels.extent().z());

        Voxels<T> bufferTarget = voxels.factory().createInitialized(extentResized);

        assert (bufferTarget.sliceBuffer(0).capacity() == extentResized.volumeXY());

        InterpolateUtilities.transferSlicesResizeXY(
                new VoxelsWrapper(voxels), new VoxelsWrapper(bufferTarget), interpolator);

        assert (bufferTarget.sliceBuffer(0).capacity() == extentResized.volumeXY());
        return bufferTarget;
    }

    @Override
    public VoxelsPredicate voxelsEqualTo(int equalToValue) {
        return new PredicateImplementation<>(
                voxels.extent(),
                voxels::sliceBuffer,
                buffer -> bufferValueEqualTo(buffer, equalToValue));
    }

    @Override
    public VoxelsPredicate voxelsGreaterThan(int threshold) {
        return new PredicateImplementation<>(
                voxels.extent(),
                voxels::sliceBuffer,
                buffer -> bufferValueGreaterThan(buffer, threshold));
    }

    protected abstract void copyBufferIndexTo(
            T sourceBuffer, int sourceIndex, T destinationBuffer, int destinationIndex);

    protected abstract int voxelAtBufferIndex(T buffer, int index);

    /**
     * Checks if the current value from a buffer is <i>greater than</i> a constant value
     *
     * <p>(i.e. by calling {@code get()} on the buffer)
     *
     * @param buffer provides the value to compare
     * @param threshold the constant threshold-value
     * @return true iff the current value from the buffer is greater than the threshold
     */
    protected abstract boolean bufferValueGreaterThan(T buffer, int threshold);

    /**
     * Checks if the current value from a buffer is <i>equal to</i> a constant value
     *
     * <p>(i.e. by calling {@code get()} on the buffer)
     *
     * @param buffer provides the value to compare
     * @param value the constant-value
     * @return true iff the current value from the buffer is equal to the constant
     */
    protected abstract boolean bufferValueEqualTo(T buffer, int value);

    private Voxels<T> regionAvoidNewIfPossible(BoundingBox box) {

        if (box.equals(new BoundingBox(voxels.extent()))
                && box.cornerMin().x() == 0
                && box.cornerMin().y() == 0
                && box.cornerMin().z() == 0) {
            return voxels;
        }
        return regionAlwaysNew(box);
    }

    private Voxels<T> regionAlwaysNew(BoundingBox box) {

        // Otherwise we create a new buffer
        Voxels<T> voxelsOut = voxels.factory().createInitialized(box.extent());
        boxCopyTo(box, voxelsOut, box.shiftToOrigin());
        return voxelsOut;
    }
}
