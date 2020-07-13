package org.anchoranalysis.io.bioformats.copyconvert;

import java.io.IOException;

import loci.formats.FormatException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Different ways of iterating through the different slices */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class IterateOverSlices {
	
	@FunctionalInterface
	public interface ApplyIterationToChnl {
		void apply( int t, int z, int c, int chnlIndex ) throws IOException, FormatException;
	}
	
	/**
	 * Iterates through all the frames, channels, z-slices in whatever order the reader recommends.
	 *   
	 * @param dimOrder
	 * @param shape the shape of the dimensions of the data
	 * @param numChnlsPerByteArray
	 * @param chnlIteration called for each unique z-slice from each channel and each frame
	 * @throws IOException
	 * @throws FormatException
	 */
	public static void iterateDimOrder(
		String dimOrder,
		ImageFileShape shape,
		int numByteArrays,
		ApplyIterationToChnl chnlIteration
	) throws IOException, FormatException {
		
		if (dimOrder.equalsIgnoreCase("XYCZT")) {
			applyXYCZT(shape, numByteArrays, chnlIteration);
			
		} else if (dimOrder.equalsIgnoreCase("XYZCT")) {
			applyXYZCT(shape, numByteArrays, chnlIteration);
					
		} else if (dimOrder.equalsIgnoreCase("XYZTC")) {
			applyXYZTC(shape, numByteArrays, chnlIteration);
			
		} else if (dimOrder.equalsIgnoreCase("XYCTZ")) {
			applyXYCTZ(shape, numByteArrays, chnlIteration);
			
		} else if (dimOrder.equalsIgnoreCase("XYTCZ")) {
			applyXYTCZ(shape, numByteArrays, chnlIteration);
		} else {
			throw new IOException( String.format("dimOrder '%s' not supported", dimOrder) );
		}
	}
	
	private static void applyXYCZT(ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for( int t=0; t<shape.getNumberFrames(); t++) {
			for (int z=0; z<shape.getNumberSlices(); z++) {
				for (int c=0; c<numByteArrays; c++ ) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}		
	}
	
	private static void applyXYZCT(ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for( int t=0; t<shape.getNumberFrames(); t++) {
			for (int c=0; c<numByteArrays; c++ ) {
				for (int z=0; z<shape.getNumberSlices(); z++) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}	
	}
	
	private static void applyXYZTC(ImageFileShape targetShape, int numByteArrays, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for (int c=0; c<numByteArrays; c++ ) {
			for( int t=0; t<targetShape.getNumberFrames(); t++) {
				for (int z=0; z<targetShape.getNumberSlices(); z++) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}
	}
	
	private static void applyXYCTZ(ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for (int z=0; z<shape.getNumberSlices(); z++) {
			for( int t=0; t<shape.getNumberFrames(); t++) {	
				for (int c=0; c<numByteArrays; c++ ) {
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}
	}
		
	private static void applyXYTCZ(ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration) throws IOException, FormatException {
		int chnlIndex = 0;
		for (int z=0; z<shape.getNumberSlices(); z++) {
			for (int c=0; c<numByteArrays; c++ ) {
				for( int t=0; t<shape.getNumberFrames(); t++) {	
					chnlIteration.apply(t, z, c, chnlIndex++);
				}
			}
		}			
	}
}
