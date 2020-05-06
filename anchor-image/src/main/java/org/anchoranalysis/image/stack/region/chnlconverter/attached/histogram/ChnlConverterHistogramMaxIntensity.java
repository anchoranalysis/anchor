package org.anchoranalysis.image.stack.region.chnlconverter.attached.histogram;

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


import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.stack.region.chnlconverter.ChnlConverterToUnsignedByte;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToByteScaleByMaxValue;

public class ChnlConverterHistogramMaxIntensity extends ChnlConverterAttached<Histogram, ByteBuffer>{

	private VoxelBoxConverterToByteScaleByMaxValue voxelBoxConverter;
	
	private ChnlConverterToUnsignedByte delegate;
	
	public ChnlConverterHistogramMaxIntensity() {
		// Initialise with a dummy value;
		voxelBoxConverter = new	VoxelBoxConverterToByteScaleByMaxValue(1);
		
		delegate = new ChnlConverterToUnsignedByte(voxelBoxConverter);
	}
	
	@Override
	public void attachObject(Histogram hist) throws OperationFailedException {
		
		int maxValue = hist.calcMax();
		voxelBoxConverter.setMaxValue(maxValue);
	}

	@Override
	public Chnl convert(Chnl chnl, ConversionPolicy changeExisting) {
		return delegate.convert(chnl, changeExisting);
	}

	@Override
	public VoxelBoxConverter<ByteBuffer> getVoxelBoxConverter() {
		return voxelBoxConverter;
	}


}
