package org.anchoranalysis.image.channel.factory;

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


import java.nio.Buffer;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.factory.VoxelDataTypeFactoryMultiplexer;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Creates a channel for one of several data-types
 * 
 */
public class ChannelFactory extends VoxelDataTypeFactoryMultiplexer<ChannelFactorySingleType> {
	
	// Singleton
	private static ChannelFactory instance;
	
	private ChannelFactory() {
		super(
			new ChannelFactoryByte(),
			new ChannelFactoryShort(),
			new ChannelFactoryInt(),
			new ChannelFactoryFloat()
		);
	}
	
	/** Singleton */
	public static ChannelFactory instance() {
		if (instance==null) {
			instance = new ChannelFactory();
		}
		return instance;
	}
	
	public Channel createEmptyInitialised(ImageDimensions dim, VoxelDataType chnlDataType ) {
		ChannelFactorySingleType factory = get(chnlDataType);
		return factory.createEmptyInitialised(dim);
	}

	public Channel createEmptyUninitialised(ImageDimensions dim, VoxelDataType chnlDataType) {
		ChannelFactorySingleType factory = get(chnlDataType);
		return factory.createEmptyUninitialised(dim);
	}

	public Channel create(VoxelBox<? extends Buffer> voxelBox, ImageResolution res ) {
		
		VoxelDataType chnlDataType = voxelBox.dataType(); 
		
		ChannelFactorySingleType factory = get(chnlDataType);
		return factory.create(voxelBox,res);
		
	}
}
