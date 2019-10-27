package org.anchoranalysis.image.interpolator;

import org.anchoranalysis.image.extent.Extent;

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


import org.anchoranalysis.image.interpolator.transfer.Transfer;
import org.anchoranalysis.image.interpolator.transfer.TransferViaByte;
import org.anchoranalysis.image.interpolator.transfer.TransferViaShort;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeShort;

public class InterpolateUtilities {
	
	
	
	private static Transfer createTransfer( VoxelBoxWrapper src, VoxelBoxWrapper dest ) {
		
		if (!src.getVoxelDataType().equals(dest.getVoxelDataType())) {
			throw new IncorrectVoxelDataTypeException("Data types don't match between src and dest");
		}
		
		if (src.getVoxelDataType().equals( VoxelDataTypeByte.instance )) {
			return new TransferViaByte(src, dest);
		} else if (src.getVoxelDataType().equals( VoxelDataTypeShort.instance )) {
			return new TransferViaShort(src, dest);
		} else {
			throw new IncorrectVoxelDataTypeException("Only unsigned byte and short are supported");
		}
	}
	
	
	public static void transferSlicesResizeXY(VoxelBoxWrapper src, VoxelBoxWrapper trgt, Interpolator interpolator ) {
		
		Extent eSrc = src.any().extnt();
		Extent eTrgt = trgt.any().extnt();
		
		Transfer biWrapper = createTransfer(src,trgt);

		//resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		
		for (int z=0; z<eSrc.getZ(); z++) {
			
			biWrapper.assignSlice(z); 
			if (eSrc.getX()==eTrgt.getX() && eSrc.getY()==eTrgt.getY()) {
				biWrapper.transferCopyTo(z);
			} else {
				if (eSrc.getX()!=1 && eSrc.getY()!=1) {
					// We only bother to interpolate when we have more than a single pixel in both directions
					// And in this case, some of the interpolation algorithms would crash. 
					biWrapper.transferTo(z, interpolator);
				} else {
					biWrapper.transferTo(z, InterpolatorFactory.getInstance().noInterpolation() );
				}
			}
		}
		assert( trgt.any().getPixelsForPlane(0).buffer().capacity() == eTrgt.getVolumeXY() );
	}
}
