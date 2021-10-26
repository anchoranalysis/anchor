/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.image.voxel.extracter;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.ExtentMatchHelper;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.extracter.predicate.PredicateImplementation;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * A base class implementing common functionality for {@link VoxelsExtracter} functionality.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@AllArgsConstructor
public abstract class Base<T> implements VoxelsExtracter<T> {

    // START REQUIRED ARGUMENTS
    /** The voxels to extract from. */
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
    public Voxels<T> resizedXY(int sizeX, int sizeY, Interpolator interpolator) {

        Extent extentResized = new Extent(sizeX, sizeY, voxels.extent().z());

        Voxels<T> bufferTarget = voxels.factory().createInitialized(extentResized);

        assert (bufferTarget.slice(0).capacity() == extentResized.areaXY());

        interpolator.interpolate(
                new VoxelsWrapper(voxels), new VoxelsWrapper(bufferTarget));

        assert (bufferTarget.slice(0).capacity() == extentResized.areaXY());
        return bufferTarget;
    }

    @Override
    public final Voxels<T> projectMax() {
        return project(createMaxIntensityBuffer(voxels.extent()));
    }

    @Override
    public final Voxels<T> projectMean() {
        return project(createMeanIntensityBuffer(voxels.extent()));
    }

    @Override
    public VoxelsPredicate voxelsEqualTo(int equalToValue) {
        return new PredicateImplementation<>(
                voxels, buffer -> bufferValueEqualTo(buffer, equalToValue));
    }

    @Override
    public VoxelsPredicate voxelsGreaterThan(int threshold) {
        return new PredicateImplementation<>(
                voxels, buffer -> bufferValueGreaterThan(buffer, threshold));
    }

    @Override
    public void objectCopyTo(
            ObjectMask object, Voxels<T> voxelsDestination, BoundingBox destinationBox) {

        ExtentMatchHelper.checkExtentMatch(object.boundingBox(), destinationBox);

        ReadableTuple3i sourceStart = object.boundingBox().cornerMin();
        ReadableTuple3i sourceEnd = object.boundingBox().calculateCornerMax();

        Point3i relativePosition = destinationBox.relativePositionTo(object.boundingBox());

        BinaryValuesByte binaryValues = object.binaryValuesByte();

        for (int z = sourceStart.z(); z <= sourceEnd.z(); z++) {

            T srcArr = voxels.sliceBuffer(z);
            T destArr = voxelsDestination.sliceBuffer(z + relativePosition.z());

            UnsignedByteBuffer maskBuffer = object.sliceBufferGlobal(z);

            int srcIndex = 0;
            for (int y = sourceStart.y(); y <= sourceEnd.y(); y++) {
                for (int x = sourceStart.x(); x <= sourceEnd.x(); x++) {

                    int destIndex =
                            voxelsDestination
                                    .extent()
                                    .offset(x + relativePosition.x(), y + relativePosition.y());

                    if (maskBuffer.getRaw() == binaryValues.getOnByte()) {
                        copyBufferIndexTo(srcArr, srcIndex, destArr, destIndex);
                    }
                    srcIndex++;
                }
            }
        }
    }

    protected abstract ProjectableBuffer<T> createMaxIntensityBuffer(Extent extent);

    protected abstract ProjectableBuffer<T> createMeanIntensityBuffer(Extent extent);

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
     * <p>(i.e. by calling {@code get()} on the buffer).
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

    private Voxels<T> project(ProjectableBuffer<T> projection) {
        voxels.extent().iterateOverZ(z -> projection.addSlice(voxels.slice(z)));
        return projection.completeProjection();
    }
}
