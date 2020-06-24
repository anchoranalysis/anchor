package org.anchoranalysis.image.stack.region.chnlconverter.attached;

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
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;

/**
 * Simply passes everything onto a ChnlConverter
 * 
 * @author Owen Feehan
 *
 * @param <S> attachment-type
 * @param <T> destination-type
 */
public class ChnlConverterAttachedSimple<S,T extends Buffer> extends ChnlConverterAttached<S,T> {

	private ChannelConverter<T> delegate;
		
	public ChnlConverterAttachedSimple(ChannelConverter<T> delegate) {
		super();
		this.delegate = delegate;
	}
	
	@Override
	public Channel convert(Channel chnl, ConversionPolicy changeExisting) {
		return delegate.convert(chnl, changeExisting);
	}

	@Override
	public void attachObject(S obj) {
		// Nothing happens
	}

	@Override
	public VoxelBoxConverter<T> getVoxelBoxConverter() {
		return delegate.getVoxelBoxConverter();
	}

}
