package org.anchoranalysis.image.voxel.iterator;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.nio.Buffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * Exposes a {@link ProcessVoxelOffset} as a {@link ProcessVoxelSliceBuffer} by retrieving a buffer from a voxel-box for each z-slice.
 * 
 * <p>Note that {@link} notifyChangeZ need not be be called for all slices (perhaps only a subset), but {@link process} must be called
 * for ALL voxels on a given slice</p>.
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type for slice
 */
public final class RetrieveBufferForSlice<T extends Buffer> implements ProcessVoxel {

	private final VoxelBox<T> voxelBox;
	private final ProcessVoxelSliceBuffer<T> process;
	
	private T bufferSlice;
	/** A 2D offset within the current slice */
	private int offsetWithinSlice;
	
	public RetrieveBufferForSlice(VoxelBox<T> voxelBox, ProcessVoxelSliceBuffer<T> process) {
		super();
		this.voxelBox = voxelBox;
		this.process = process;
	}
	
	@Override
	public void notifyChangeZ(int z) {
		process.notifyChangeZ(z);
		offsetWithinSlice = 0;
		this.bufferSlice = voxelBox.getPixelsForPlane(z).buffer();
	}
	
	@Override
	public void process(Point3i point) {
		process.process(
			point,
			bufferSlice,
			offsetWithinSlice++
		);
	}
}
