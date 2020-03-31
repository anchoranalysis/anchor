package org.anchoranalysis.io.bioformats.bean.options;

/*-
 * #%L
 * anchor-plugin-io
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;

import loci.formats.IFormatReader;

public class ForceNumChnl extends ReadOptions {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private ReadOptions options = new Default();
		
	@BeanField @Positive
	private int numChnl;
	// END BEAN PROPERTIES

	@Override
	public int sizeC(IFormatReader reader) {
		return numChnl;
	}

	@Override
	public int sizeT(IFormatReader reader) {
		return options.sizeT(reader);
	}

	@Override
	public int sizeZ(IFormatReader reader) {
		return options.sizeZ(reader);
	}

	@Override
	public int effectiveBitsPerPixel(IFormatReader reader) {
		return options.effectiveBitsPerPixel(reader);
	}

	@Override
	public int chnlsPerByteArray(IFormatReader reader) {
		return options.chnlsPerByteArray(reader);
	}
	
	@Override
	public boolean isRGB(IFormatReader reader) {
		// Not supporte when the numer of channels is being forced
		return false;
	}

	@Override
	public List<String> determineChannelNames(IFormatReader reader) {
		return options.determineChannelNames(reader);
	}

	public ReadOptions getOptions() {
		return options;
	}

	public void setOptions(ReadOptions options) {
		this.options = options;
	}

	public int getNumChnl() {
		return numChnl;
	}

	public void setNumChnl(int numChnl) {
		this.numChnl = numChnl;
	}
}