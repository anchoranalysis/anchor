package org.anchoranalysis.image.voxel.box;

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

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type
 */
class SubrangeVoxelAccess<T extends Buffer> implements PixelsForPlane<T> {

	private int zRel;
	private Extent extent;
	private BoundedVoxelBox<T> src;
	
	public SubrangeVoxelAccess(int zRel, Extent extent,	BoundedVoxelBox<T> src) {
		super();
		this.zRel = zRel;
		this.extent = extent;
		this.src = src;
	}

	@Override
	public void setPixelsForPlane(int z, VoxelBuffer<T> pixels) {
		src.getVoxelBox().setPixelsForPlane(z+zRel, pixels);
	}

	@Override
	public VoxelBuffer<T> getPixelsForPlane(int z) {
		return src.getVoxelBox().getPixelsForPlane(z+zRel);
	}

	@Override
	public Extent extent() {
		return extent;
	}
}
