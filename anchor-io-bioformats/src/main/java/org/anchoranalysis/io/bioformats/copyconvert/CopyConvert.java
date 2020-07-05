package org.anchoranalysis.io.bioformats.copyconvert;

/*
 * #%L
 * anchor-plugin-io
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


import java.io.IOException;
import java.util.List;

import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterIncrement;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.io.bioformats.DestChnlForIndex;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

import loci.formats.FormatException;
import loci.formats.IFormatReader;

/**
 * Copies the bytes from a IFormatReader to a list of channels, converting if necessary
 * @author Owen Feehan
 *
 */
public class CopyConvert {
	
	/**
	 * Copies all frames, channels, z-slices (in a byte-array) into a destination set of Channels
	 *  converting them if necessary along the way
	 * 
	 * @param reader the source of the copy
	 * @param dest the destination of the copy
	 * @param progressReporter
	 * @param dim
	 * @param numChnl
	 * @param numFrames
	 * @param bitsPerPixel
	 * @param numChnlsPerByteArray
	 * @throws FormatException
	 * @throws IOException
	 */
	public static void copyAllFrames(
		IFormatReader reader,
		List<Channel> dest,
		ProgressReporter progressReporter,
		ImageDimensions dim,
		int numChnl,
		int numFrames,
		ConvertTo<?> convertTo,
		ReadOptions readOptions
	) throws FormatException, IOException
	{
		int numChnlsPerByteArray = readOptions.chnlsPerByteArray(reader);
		
		int numByteArraysPerIteration = calcByteArraysPerIter(numChnl, numChnlsPerByteArray);
		
		try( ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter)) {
			
			pri.setMax(numFrames*dim.getZ()*numChnl);
			pri.open();
						
			iterateDimOrder(
				reader.getDimensionOrder(),
				numFrames,
				numByteArraysPerIteration,
				dim,
				(t, z, c, readerIndex) -> {
					
					/** Selects a destination channel for a particular relative channel */
					DestChnlForIndex destC = c_rel -> dest.get(
						destIndex(c + c_rel, t, numChnl)
					);
					
					byte[] b = reader.openBytes(readerIndex);
					
					convertTo.copyAllChnls(
						dim,
						b,
						destC,
						z,
						numChnlsPerByteArray
					);
										
					pri.update();
				}
			);
		}
	}
	
	private static int calcByteArraysPerIter(int numChnl, int numChnlsPerByteArray) throws FormatException {
		
		if( (numChnl % numChnlsPerByteArray) != 0) {
			throw new FormatException(
				String.format("numChnls(%d) mod numChnlsPerByteArray(%d) != 0", numChnl, numChnlsPerByteArray)
			);
		}
		
		return numChnl / numChnlsPerByteArray;
	}
	
	private static int destIndex(int c, int t, int numChnlsPerFrame) {
		return (t*numChnlsPerFrame) + c ;
	}
	
	@FunctionalInterface
	private interface ApplyIterationToChnl {
		void apply( int t, int z, int c, int chnlIndex ) throws IOException, FormatException;
	}
	
	/**
	 * Iterates through all the frames, channels, z-slices in whatever order the reader
	 *   recommends
	 *   
	 * @param dimOrder
	 * @param numFrames
	 * @param numChnl
	 * @param dim
	 * @param numChnlsPerByteArray
	 * @param chnlIteration called for each unique z-slice from each channel and each frame
	 * @throws IOException
	 * @throws FormatException
	 */
	private static void iterateDimOrder(
		String dimOrder,
		int numFrames,
		int numByteArrays,
		ImageDimensions dim,
		ApplyIterationToChnl chnlIteration
	) throws IOException, FormatException {
		
		if (dimOrder.equalsIgnoreCase("XYCZT")) {
			applyXYCZT(dim, numByteArrays, numFrames, chnlIteration);
			
		} else if (dimOrder.equalsIgnoreCase("XYZCT")) {
			applyXYZCT(dim, numByteArrays, numFrames, chnlIteration);
					
		} else if (dimOrder.equalsIgnoreCase("XYZTC")) {
			applyXYZTC(dim, numByteArrays, numFrames, chnlIteration);
			
		} else if (dimOrder.equalsIgnoreCase("XYCTZ")) {
			applyXYCTZ(dim, numByteArrays, numFrames, chnlIteration);
			
		} else if (dimOrder.equalsIgnoreCase("XYTCZ")) {
			applyXYTCZ(dim, numByteArrays, numFrames, chnlIteration);
		} else {
			throw new IOException( String.format("dimOrder '%s' not supported", dimOrder) );
		}
	}
	
	private static void applyXYCZT(ImageDimensions dim, int numByteArrays, int numFrames, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for( int t=0; t<numFrames; t++) {
			for (int z=0; z<dim.getZ(); z++) {
				for (int c=0; c<numByteArrays; c++ ) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}		
	}
	
	private static void applyXYZCT(ImageDimensions dim, int numByteArrays, int numFrames, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for( int t=0; t<numFrames; t++) {
			for (int c=0; c<numByteArrays; c++ ) {
				for (int z=0; z<dim.getZ(); z++) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}	
	}
	
	private static void applyXYZTC(ImageDimensions dim, int numByteArrays, int numFrames, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for (int c=0; c<numByteArrays; c++ ) {
			for( int t=0; t<numFrames; t++) {
				for (int z=0; z<dim.getZ(); z++) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}
	}
	
	private static void applyXYCTZ(ImageDimensions dim, int numByteArrays, int numFrames, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for (int z=0; z<dim.getZ(); z++) {
			for( int t=0; t<numFrames; t++) {	
				for (int c=0; c<numByteArrays; c++ ) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}
	}
		
	private static void applyXYTCZ(ImageDimensions dim, int numByteArrays, int numFrames, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for (int z=0; z<dim.getZ(); z++) {
			for (int c=0; c<numByteArrays; c++ ) {
				for( int t=0; t<numFrames; t++) {	
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}			
	}
}
