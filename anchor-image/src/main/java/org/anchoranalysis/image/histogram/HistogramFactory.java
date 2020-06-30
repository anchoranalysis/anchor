package org.anchoranalysis.image.histogram;

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
import java.util.Collection;
import java.util.Optional;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class HistogramFactory {

	public static Histogram createGuessMaxBin( Collection<Histogram> histograms) throws CreateException {
		
		if (histograms.size()==0) {
			throw new CreateException("Cannot determine a maxBinVal as the collection is empty");
		}
		
		Histogram h = histograms.iterator().next();
		
		int maxBinVal = h.getMaxBin();
		return create( histograms, maxBinVal );
	}
	
	// Creates histograms from a collection of existing histograms
	// Assumes histograms all have the same max Bin
	public static Histogram create( Collection<Histogram> histograms, int maxBinVal ) throws CreateException {
		
		if (histograms.size()==0) {
			return new HistogramArray(maxBinVal);
		}
		
		Histogram out = new HistogramArray(maxBinVal);
		for( Histogram h : histograms ) {
			try {
				out.addHistogram(h);
			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
		}
		return out;
	}
	
	public static Histogram create( Channel chnl ) throws CreateException {
		
		try {
			return create( chnl.getVoxelBox() );
		} catch (IncorrectVoxelDataTypeException e) {
			throw new CreateException("Cannot create histogram from ImgChnl",e);
		}
	}
	
	
	public static Histogram create( Channel chnl, BinaryChnl mask ) throws CreateException {
		
		if (!chnl.getDimensions().getExtnt().equals(mask.getDimensions().getExtnt())) {
			throw new CreateException("Size of chnl and mask do not match");
		}
		
		Histogram total = new HistogramArray( (int) chnl.getVoxelDataType().maxValue() );
		
		VoxelBox<?> vb = chnl.getVoxelBox().any();
		
		ObjectMask om = new ObjectMask( mask.binaryVoxelBox() );
		
		Histogram h = createWithMask(vb, om);
		try {
			total.addHistogram(h);
		} catch (OperationFailedException e) {
			assert false;
		}
		
		return total;
	}

	
	public static Histogram create( Channel chnl, ObjectMask obj ) {
		return create(
			chnl,
			ObjectCollectionFactory.from(obj)
		);
	}
	
	public static Histogram create( Channel chnl, ObjectCollection objs ) {
		return createWithMasks( chnl.getVoxelBox(), objs );
	}
	
	public static Histogram create( VoxelBuffer<?> inputBuffer ) {
		
		Histogram hist = new HistogramArray( (int) inputBuffer.dataType().maxValue() );
		addBufferToHistogram( hist, inputBuffer, inputBuffer.size() );
		return hist;
	}
	
	public static Histogram create( VoxelBoxWrapper inputBuffer) {
		return create(inputBuffer, Optional.empty());
	}
	
	public static Histogram create( VoxelBoxWrapper inputBuffer, Optional<ObjectMask> mask ) {
		
		if (!isDataTypeSupported(inputBuffer.getVoxelDataType())) {
			throw new IncorrectVoxelDataTypeException( String.format("Data type %s is not supported", inputBuffer.getVoxelDataType()) );
		}
		
		if (mask.isPresent()) {
			return createWithMask(inputBuffer.any(), mask.get());
		} else {
			return create(inputBuffer.any());
		}
	}
	
	private static boolean isDataTypeSupported(VoxelDataType dataType) {
		return dataType.equals(VoxelDataTypeUnsignedByte.instance) || dataType.equals(VoxelDataTypeUnsignedShort.instance); 
	}
	
	private static Histogram createWithMask( VoxelBox<?> inputBuffer, ObjectMask objMask ) {
		
		Histogram hist = new HistogramArray( (int) inputBuffer.dataType().maxValue() );
		
		Extent e = inputBuffer.extent();
		Extent eMask = objMask.getBoundingBox().extent();
		
		ReadableTuple3i crnrMin = objMask.getBoundingBox().getCornerMin();
		ReadableTuple3i crnrMax = objMask.getBoundingBox().calcCornerMax();
		
		byte maskOnVal = objMask.getBinaryValuesByte().getOnByte();
		
		for (int z=crnrMin.getZ(); z<=crnrMax.getZ(); z++) {
			
			VoxelBuffer<?> bb = inputBuffer.getPixelsForPlane(z);
			ByteBuffer bbMask = objMask.getVoxelBox().getPixelsForPlane(z-crnrMin.getZ()).buffer();
			
			for (int y=crnrMin.getY(); y<=crnrMax.getY(); y++) {
				for (int x=crnrMin.getX(); x<=crnrMax.getX(); x++) {
					
					int offset = e.offset(x, y);
					int offsetMask = eMask.offset(x-crnrMin.getX(), y-crnrMin.getY());
					
					byte maskVal = bbMask.get(offsetMask);
					
					if (maskVal==maskOnVal) {
						int val = bb.getInt(offset);
						hist.incrVal(val);
					}
				}
			}
		}
		return hist;
	}
	
	
	private static Histogram createWithMasks( VoxelBoxWrapper vb, ObjectCollection objs ) {
		
		Histogram total = new HistogramArray( (int) vb.getVoxelDataType().maxValue() );
		
		for( ObjectMask om : objs ) {
			Histogram h = createWithMask( vb.any(), om);
			try {
				total.addHistogram(h);
			} catch (OperationFailedException e) {
				assert false;
			}
		}
		return total;
	}
	
	private static Histogram create( VoxelBox<?> inputBox ) {
		
		Histogram hist = new HistogramArray( (int) inputBox.dataType().maxValue() );
		
		Extent e = inputBox.extent();
		int volumeXY = e.getVolumeXY();
		for (int z=0; z<e.getZ(); z++) {
			addBufferToHistogram( hist, inputBox.getPixelsForPlane(z), volumeXY);
		}
		return hist;
	}
	
	private static void addBufferToHistogram( Histogram hist, VoxelBuffer<?> bb, int maxOffset ) {
		for (int offset=0; offset<maxOffset; offset++) {
			int val = bb.getInt(offset);
			hist.incrVal(val);
		}
	}
	
	
	public static Histogram createHistogramIgnoreZero( Channel chnl, ObjectMask objMask, boolean ignoreZero ) {
		Histogram hist = create(chnl, objMask);
		if (ignoreZero) {
			hist.zeroVal(0);
		}
		return hist;
	}
	
}
