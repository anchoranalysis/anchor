package org.anchoranalysis.image.stack.region.chnlconverter;

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

import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Converts a channel from one type to another
 * 
 * @author Owen Feehan
 *
 * @param <T> type to convert to (destination-type)
 */
public abstract class ChnlConverter<T extends Buffer> {

	private VoxelBoxFactoryTypeBound<T> voxelBoxFactory;
	
	private VoxelDataType dataTypeTarget;
	private VoxelBoxConverter<T> voxelBoxConverter;
		
	public ChnlConverter(VoxelDataType dataTypeTarget,
			VoxelBoxConverter<T> voxelBoxConverter, VoxelBoxFactoryTypeBound<T> voxelBoxFactory) {
		super();
		this.dataTypeTarget = dataTypeTarget;
		this.voxelBoxConverter = voxelBoxConverter;
		this.voxelBoxFactory = voxelBoxFactory;
	}

	public Stack convert(Stack stackIn, ConversionPolicy changeExisting) {
		Stack stackOut = new Stack();
		
		for( Chnl chnl : stackIn ) {
			try {
				stackOut.addChnl( convert(chnl,changeExisting) );
			} catch (IncorrectImageSizeException e) {
				// Should never happen as sizes are correct in the incoming stack
				assert false;
			}
		}
		return stackOut;
	}
	
	// If changeExisting is true, the contents of the existing channel will be changed
	// If changeExisting is false, a new channel will be created
	@SuppressWarnings("unchecked")
	public Chnl convert(Chnl chnlIn, ConversionPolicy changeExisting) {
		
		// Nothing to do
		if (chnlIn.getVoxelDataType().equals( dataTypeTarget )&&changeExisting!=ConversionPolicy.ALWAYS_NEW) {
			return chnlIn;
		}
		
		Chnl chnlOut;
		VoxelBox<T> voxelBoxOut;
		
		if (changeExisting==ConversionPolicy.CHANGE_EXISTING_CHANNEL) {
			chnlOut = chnlIn;
			// We need to create a new voxel buffer
			voxelBoxOut = voxelBoxFactory.create( chnlIn.getDimensions().getExtnt() );
		} else {
			chnlOut = ChnlFactory.instance().createEmptyUninitialised( chnlIn.getDimensions(), dataTypeTarget );
			voxelBoxOut = (VoxelBox<T>) chnlOut.getVoxelBox().match(dataTypeTarget);
		}
		
		voxelBoxConverter.convertFrom(chnlIn.getVoxelBox(), voxelBoxOut);
		
		if (changeExisting==ConversionPolicy.CHANGE_EXISTING_CHANNEL) {
			VoxelBoxWrapper wrapper = new VoxelBoxWrapper( voxelBoxOut );
			chnlOut.replaceVoxelBox( wrapper );
		}
		
		return chnlOut;
	}

	public VoxelBoxConverter<T> getVoxelBoxConverter() {
		return voxelBoxConverter;
	}

}
