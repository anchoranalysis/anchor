package org.anchoranalysis.image.channel.factory;

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

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBoxShort;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromShortBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ChannelFactoryShort extends ChannelFactorySingleType {

	private static final VoxelDataType DATA_TYPE = VoxelDataTypeUnsignedShort.instance;
	
	@Override
	public Channel createEmptyInitialised(ImageDimensions dim) {
		assert(dim!=null);
		VoxelBoxShort vb = new VoxelBoxShort( PixelsFromShortBufferArr.createInitialised(dim.getExtnt()) );
		return create(vb, dim.getRes() );
	}

	@Override
	public Channel createEmptyUninitialised(ImageDimensions dim) {
		
		PixelsFromShortBufferArr pixels = PixelsFromShortBufferArr.createEmpty( dim.getExtnt() );
		
		VoxelBoxShort vb = new VoxelBoxShort( pixels );
		return create(vb, dim.getRes() );
	}

	@Override
	public VoxelDataType dataType() {
		return DATA_TYPE;
	}
	
	public static VoxelDataType staticDataType() {
		return DATA_TYPE;
	}
}
