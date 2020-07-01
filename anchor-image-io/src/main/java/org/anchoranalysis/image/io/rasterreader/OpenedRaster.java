package org.anchoranalysis.image.io.rasterreader;

/*
 * #%L
 * anchor-io
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


import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public abstract class OpenedRaster implements AutoCloseable {
	
	// Open when we don't have a specific-type
	public abstract TimeSequence open( int seriesIndex, ProgressReporter progressReporter ) throws RasterIOException;
	
	// Opens a time-series as a particular type. If it's not the correct type, an error is thrown
	public TimeSequence openCheckType( int seriesIndex, ProgressReporter progressReporter, VoxelDataType chnlDataType ) throws RasterIOException {
		
		TimeSequence ts = open( seriesIndex, progressReporter );
		
		if (!ts.allChnlsHaveType(chnlDataType)) {
			throw new RasterIOException( String.format("File does not have dataType %s", chnlDataType) );
		}
		
		return ts;
	}
	
	public abstract int numSeries();
	
	// Can be null if no channel names exist
	public abstract Optional<List<String>> channelNames();
	
	public abstract int numChnl() throws RasterIOException;
	
	public abstract int numFrames() throws RasterIOException;
	
	public abstract int bitDepth() throws RasterIOException;
	
	public abstract boolean isRGB() throws RasterIOException;
	
	public abstract void close() throws RasterIOException;
	
	public abstract ImageDimensions dim( int seriesIndex ) throws RasterIOException;
}
