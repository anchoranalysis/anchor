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

import java.util.ArrayList;
import java.util.List;

import loci.formats.IFormatReader;

public class Default extends ReadOptions {


	
	
	// START BEAN FIELDS
	// END BEAN FIELDS
	
	@Override
	public int sizeT(IFormatReader reader) {
		return reader.getSizeT();
	}
	
	@Override
	public int sizeZ(IFormatReader reader) {
		return reader.getSizeZ();
	}
	
	@Override
	public int sizeC(IFormatReader reader) {
		return reader.getSizeC();
	}
	
	@Override
	public boolean isRGB(IFormatReader reader) {
		return reader.isRGB();
	}
	
	@Override
	public int effectiveBitsPerPixel(IFormatReader reader) {
		Object bitDepth = reader.getMetadataValue("Acquisition Bit Depth");
		if (bitDepth!=null) {
			return Integer.valueOf( (String) bitDepth);
		} else {
			return reader.getBitsPerPixel();
		}
	}
	
	@Override
	public int chnlsPerByteArray(IFormatReader reader) {
		return reader.getRGBChannelCount();
	}

	@Override
	public List<String> determineChannelNames(IFormatReader reader) {
		
		String formatName = reader.getFormat();
		if (formatName.equals("Zeiss CZI")) {
			List<String> names = determineChannelNamesWithPrefix(reader, "Metadata DisplaySetting Channels Channel ShortName ");
			
			// We try again
			if (names==null) {
				names = determineChannelNamesWithPrefix(reader, "Metadata Experiment ExperimentBlocks AcquisitionBlock MultiTrackSetup Track Channels Channel FluorescenceDye ShortName ");
			}
			
			return names;
		} else if (formatName.equals("Zeiss Vision Image (ZVI)")) {
			return determineChannelNamesWithPrefix(reader, "Channel Name ");
		}
		return null;
	}
	

	private static List<String> determineChannelNamesWithPrefix( IFormatReader reader, String prefixString ) {
		
		int numChnl = reader.getSizeC();
		
		ArrayList<String> names = new ArrayList<>();
		for( int i=0; i<numChnl; i++ ) {
			Object o = reader.getMetadataValue(prefixString + i);
			if (o==null) {
				return null;
			}
			if (!(o instanceof String)) {
				return null;
			}
			names.add( (String) o );
		}
		return names;
	}

}
