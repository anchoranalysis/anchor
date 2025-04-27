/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.projection.extrema;

import java.nio.FloatBuffer;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Base class for different types of projections that compare and replace each voxel.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ComparisonProjection {
	
	/** Performs comparison for two values of type <b>int</b>. */
	@FunctionalInterface
	protected interface IntComparer {
		
		/**
		 * Compare a value to an existing value. 
		 * @param value the value to compare.
		 * @param existingValue the existing value.
		 * @return true if value should replace existingValue, false otherwise.
		 */
		boolean compare(int value, int existingValue);
	}
	
	/** Performs comparison of two values of type <b>long</b>. */
	@FunctionalInterface
	protected interface LongComparer {
		
		/**
		 * Compare a value to an existing value. 
		 * @param value the value to compare.
		 * @param existingValue the existing value.
		 * @return true if value should replace existingValue, false otherwise.
		 */
		boolean compare(long value, long existingValue);
	}
	
	/** Performs comparison of two values of type <b>long</b>. */
	@FunctionalInterface
	protected interface FloatComparer {
		
		/**
		 * Compare a value to an existing value. 
		 * @param value the value to compare.
		 * @param existingValue the existing value.
		 * @return true if value should replace existingValue, false otherwise.
		 */
		boolean compare(float value, float existingValue);
	}
	
	private final IntComparer intComparer;
	private final LongComparer longComparer;
	private final FloatComparer floatComparer;
	
	 /**
     * Creates a buffer for a <i>maximum-intensity projection</i> for <b>unsigned byte</b> voxels.
     *
     * @param dataType the data-type to use for creating the buffer.
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @param <T> voxel data type to be equal to {@code dataType}.
     * @return a newly created buffer that can be used for projection.
     * @throws OperationFailedException if {@code dataType} is unsupported.
     */
    @SuppressWarnings("unchecked")
    public <T> ProjectableBuffer<T> create(VoxelDataType dataType, Extent extent)
            throws OperationFailedException {
        if (dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createUnsignedByte(extent);
        } else if (dataType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createUnsignedShort(extent);
        } else if (dataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createUnsignedInt(extent);
        } else if (dataType.equals(FloatVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createFloat(extent);
        } else {
            throw new OperationFailedException(
                    "No maximum-intensity projection buffer can be created, as voxel-data-type is unsupported: "
                            + dataType.toString());
        }
    }

    /**
     * Creates a buffer for a <i>maximum-intensity projection</i> for <b>unsigned byte</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public ProjectableBuffer<UnsignedByteBuffer> createUnsignedByte(Extent extent) {
        return new UnsignedByteImplementation(
                extent, intComparer::compare);
    }

    /**
     * Creates a buffer for a <i>maximum-intensity projection</i> for <b>unsigned short</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public ProjectableBuffer<UnsignedShortBuffer> createUnsignedShort(Extent extent) {
        return new UnsignedShortImplementation(
                extent, intComparer::compare);
    }

    /**
     * Creates a buffer for a <i>maximum-intensity projection</i> for <b>unsigned int</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public ProjectableBuffer<UnsignedIntBuffer> createUnsignedInt(Extent extent) {
        return new UnsignedIntImplementation(
                extent, longComparer::compare);
    }

    /**
     * Creates a buffer for a <i>maximum-intensity projection</i> for <b>float</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public ProjectableBuffer<FloatBuffer> createFloat(Extent extent) {
        return new FloatImplementation(
                extent, floatComparer::compare);
    }
}
