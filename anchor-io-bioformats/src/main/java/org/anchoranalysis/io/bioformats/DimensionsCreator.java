package org.anchoranalysis.io.bioformats;

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

import java.util.function.Consumer;
import java.util.function.Function;

import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.Length;

public class DimensionsCreator {

	private IMetadata lociMetadata;
	
	public DimensionsCreator(IMetadata lociMetadata) {
		super();
		this.lociMetadata = lociMetadata;
	}
			
	public ImageDim apply( IFormatReader reader, ReadOptions readOptions, int seriesIndex ) {
		
		ImageDim sd = new ImageDim();
		sd.setX( reader.getSizeX() );
		sd.setY( reader.getSizeY() );
		sd.setZ( readOptions.sizeZ(reader) );
			
		assert( sd != null );
		assert( sd.getRes() != null );
		assert( lociMetadata != null );
		
		ImageRes sr = sd.getRes();
		
		metadataDim(
			metadata -> metadata.getPixelsPhysicalSizeX(seriesIndex),
			len -> sr.setX(len)
		);
		
		metadataDim(
			metadata -> metadata.getPixelsPhysicalSizeY(seriesIndex),
			len -> sr.setY(len)
		);
		
		metadataDim(
			metadata -> metadata.getPixelsPhysicalSizeZ(seriesIndex),
			len -> sr.setZ(len)
		);
		
		return sd;
	}
	
	private void metadataDim( Function<IMetadata,Length> funcDimRes, Consumer<Double> setter ) {
		Length len = funcDimRes.apply(lociMetadata);
		if (len!=null) {
			Double dbl = len.value( UNITS.METER ).doubleValue();
			setter.accept(dbl);
		}
	}
}