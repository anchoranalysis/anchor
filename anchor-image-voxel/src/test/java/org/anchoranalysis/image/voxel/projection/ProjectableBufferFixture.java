/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedBiFunction;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.math.arithmetic.Counter;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Creates a {@link ProjectableBuffer} and calculates the projection after adding three {@link
 * Voxels}.
 *
 * <p>It has a fixed 2x2x2 size, giving 8 voxels.
 *
 * <p>The <i>first</i> buffer added ranges (0,7)
 *
 * <p>The <i>second</i> buffer added ranges (10,17)
 *
 * <p>The <i>third</i> buffer added ranges (20,27)
 *
 * @param <T> buffer-type
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ProjectableBufferFixture<T> {

    /** The size of a small 3D buffer with 8 voxels in total. */
    public static final Extent EXTENT = new Extent(2, 2, 2);

    /** The starting value of the <b>first</b> buffer's sequence. */
    public static final int INTIIAL_VALUE_FIRST_BUFFER = 0;

    /** The starting value of the <b>third</b> buffer's sequence. */
    public static final int INTIIAL_VALUE_SECOND_BUFFER = 10;

    /** The starting value of the <b>third</b> buffer's sequence. */
    public static final int INTIIAL_VALUE_THIRD_BUFFER = 20;

    /** The data-type used to create the buffers. */
    private VoxelDataType voxelDataType;

    /**
     * Calculates the eventual projection after creating and adding the buffers, as described in the
     * class description.
     *
     * @param projectableBufferCreator creates the {@link ProjectableBuffer} to use.
     * @return the added values.
     * @throws OperationFailedException if thrown by {@code projectableBufferCreator}.
     */
    public Voxels<T> calculate(
            CheckedBiFunction<VoxelDataType, Extent, ProjectableBuffer<T>, OperationFailedException>
                    projectableBufferCreator)
            throws OperationFailedException {
        ProjectableBuffer<T> projectableBuffer =
                projectableBufferCreator.apply(voxelDataType, EXTENT);
        addBuffers(projectableBuffer);
        return projectableBuffer.completeProjection();
    }

    /** Adds the three buffers. */
    private void addBuffers(ProjectableBuffer<T> projectableBuffer) {
        projectableBuffer.addVoxels(createBufferUnsignedByte(INTIIAL_VALUE_FIRST_BUFFER));
        projectableBuffer.addVoxels(createBufferUnsignedByte(INTIIAL_VALUE_SECOND_BUFFER));
        projectableBuffer.addVoxels(createBufferUnsignedByte(INTIIAL_VALUE_THIRD_BUFFER));
    }

    /** Create a {@link Voxels} with incrementing values, starting at {@code startingValue}. */
    private Voxels<T> createBufferUnsignedByte(int startingValue) {
        @SuppressWarnings("unchecked")
        Voxels<T> voxels =
                (Voxels<T>) VoxelsFactory.instance().createEmpty(EXTENT, voxelDataType).any();
        Counter counter = new Counter(startingValue);
        IterateVoxelsAll.withVoxelBuffer(
                voxels,
                (point, buffer, offset) -> buffer.putInt(offset, counter.incrementReturn()));
        return voxels;
    }
}
