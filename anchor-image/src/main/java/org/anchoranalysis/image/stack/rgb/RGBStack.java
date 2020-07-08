package org.anchoranalysis.image.stack.rgb;

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

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

import com.google.common.base.Preconditions;

public class RGBStack {

	private Stack chnls;
	
	public RGBStack( ImageDimensions sd, ChannelFactorySingleType factory ) {
		chnls = new Stack(sd, factory, 3);
	}
	
	public RGBStack( Stack in ) {
		chnls = in;
	}
	
	private RGBStack() {
		
	}
	
	private RGBStack( RGBStack src ) {
		chnls = src.chnls.duplicate();
	}
	
	public RGBStack( Channel red, Channel green, Channel blue) throws IncorrectImageSizeException {
		chnls = new Stack();
		chnls.addChnl( red );
		chnls.addChnl( green );
		chnls.addChnl( blue );
	}
	
	public Channel getRed() {
		return chnls.getChnl(0);
	}

	public Channel getGreen() {
		return chnls.getChnl(1);
	}
	public Channel getBlue() {
		return chnls.getChnl(2);
	}
	
	public Channel getChnl( int index ) {
		return chnls.getChnl(index);
	}
	
	public ImageDimensions getDimensions() {
		return chnls.getDimensions();
	}
	
	public RGBStack extractSlice(int z) {
		RGBStack out = new RGBStack();
		out.chnls = chnls.extractSlice(z);
		return out;
	}
	
	public Stack asStack() {
		return chnls;
	}
	
	public DisplayStack backgroundStack() throws CreateException {
		return DisplayStack.create( this );
	}
	
	public RGBStack duplicate() {
		return new RGBStack(this);
	}

	public boolean allChnlsHaveType(VoxelDataType chnlDataType) {
		return chnls.allChnlsHaveType(chnlDataType);
	}
	
	
	private static void writePoint( Point3i point, Channel chnl, byte toWrite ) {
		int index = chnl.getDimensions().getExtnt().offset(point.getX(), point.getY());
		chnl.getVoxelBox().asByte().getPixelsForPlane( point.getZ() ).buffer().put( index, toWrite );
	}
	
	
	// Only supports 8-bit
	public void writeRGBPoint( Point3i point, RGBColor color) {
		assert( chnls.allChnlsHaveType(VoxelDataTypeUnsignedByte.INSTANCE) );
		writePoint(point, chnls.getChnl(0), (byte) color.getRed() );
		writePoint(point, chnls.getChnl(1), (byte) color.getGreen() );
		writePoint(point, chnls.getChnl(2), (byte) color.getBlue() );
	}
	
	// Only supports 8-bit
	public void writeRGBMaskToSlice( ObjectMask mask, BoundingBox bbox, RGBColor c, Point3i pntGlobal, int zLocal, ReadableTuple3i maxGlobal) {
		Preconditions.checkArgument( pntGlobal.getZ()>=0 );
		Preconditions.checkArgument( chnls.getNumChnl()==3 );
		Preconditions.checkArgument( chnls.allChnlsHaveType(VoxelDataTypeUnsignedByte.INSTANCE) );
		
		byte maskOn = mask.getBinaryValuesByte().getOnByte();
		
		ByteBuffer inArr = mask.getVoxelBox().getPixelsForPlane(zLocal).buffer();
		
		ByteBuffer red = extractBuffer(0, pntGlobal.getZ());
		ByteBuffer green = extractBuffer(1, pntGlobal.getZ());
		ByteBuffer blue = extractBuffer(2, pntGlobal.getZ());
		
		Extent eMask = mask.getBoundingBox().extent();
		
 		for (pntGlobal.setY(bbox.cornerMin().getY()); pntGlobal.getY() <= maxGlobal.getY(); pntGlobal.incrementY() ) {
		
			for (pntGlobal.setX(bbox.cornerMin().getX()); pntGlobal.getX() <= maxGlobal.getX(); pntGlobal.incrementX() ) {	
				
				int maskOffset = eMask.offset(
					pntGlobal.getX() - mask.getBoundingBox().cornerMin().getX(),
					pntGlobal.getY() - mask.getBoundingBox().cornerMin().getY()
				);
				
				if (inArr.get(maskOffset)!=maskOn ) {
					continue;
				}

				RGBOutputUtils.writeRGBColorToByteArr(
					c,
					pntGlobal,
					chnls.getChnl(0).getDimensions(),
					red, blue,
					green
				);
			}
		}
	}
	
	private ByteBuffer extractBuffer( int chnlIndex, int zIndex ) {
		return chnls.getChnl(chnlIndex).getVoxelBox().asByte().getPlaneAccess().getPixelsForPlane(
			zIndex
		).buffer();
	}	
}