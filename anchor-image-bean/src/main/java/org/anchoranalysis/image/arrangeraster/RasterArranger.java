package org.anchoranalysis.image.arrangeraster;

/*
 * #%L
 * anchor-image-io
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


import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

@SuppressWarnings("unused")
public class RasterArranger {
	
	private BBoxSetOnPlane bboxSetOnPlane;
	private ImageDim dim;
	
	public void init( IArrangeRaster arrangeRaster, List<RGBStack> list ) throws InitException {

		Iterator<RGBStack> rasterIterator = list.iterator();
		try {
			this.bboxSetOnPlane = arrangeRaster.createBBoxSetOnPlane( rasterIterator );
		} catch (ArrangeRasterException e) {
			throw new InitException(e);
		}
		
		if (rasterIterator.hasNext()) {
			throw new InitException("rasterIterator has more items than can be accomodated");
		}

		dim = new ImageDim(
			bboxSetOnPlane.getExtnt()
		);
	}
	
	public RGBStack createStack( List<RGBStack> list, ChnlFactorySingleType chnlfactory ) {
		RGBStack stackOut =  new RGBStack( dim, chnlfactory );
		ppltStack( list, stackOut.asStack() );
		return stackOut;
	}
	
	private void ppltStack( List<RGBStack> generatedImages, Stack stackOut ) {
		
		int index = 0;
		for (RGBStack img : generatedImages) {

			BoundingBox bbox = this.bboxSetOnPlane.get(index++);
			
			// NOTE
			// For a special case where our projection z-extent is different to our actual z-extent, that means
			//   we should repeat
			if (bbox.extnt().getZ()!=img.getDimensions().getZ()) {
				
				int zShift = 0;
				do {
					projectImgOntoStackOut( bbox, img.asStack(), stackOut, zShift );
					zShift += img.getDimensions().getZ();
				} while( zShift < stackOut.getDimensions().getZ() );
				
			} else {
				projectImgOntoStackOut( bbox, img.asStack(), stackOut, 0 );
			}
			

		}
	}
	
	private void projectImgOntoStackOut( BoundingBox bbox, Stack stackIn, Stack stackOut, int zShift ) {
		
		assert(stackIn.getNumChnl()==stackOut.getNumChnl());
		
		Extent extnt = stackIn.getDimensions().getExtnt();
		Extent extntOut = stackIn.getDimensions().getExtnt();
		
		Point3i leftCrnr = bbox.getCrnrMin();
		int xEnd = leftCrnr.getX() + bbox.extnt().getX() - 1;
		int yEnd = leftCrnr.getY() + bbox.extnt().getY() - 1;
		
		int numC = stackIn.getNumChnl();
		VoxelBuffer<?>[] vbIn = new VoxelBuffer<?>[numC]; 
		VoxelBuffer<?>[] vbOut = new VoxelBuffer<?>[numC];
		
		for (int z=0; z<extnt.getZ(); z++) {
			
			int outZ = leftCrnr.getZ() + z + zShift;
			
			if (outZ>=extntOut.getZ()) {
				return;
			}

			for( int c=0; c<numC; c++) {
				vbIn[c] = stackIn.getChnl(c).getVoxelBox().any().getPixelsForPlane(z);
				vbOut[c] = stackOut.getChnl(c).getVoxelBox().any().getPixelsForPlane(outZ);
			}
		
			int src = 0;
			for (int y=leftCrnr.getY(); y<=yEnd; y++) {
				for (int x=leftCrnr.getX(); x<=xEnd; x++) {
					
					int outPos = stackOut.getDimensions().offset(x, y);

					for( int c=0; c<numC; c++) {
						vbOut[c].transferFromConvert(outPos, vbIn[c], src);
					}
					src++;
				}
			}
		}		
	}
}