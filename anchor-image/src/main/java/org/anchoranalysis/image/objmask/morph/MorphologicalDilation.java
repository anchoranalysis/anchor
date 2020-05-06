package org.anchoranalysis.image.objmask.morph;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.ConditionalKernel;
import org.anchoranalysis.image.voxel.kernel.dilateerode.DilationKernel3;
import org.anchoranalysis.image.voxel.kernel.dilateerode.DilationKernel3ZOnly;

public class MorphologicalDilation {

	/**
	 * 
	 * @param om
	 * @param extnt if non-NULL ensures the object stays within certain bounds
	 * @param do3D
	 * @param iterations
	 * @return
	 * @throws CreateException
	 */
	public static ObjMask createDilatedObjMask( ObjMask om, Optional<Extent> extnt, boolean do3D, int iterations, boolean bigNghb ) throws CreateException {
		
		Point3i grow = do3D ? new Point3i(iterations,iterations,iterations) : new Point3i(iterations,iterations,0);
		
		try {
			ObjMask omGrown = om.growBuffer(grow, grow, extnt );
			omGrown.setVoxelBox(
				dilate(omGrown.binaryVoxelBox(), do3D, iterations, null, 0, bigNghb).getVoxelBox()
			);
	
			return omGrown;	
		} catch (OperationFailedException e) {
			throw new CreateException("Cannot grow object-mask", e);
		}
	}
	

	private static BinaryKernel createDilationKernel( BinaryValuesByte bv, boolean do3D, VoxelBox<ByteBuffer> backgroundVb, int minIntensityValue, boolean zOnly, boolean outsideAtThreshold, boolean bigNghb ) throws CreateException {

		BinaryKernel kernelDilation;
		
		if (zOnly) {
			
			if (bigNghb) {
				throw new CreateException("BigNghb not supported for zOnly");
			}
			
			kernelDilation = new DilationKernel3ZOnly( bv, outsideAtThreshold);
		} else {
			kernelDilation = new DilationKernel3( bv, outsideAtThreshold, do3D, bigNghb);
		}
		
		// TODO HACK FIX , how we handle the different regions
		
		if(minIntensityValue>0 && backgroundVb!=null) {
			return new ConditionalKernel(kernelDilation, minIntensityValue, backgroundVb );
		} else {
			return kernelDilation;
		}
	}
	
	

	
	public static BinaryVoxelBox<ByteBuffer> dilate( BinaryVoxelBox<ByteBuffer> bvb, boolean do3D, int iterations, VoxelBox<ByteBuffer> backgroundVb, int minIntensityValue, boolean bigNghb ) throws CreateException {
		return dilate(bvb, do3D, iterations, backgroundVb, minIntensityValue, false, false, null, bigNghb);
	}
	
	/**
	 * Performs a morpholgical dilation operation
	 * 
	 * TODO: merge do3D and zOnly parameters into an enum
	 * 
	 * @param bvb input-buffer
	 * @param do3D if TRUE, 6-neighbourhood dilation, otherwise 4-neighnrouhood
	 * @param iterations number of dilations
	 * @param backgroundVb optional background-buffer that can influence the dilation with the minIntensityValue
	 * @param minIntensityValue minimumIntensity on the background, for a pixel to be included
	 * @param zOnly if TRUE, only peforms dilation in z direction. Requires do3D to be TRUE
	 * @param outsideAtThreshold if TRUE, pixels outside the buffer are treated as ON, otherwise as OFF
	 * @param acceptConditions if non-NULL, imposes a condition on each iteration that must be passed
	 * @return a new buffer containing the results of the dilation-operations
	 * @throws CreateException
	 */
	public static BinaryVoxelBox<ByteBuffer> dilate(
		BinaryVoxelBox<ByteBuffer> bvb,
		boolean do3D,
		int iterations,
		VoxelBox<ByteBuffer> backgroundVb,
		int minIntensityValue,
		boolean zOnly,
		boolean outsideAtThreshold,
		IAcceptIteration acceptConditions,
		boolean bigNghb
	) throws CreateException {

		assert( (zOnly&&do3D)==zOnly );
		
		BinaryKernel kernelDilation = createDilationKernel(
			bvb.getBinaryValues().createByte(),
			do3D,
			backgroundVb,
			minIntensityValue,
			zOnly,
			outsideAtThreshold,
			bigNghb
		);
		
		VoxelBox<ByteBuffer> buf = bvb.getVoxelBox();
		for( int i=0; i<iterations; i++) {
			VoxelBox<ByteBuffer> next = ApplyKernel.apply(kernelDilation, buf, bvb.getBinaryValues().createByte() );
			
			try {
				if (acceptConditions!=null) {
					if (!acceptConditions.acceptIteration(next, bvb.getBinaryValues())) {
						break;
					}
				}
			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
			
			buf = next;
		}
		return new BinaryVoxelBoxByte(buf, bvb.getBinaryValues() );
	}
	
	public static interface IAcceptIteration {
		/**
		 * 
		 * @param buffer
		 * @param bvb
		 * @return TRUE if the particular iteration should be accepted, FALSE otherwise
		 */
		boolean acceptIteration(  VoxelBox<ByteBuffer> buffer, BinaryValues bvb ) throws OperationFailedException;
	}
	
	public static class AcceptIterationList implements IAcceptIteration {
		private List<IAcceptIteration> list = new ArrayList<>();

		@Override
		public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) throws OperationFailedException {
			for (IAcceptIteration ai : list) {
				if (!ai.acceptIteration(buffer, bvb)) {
					return false;
				}
			}
			return true;
		}

		public boolean add(IAcceptIteration e) {
			return list.add(e);
		}
		
		
	}
	
	public static class RejectIterationIfAllHigh implements IAcceptIteration {

		@Override
		public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) {
			// We exit early if there's no off-pixel
			BinaryVoxelBoxByte nextBinary = new BinaryVoxelBoxByte(buffer, bvb );
			return nextBinary.hasOffVoxel();
		}
		
	}
	
	public static class RejectIterationIfLowDisconnected implements IAcceptIteration {

		@Override
		public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) throws OperationFailedException {
			BinaryVoxelBoxByte nextBinary = new BinaryVoxelBoxByte(buffer, bvb.createInverted() );
			
			ObjMask omMask = new ObjMask(nextBinary);
			return omMask.checkIfConnected();
			
			
		}
		
	}

}
