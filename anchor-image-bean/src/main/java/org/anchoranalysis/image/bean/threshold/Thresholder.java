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

import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;

// Needs SharedObjects due to Thresholder
public abstract class Thresholder extends NullParamsBean<VoxelBoxThresholder> {

	// Returns a BinaryVoxelBox<ByteBuffer> if successful, or NULL if not
	// The output buffer can be constructed from the input buffer where possible
	// Histogram is an optional param
	public abstract BinaryVoxelBox<ByteBuffer> threshold( VoxelBoxWrapper inputBuffer, BinaryValuesByte bvOut, Optional<Histogram> histogram ) throws OperationFailedException;
	
	// True if it was a successful threshold, false if we failed to find a threshold value
	public abstract BinaryVoxelBox<ByteBuffer> threshold( VoxelBoxWrapper inputBuffer, ObjMask objMask, BinaryValuesByte bvOut, Optional<Histogram> histogram ) throws OperationFailedException;
	
	public abstract int getLastThreshold();
}
