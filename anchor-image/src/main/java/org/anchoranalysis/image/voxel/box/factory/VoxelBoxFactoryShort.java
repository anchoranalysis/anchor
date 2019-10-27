package org.anchoranalysis.image.voxel.box.factory;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.ShortBuffer;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxShort;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromShortBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeShort;

class VoxelBoxFactoryShort extends VoxelBoxFactoryTypeBound<ShortBuffer> {

	private static VoxelDataType dataType = VoxelDataTypeShort.instance;
	
	public VoxelBoxFactoryShort() {
		// Prevent creation outside this package
	}
	
	@Override
	public VoxelBox<ShortBuffer> create(
			IPixelsForPlane<ShortBuffer> pixelsForPlane) {
		return new VoxelBoxShort(pixelsForPlane);
	}

	@Override
	public VoxelBox<ShortBuffer> create(Extent e) {
		return new VoxelBoxShort( PixelsFromShortBufferArr.createInitialised(e) );
	}

	@Override
	public VoxelDataType dataType() {
		return dataType;
	}

}
