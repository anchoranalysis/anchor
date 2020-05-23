package org.anchoranalysis.image.bean.threshold;

/*
 * #%L
 * anchor-image-bean
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
import java.util.Optional;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;

public class ThresholderGlobal extends Thresholder {

	// START BEAN PARAMETERS
	@BeanField
	private CalculateLevel calculateLevel;
	// END BEAN PARAMETERS
	
	@Override
	public BinaryVoxelBox<ByteBuffer> threshold(
		VoxelBoxWrapper inputBuffer,
		BinaryValuesByte bvOut,
		Optional<Histogram> histogram,
		Optional<ObjMask> mask
	) throws OperationFailedException {
		return thresholdForHistogram(
			histogramBuffer(inputBuffer, histogram, mask),
			inputBuffer,
			bvOut
		);
	}
	
	private BinaryVoxelBox<ByteBuffer> thresholdForHistogram(
		Histogram hist,
		VoxelBoxWrapper inputBuffer,
		BinaryValuesByte bvOut
	) throws OperationFailedException {
		// We use the histogram if there is one
		int thresholdVal = calculateLevel.calculateLevel(hist);
		assert(thresholdVal>=0);
		
		try {
			return VoxelBoxThresholder.thresholdForLevel(inputBuffer, thresholdVal, bvOut, false );
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}		
	}

	private Histogram histogramBuffer(VoxelBoxWrapper inputBuffer, Optional<Histogram> histogram, Optional<ObjMask> mask) {
		return histogram.orElseGet( ()->
			HistogramFactory.create(inputBuffer, mask)
		);
	}
	
	public CalculateLevel getCalculateLevel() {
		return calculateLevel;
	}

	public void setCalculateLevel(CalculateLevel calculateLevel) {
		this.calculateLevel = calculateLevel;
	}
}
