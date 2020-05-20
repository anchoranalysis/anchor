package org.anchoranalysis.image.bean.sgmn.binary;

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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.threshold.Thresholder;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.sgmn.SgmnFailedException;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class BinarySgmnThrshld extends BinarySgmn {
	
	// START PARAMETERS
	@BeanField
	private Thresholder thresholder;
	// END PARAMETERS


	
	@Override
	public BinaryVoxelBox<ByteBuffer> sgmn(VoxelBoxWrapper voxelBox, BinarySgmnParameters params, Optional<ObjMask> mask) throws SgmnFailedException {
		if (mask.isPresent()) {
			return sgmnWithMask(voxelBox, params, mask.get());
		} else {
			return sgmnWithoutMask(voxelBox, params);
		}
	}
	
	private BinaryVoxelBox<ByteBuffer> sgmnWithoutMask(VoxelBoxWrapper voxelBox, BinarySgmnParameters params) throws SgmnFailedException {
		
		BinaryValuesByte bvOut = BinaryValuesByte.getDefault();
		
		try {
			return thresholder.threshold(voxelBox, bvOut, Optional.empty());
		} catch (OperationFailedException e) {
			throw new SgmnFailedException(e);
		}
	}
	
	private BinaryVoxelBox<ByteBuffer> sgmnWithMask(VoxelBoxWrapper voxelBox, BinarySgmnParameters params, ObjMask mask) throws SgmnFailedException {
		BoundingBox bboxE = new BoundingBox(mask.getVoxelBox().extnt());
		
		// We just want to return the area under the objMask
		VoxelBox<ByteBuffer> maskDup = VoxelBoxFactory.instance().getByte().create( mask.getVoxelBox().extnt() );
		VoxelBoxWrapper maskDupWrap = new VoxelBoxWrapper(maskDup);
		
		voxelBox.copyPixelsToCheckMask(
			mask.getBoundingBox(),
			maskDupWrap,
			bboxE,
			mask.getVoxelBox(),
			mask.getBinaryValuesByte()
		);
		
		// As we are thresholding a mask
		
		BinaryValuesByte bvOut = BinaryValuesByte.getDefault();
		try {		
			return thresholder.threshold(
				voxelBox,
				new ObjMask(bboxE,mask.getVoxelBox(),mask.getBinaryValuesByte()),
				bvOut,
				params.getIntensityHistogram()
			);
		} catch (OperationFailedException e) {
			throw new SgmnFailedException(e);
		}
					
	}

	public Thresholder getThresholder() {
		return thresholder;
	}

	public void setThresholder(Thresholder thresholder) {
		this.thresholder = thresholder;
	}
}
