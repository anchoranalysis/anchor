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

import org.anchoranalysis.bean.AnchorBean;

import loci.formats.IFormatReader;

public abstract class ReadOptions extends AnchorBean<ReadOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract int sizeC(IFormatReader reader);
	
	public abstract int sizeT(IFormatReader reader);
	
	public abstract int sizeZ(IFormatReader reader);
		
	public abstract int effectiveBitsPerPixel(IFormatReader reader);
	
	public abstract int chnlsPerByteArray(IFormatReader reader);
	
	/** Is it an image with three channels (red, green and blue)? */
	public abstract boolean isRGB(IFormatReader reader);
	
	/** Returns a list of channel-names or NULL if they are unavailable */
	public abstract List<String> determineChannelNames( IFormatReader reader );
}
