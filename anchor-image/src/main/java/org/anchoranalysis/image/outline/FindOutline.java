package org.anchoranalysis.image.outline;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.logical.BinaryChnlXor;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.dilateerode.ErosionKernel3;
import org.anchoranalysis.image.voxel.kernel.outline.OutlineKernel3;


/**
 * Finds outline voxels i.e. pixels on the contour/edge of the object
 * 
 * Specifically, it converts a solid-object (where all voxels inside an object are ON) into where only pixels on the contour are ON
 * 
 * A new object/voxel-box is always created, so the existing buffers are not overwritten
 */
public class FindOutline {
	
	private FindOutline() {
		
	}
	
	public static BinaryChnl outline( BinaryChnl chnl, boolean do3D, boolean erodeEdges ) throws CreateException {
		// We create a new image for output
		Chnl chnlOut = ChnlFactory.instance().createEmptyInitialised( chnl.getChnl().getDimensions(), VoxelDataTypeUnsignedByte.instance );
		BinaryChnl chnlOutBinary = new BinaryChnl(chnlOut, chnl.getBinaryValues());
		
		// Gets outline
		try {
			outlineChnlInto( chnl, chnlOutBinary, do3D, erodeEdges );
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
		
		return chnlOutBinary;
	}
	
	/** Outline using multiple erosions to create a deeper outline */
	public static ObjMask outline(ObjMask mask, int numberErosions, boolean erodeEdges, boolean do3D ) throws CreateException {
		assert( mask.getVoxelBox().extnt().getZ() > 0 );
		
		ObjMask maskIn = mask.duplicate();
				
		if (numberErosions<1) {
			assert false;
		}
				
		BinaryVoxelBox<ByteBuffer> bufferOut = outlineMultiplex( maskIn.binaryVoxelBox(), numberErosions, erodeEdges, do3D );
		return new ObjMask( maskIn.getBoundingBox(), bufferOut.getVoxelBox(), bufferOut.getBinaryValues() );

	}

	private static BinaryVoxelBox<ByteBuffer> outlineMultiplex( BinaryVoxelBox<ByteBuffer> voxelBox, int numberErosions, boolean erodeEdges, boolean do3D) throws CreateException {
		// If we just want an edge of size 1, we can do things more optimally
		if (numberErosions==1) {
			return outlineByKernel(voxelBox, erodeEdges, do3D);
		} else {
			return outlineByErosion( voxelBox, numberErosions, erodeEdges, do3D );
		}
	}
		
	// Assumes imgChnlOut has the same ImgChnlRegions
	private static void outlineChnlInto( BinaryChnl imgChnl, BinaryChnl imgChnlOut, boolean do3D, boolean erodeEdges ) throws OperationFailedException {

		BinaryVoxelBox<ByteBuffer> box = new BinaryVoxelBoxByte( imgChnl.getVoxelBox(), imgChnl.getBinaryValues() );
		
		BinaryVoxelBox<ByteBuffer> outline;
		try {
			outline = outlineByKernel(box, erodeEdges, do3D );
		} catch (CreateException e1) {
			throw new OperationFailedException(e1);
		}
		
		try {
			imgChnlOut.replaceBy(outline);
		} catch (IncorrectImageSizeException e) {
			assert false;
		}
		
	}
		
	/** Find an outline only 1 pixel deep by using a kernel directly */
	private static BinaryVoxelBox<ByteBuffer> outlineByKernel( BinaryVoxelBox<ByteBuffer> voxelBox, boolean erodeEdges, boolean do3D ) throws CreateException {
		
		// if our solid is too small, we don't apply the kernel, as it fails on anything less than 3x3, and instead we simply return the solid as it is
		if (isTooSmall(voxelBox.extnt(), do3D)) {
			return voxelBox.duplicate();
		}
		
		BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();
		
		BinaryKernel kernel = new OutlineKernel3( bvb, !erodeEdges, do3D );
		
		VoxelBox<ByteBuffer> out = ApplyKernel.apply( kernel, voxelBox.getVoxelBox(), bvb );
		return new BinaryVoxelBoxByte(out, voxelBox.getBinaryValues());
	}
	
	/** Find an outline by doing (maybe more than 1) morphological erosions, and subtracting from original object */
	private static BinaryVoxelBox<ByteBuffer> outlineByErosion( BinaryVoxelBox<ByteBuffer> voxelBox, int numberErosions, boolean erodeEdges, boolean do3D ) throws CreateException {
		
		// Otherwise if > 1
		VoxelBox<ByteBuffer> eroded = multipleErode( voxelBox, numberErosions, erodeEdges, do3D );
		
		// Binary and between the original version and the eroded version
		assert(eroded!=null);
		BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();
		BinaryChnlXor.apply(voxelBox.getVoxelBox(), eroded, bvb, bvb);
		return voxelBox;
	}
	
	private static VoxelBox<ByteBuffer> multipleErode( BinaryVoxelBox<ByteBuffer> voxelBox, int numberErosions, boolean erodeEdges, boolean do3D ) {
		
		BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();
		BinaryKernel kernelErosion = new ErosionKernel3( bvb, erodeEdges, do3D );
		
		VoxelBox<ByteBuffer> eroded = ApplyKernel.apply( kernelErosion, voxelBox.getVoxelBox(), bvb ); 
		for (int i=1; i<numberErosions; i++) {
			eroded = ApplyKernel.apply( kernelErosion, eroded, bvb );
		}
		
		return eroded;
	}

	private static boolean isTooSmall( Extent e, boolean do3D ) {
		if (e.getX()<3 || e.getY()<3) {
			return true;
		}
		if (do3D && e.getZ() < 3) {
			return true;
		}
		return false;
	}
}
