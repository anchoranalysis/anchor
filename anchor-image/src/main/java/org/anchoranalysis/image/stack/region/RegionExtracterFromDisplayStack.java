package org.anchoranalysis.image.stack.region;

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
import java.nio.ShortBuffer;
import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class RegionExtracterFromDisplayStack extends RegionExtracter {
	
	// Used to convert our source buffer to bytes, not called if it's already bytes
	private List<ChnlConverterAttached<Chnl,ByteBuffer>> listChnlConverter;
	
	// Current displayStacl
	private Stack stack;
	
	public RegionExtracterFromDisplayStack(
		Stack stack,
		List<ChnlConverterAttached<Chnl,ByteBuffer>> listChnlConverter
	) {
		super();
		this.stack = stack;
		this.listChnlConverter = listChnlConverter;
	}

	@Override
	public DisplayStack extractRegionFrom( BoundingBox bbox, double zoomFactor ) throws OperationFailedException {
		
		Stack out = null;
		for( int c=0; c<stack.getNumChnl(); c++) {

			Chnl chnl = extractRegionFrom( stack.getChnl(c), bbox, zoomFactor, listChnlConverter.get(c));

			if (c==0) {
				out = new Stack( chnl );
			} else {
				try {
					out.addChnl(chnl);
				} catch (IncorrectImageSizeException e) {
					assert false;
				}
			}
			
		}
		try {
			return DisplayStack.create(out);
		} catch (CreateException e) {
			assert false;
			return null;
		}
	}
	
	
	// TODO put in some form of interpolation when zoomFactor<1
	private Chnl extractRegionFrom( Chnl extractedSlice, BoundingBox bbox, double zoomFactor, ChnlConverterAttached<Chnl,ByteBuffer> chnlConverter ) throws OperationFailedException {
		
		ScaleFactor sf = new ScaleFactor(zoomFactor);
		
		// We calculate how big our outgoing voxelbox wil be 
		ImageDim sd = extractedSlice.getDimensions().scaleXYBy(sf);
		
		Extent extntTrgt = bbox.extnt().scaleXYBy(sf);

		VoxelBox<ByteBuffer> bufferSc = VoxelBoxFactory.instance().getByte().create(extntTrgt);

		MeanInterpolator interpolator = (zoomFactor < 1) ? new MeanInterpolator(zoomFactor) : null;
		
		if (extractedSlice.getVoxelDataType().equals( VoxelDataTypeUnsignedByte.instance )) {
			VoxelBox<ByteBuffer> vb = extractedSlice.getVoxelBox().asByte(); 
			interpolateRegionFromByte( vb,bufferSc,extractedSlice.getDimensions().getExtnt(),extntTrgt,bbox,zoomFactor,interpolator );
			
			if (chnlConverter!=null) {
				chnlConverter.getVoxelBoxConverter().convertFromByte(bufferSc, bufferSc);
			}
			
		} else if (extractedSlice.getVoxelDataType().equals( VoxelDataTypeUnsignedShort.instance ) && chnlConverter!=null) {
			
			VoxelBox<ShortBuffer> vb = extractedSlice.getVoxelBox().asShort();
			
			VoxelBox<ShortBuffer> bufferIntermediate = VoxelBoxFactory.instance().getShort().create(extntTrgt);
			interpolateRegionFromShort( vb,bufferIntermediate,extractedSlice.getDimensions().getExtnt(),extntTrgt,bbox,zoomFactor,interpolator );
			
			// We now convert the ShortBuffer into bytes
			chnlConverter.getVoxelBoxConverter().convertFromShort(bufferIntermediate,bufferSc);
			
		} else {
			throw new IncorrectVoxelDataTypeException( String.format("dataType %s is unsupported without chnlConverter", extractedSlice.getVoxelDataType()) );
		}
		
		return ChnlFactory
				.instance()
				.get(VoxelDataTypeUnsignedByte.instance)
				.create(bufferSc, sd.getRes());
		
	}
	
	// extntTrgt is the target-size (where we write this region)
	// extntSrcSlice is the source-size (the single slice we've extracted from the buffer to interpolate from)
	private static void interpolateRegionFromByte( VoxelBox<ByteBuffer> vbSrc, VoxelBox<ByteBuffer> vbDest, Extent extntSrc, Extent extntTrgt, BoundingBox bbox, double zoomFactor, MeanInterpolator interpolator ) throws OperationFailedException {
		
		Point3i crnrMin = bbox.getCrnrMin();
		Point3i crnrMax = bbox.calcCrnrMax();
		for( int z=crnrMin.getZ(); z<=crnrMax.getZ(); z++ ) {
			
			ByteBuffer bbIn = vbSrc.getPixelsForPlane(z).buffer();
			ByteBuffer bbOut = vbDest.getPixelsForPlane(z-crnrMin.getZ()).buffer();
			
			// We go through every pixel in the new width, and height, and sample from the original image
			int indOut = 0;
			for( int y=0; y<extntTrgt.getY(); y++) {
				
				int yOrig = ((int) (y/zoomFactor)) + crnrMin.getY();
				for( int x=0; x<extntTrgt.getX(); x++) {
					
					int xOrig = ((int) (x/zoomFactor)) + crnrMin.getX();
					
					// We get the byte to write
					byte b = (interpolator!=null) ?
						interpolator.getInterpolatedPixelByte(xOrig, yOrig, bbIn, extntSrc)
						: bbIn.get( extntSrc.offset(xOrig, yOrig) );
					
					bbOut.put(indOut++,b); 
				}
			}
		}
	}
	
	
	// extntTrgt is the target-size (where we write this region)
	// extntSrcSlice is the source-size (the single slice we've extracted from the buffer to interpolate from)
	private static void interpolateRegionFromShort( VoxelBox<ShortBuffer> vbSrc, VoxelBox<ShortBuffer> vbDest, Extent extntSrc, Extent extntTrgt, BoundingBox bbox, double zoomFactor, MeanInterpolator interpolator ) throws OperationFailedException {
		
		Point3i crnrMin = bbox.getCrnrMin();
		Point3i crnrMax = bbox.calcCrnrMax();
		for( int z=crnrMin.getZ(); z<=crnrMax.getZ(); z++ ) {
			
			assert( vbSrc.getPixelsForPlane(z) != null );
			assert( vbDest.getPixelsForPlane(z-crnrMin.getZ()) != null );
			
			ShortBuffer bbIn = vbSrc.getPixelsForPlane(z).buffer();
			ShortBuffer bbOut = vbDest.getPixelsForPlane(z-crnrMin.getZ()).buffer();
			
			// We go through every pixel in the new width, and height, and sample from the original image
			int indOut = 0;
			for( int y=0; y<extntTrgt.getY(); y++) {
				
				int yOrig = ((int) (y/zoomFactor)) + crnrMin.getY();
				for( int x=0; x<extntTrgt.getX(); x++) {
					
					int xOrig = ((int) (x/zoomFactor)) + crnrMin.getX();
					
					// We get the byte to write
					short s = (interpolator!=null) ?
						interpolator.getInterpolatedPixelShort(xOrig, yOrig, bbIn, extntSrc)
						: bbIn.get( extntSrc.offset(xOrig, yOrig) );
					
					bbOut.put(indOut++,s); 
				}
			}
		}
	}
}
