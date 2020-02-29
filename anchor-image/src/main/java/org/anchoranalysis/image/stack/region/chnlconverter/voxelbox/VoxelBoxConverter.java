package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

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


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public abstract class VoxelBoxConverter<DestinationType extends Buffer> {

	public VoxelBox<DestinationType> convertFrom( VoxelBoxWrapper vbIn, VoxelBoxFactoryTypeBound<DestinationType> factory ) {
		VoxelBox<DestinationType> vbOut = factory.create( vbIn.any().extnt() );
		convertFrom( vbIn, vbOut );
		return vbOut;
	}
	
	public void convertFrom( VoxelBoxWrapper vbIn, VoxelBox<DestinationType> vbOut ) {
		// Otherwise, depending on the input type we spawn in different directions
		if (vbIn.getVoxelDataType().equals( VoxelDataTypeUnsignedByte.instance )) {
			convertFromByte(vbIn.asByte(), vbOut);
		} else if (vbIn.getVoxelDataType().equals( VoxelDataTypeFloat.instance )) {
			convertFromFloat(vbIn.asFloat(), vbOut);
		} else if (vbIn.getVoxelDataType().equals( VoxelDataTypeUnsignedShort.instance )) {
			convertFromShort(vbIn.asShort(), vbOut);
		} else if (vbIn.getVoxelDataType().equals( VoxelDataTypeUnsignedInt.instance )) {
			convertFromInt(vbIn.asInt(), vbOut);
		}
	}
	
	public void convertFromByte(VoxelBox<ByteBuffer> vbIn, VoxelBox<DestinationType> vbOut) {
		
		for (int z=0; z<vbIn.extnt().getZ(); z++) {
			VoxelBuffer<ByteBuffer> bufferIn = vbIn.getPixelsForPlane(z);
			vbOut.setPixelsForPlane(z, convertFromByte(bufferIn) );
		}
	}
	
	public void convertFromFloat(VoxelBox<FloatBuffer> vbIn, VoxelBox<DestinationType> vbOut) {
		
		for (int z=0; z<vbIn.extnt().getZ(); z++) {
			VoxelBuffer<FloatBuffer> bufferIn = vbIn.getPixelsForPlane(z);
			vbOut.setPixelsForPlane(z, convertFromFloat(bufferIn) );
		}
	}
	
	public void convertFromInt(VoxelBox<IntBuffer> vbIn, VoxelBox<DestinationType> vbOut) {
		
		for (int z=0; z<vbIn.extnt().getZ(); z++) {
			VoxelBuffer<IntBuffer> bufferIn = vbIn.getPixelsForPlane(z);
			vbOut.setPixelsForPlane(z, convertFromInt(bufferIn) );
		}
	}
	
	public void convertFromShort(VoxelBox<ShortBuffer> vbIn, VoxelBox<DestinationType> vbOut) {
		
		for (int z=0; z<vbIn.extnt().getZ(); z++) {
			VoxelBuffer<ShortBuffer> bufferIn = vbIn.getPixelsForPlane(z);
			vbOut.setPixelsForPlane(z, convertFromShort(bufferIn) );
		}
	}
	
	public abstract VoxelBuffer<DestinationType> convertFromByte(VoxelBuffer<ByteBuffer> in);
	
	public abstract VoxelBuffer<DestinationType> convertFromFloat(VoxelBuffer<FloatBuffer> in);
	
	public abstract VoxelBuffer<DestinationType> convertFromInt(VoxelBuffer<IntBuffer> in);
	
	public abstract VoxelBuffer<DestinationType> convertFromShort(VoxelBuffer<ShortBuffer> in);

}
