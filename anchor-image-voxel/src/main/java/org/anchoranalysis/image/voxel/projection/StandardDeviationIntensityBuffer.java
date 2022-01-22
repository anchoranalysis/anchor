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

package org.anchoranalysis.image.voxel.projection;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.convert.VoxelsConverterMulti;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.spatial.box.Extent;

/**
 * The buffer used when calculating the <b>standard deviation</b> of a range of values.
 *
 * <p>The formula uses calculates the stand-deviation by: {@code sqrt( mean[X^2] - (mean[X])^2 ) }.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/Standard_deviation">Wikipedia article on standard
 * deviation</a>.
 *
 * @author Owen Feehan
 * @param <T> type of buffer used, both as input and result, of the maximum intensity projection
 */
class StandardDeviationIntensityBuffer<T> implements ProjectableBuffer<T> {

    private static final VoxelsConverterMulti CONVERTER = new VoxelsConverterMulti();

    private Voxels<FloatBuffer> voxelsSum;
    private Voxels<FloatBuffer> voxelsSumSquared;
    private int count = 0;
    private final VoxelsFactoryTypeBound<T> flatType;

    /**
     * Creates with minimal parameters, as no preprocessing is necessary.
     *
     * @param flatType the voxel data-type to use for the flattened (mean-intensity) buffer.
     * @param extent the size expected for images that will be projected.
     */
    public StandardDeviationIntensityBuffer(VoxelsFactoryTypeBound<T> flatType, Extent extent) {
        this.flatType = flatType;
        this.voxelsSum = VoxelsFactory.getFloat().createInitialized(extent);
        this.voxelsSumSquared = VoxelsFactory.getFloat().createInitialized(extent);
    }

    @Override
    public void addVoxelBuffer(VoxelBuffer<T> voxelBuffer) {
        addVoxelBufferInternal(voxelBuffer, 0);
        count++;
    }

    @Override
    public void addVoxels(Voxels<T> voxels) {
        for (int z = 0; z < voxels.extent().z(); z++) {
            addVoxelBufferInternal(voxels.slice(z), z);
        }
        count++;
    }

    @Override
    public Voxels<T> completeProjection() {
        voxelsSum.arithmetic().divideBy(count);
        voxelsSumSquared.arithmetic().divideBy(count);
        squareEachVoxel(voxelsSum);
        subtractSquareRoot(voxelsSumSquared, voxelsSum);
        return CONVERTER.convert(new VoxelsUntyped(voxelsSumSquared), flatType);
    }

    /** Adds a {@link VoxelsBuffer} without incrementing the count. */
    private void addVoxelBufferInternal(VoxelBuffer<T> voxelBuffer, int z) {
        FloatBuffer sumBuffer = voxelsSum.sliceBuffer(z);
        FloatBuffer sumSquaredBuffer = voxelsSumSquared.sliceBuffer(z);
        voxelsSum
                .extent()
                .iterateOverXYOffset(
                        offset ->
                                incrementSumBuffer(
                                        offset,
                                        voxelBuffer.getInt(offset),
                                        sumBuffer,
                                        sumSquaredBuffer));
    }

    /** Increments a particular offset in the sum buffer by a certain amount */
    private void incrementSumBuffer(
            int index, int toAdd, FloatBuffer sumBuffer, FloatBuffer sumSquaredBuffer) {
        addToPosition(sumBuffer, index, toAdd);
        addToPosition(sumSquaredBuffer, index, (toAdd * toAdd));
    }

    /** Adds a value to a particular position in a {@link FloatBuffer}. */
    private static void addToPosition(FloatBuffer buffer, int index, int toAdd) {
        buffer.put(index, buffer.get(index) + toAdd);
    }

    /** Squares each value in a {@link FloatBuffer}. */
    private static void squareEachVoxel(Voxels<FloatBuffer> voxelsBuffer) {
        for (int z = 0; z < voxelsBuffer.extent().z(); z++) {
            FloatBuffer buffer = voxelsBuffer.sliceBuffer(z);
            buffer.rewind();
            while (buffer.hasRemaining()) {
                float value = buffer.get();
                buffer.position(buffer.position() - 1);
                buffer.put(value * value);
            }
        }
    }

    /**
     * Subtract the value in one buffer from the corresponding value in the other, and calculate the
     * square root.
     *
     * @param voxelsBufferBigger the buffer with the <i>larger</i> values, which are subtracted
     *     <i>from</i>. The result is written here.
     * @param voxelsBufferSmaller the buffer with the <i>smaller</i> values, which are subtracted.
     *     It is unchanged by the operation.
     */
    private static void subtractSquareRoot(
            Voxels<FloatBuffer> voxelsBufferBigger, Voxels<FloatBuffer> voxelsBufferSmaller) {
        for (int z = 0; z < voxelsBufferBigger.extent().z(); z++) {
            FloatBuffer bufferBigger = voxelsBufferBigger.sliceBuffer(z);
            FloatBuffer bufferSmaller = voxelsBufferSmaller.sliceBuffer(z);
            bufferBigger.rewind();
            bufferSmaller.rewind();
            while (bufferBigger.hasRemaining()) {
                float bigger = bufferBigger.get();
                float smaller = bufferSmaller.get();
                float valueToAssign = (float) Math.sqrt(bigger - smaller);
                bufferBigger.position(bufferBigger.position() - 1);
                bufferBigger.put(valueToAssign);
            }
        }
    }
}
