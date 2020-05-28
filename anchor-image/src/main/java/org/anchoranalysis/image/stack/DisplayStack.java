package org.anchoranalysis.image.stack;

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


import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.bufferedimage.BufferedImageFactory;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.stack.region.RegionExtracterFromDisplayStack;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.chnl.ChnlConverterChnlUpperLowerQuantileIntensity;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

// Byte Stack that contains 1 or 3 channels
//  so that we cand display it as either
//  a monochrome or RGB 8-bit image
//
// A voxelBoxConverter is optionally associated with each channel
//  and is used to convert the source images into 8-bits
public class DisplayStack {
	
	private Stack delegate;
	private List<ChnlConverterAttached<Chnl,ByteBuffer>> listConverters = new ArrayList<>(); 

	
	// START: factory methods
	
	// We don't allow
	
	public static DisplayStack create( Chnl chnl ) throws CreateException {
		DisplayStack ds = new DisplayStack();
		ds.delegate = new Stack( chnl );
		try {
			ds.addConvertersAsNeeded();
		} catch (SetOperationFailedException e) {
			throw new CreateException(e);
		}
		return ds;
	}
	
	public static DisplayStack create( Stack stack ) throws CreateException {
		DisplayStack ds = new DisplayStack();
		ds.delegate = stack;
		ds.checkChnlNum( stack.getNumChnl() );
		try {
			ds.addConvertersAsNeeded();
		} catch (SetOperationFailedException e) {
			throw new CreateException(e);
		}
		return ds;
	}
	
	public static DisplayStack create( RGBStack rgbStack ) throws CreateException {
		DisplayStack ds = new DisplayStack();
		ds.delegate = rgbStack.asStack();
		try {
			ds.addConvertersAsNeeded();
		} catch (SetOperationFailedException e) {
			throw new CreateException(e);
		}
		return ds;
	}
	
	// END: factory methods
	
	private DisplayStack() {
		
	}
	
	
	private void addConvertersAsNeeded() throws SetOperationFailedException {
		addEmptyConverters( getNumChnl() );
		for( int c=0; c<getNumChnl(); c++) {
			if (!delegate.getChnl(c).getVoxelDataType().equals( VoxelDataTypeUnsignedByte.instance )) {
				setConverterFor(c,new ChnlConverterChnlUpperLowerQuantileIntensity(0.0001,0.9999) );
			}
		}
	}
	
	public int numNonNullConverters() {
		int a = 0;
		for( ChnlConverterAttached<Chnl,ByteBuffer> c : listConverters ) {
			if (c!=null) {
				a++;
			}
		}
		return a;
	}
	
	// We don't need to worry about channel numbers
	// TODO let's switch this back to private	
	private DisplayStack( Stack stack, List<ChnlConverterAttached<Chnl,ByteBuffer>> listConverters ) throws CreateException {
		delegate = stack;
		this.listConverters = listConverters;
		
		for( int c=0; c<stack.getNumChnl(); c++) {
			ChnlConverterAttached<Chnl,ByteBuffer> cca = listConverters.get(c);
			if (cca!=null) {
				try {
					cca.attachObject( stack.getChnl(c) );
				} catch (OperationFailedException e) {
					throw new CreateException("Cannot attach the chnl to the converter");
				}
			}
		}
	}
	
	private void checkChnlNum( int numChnl ) throws CreateException {
		
		if (numChnl>=4) {
			throw new CreateException(
				String.format("Cannot convert to DisplayStack as there are %d channels. There must be 3 or less.", numChnl)
			);
		}

		if (numChnl==2) {
			try {
				delegate.addBlankChnl();
			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
		}
	}
	
	private void addEmptyConverters( int num ) {
		for( int c=0; c<num; c++ ) {
			listConverters.add( null );
		}
	}
	
	private void setConverterFor( int chnlNum, ChnlConverterAttached<Chnl,ByteBuffer> converter ) throws SetOperationFailedException {
		try {
			converter.attachObject( delegate.getChnl(chnlNum) );
		} catch (OperationFailedException e) {
			throw new SetOperationFailedException(e);
		}
		listConverters.set(chnlNum, converter );
	}
	
	
	public ImageDim getDimensions() {
		return delegate.getDimensions();
	}


	public final int getNumChnl() {
		return delegate.getNumChnl();
	}

	// Only creates a new channel if needs be, otherwise reuses existing channel
	public Chnl createChnl(int index, boolean alwaysNew) {
		
		Chnl chnl = delegate.getChnl(index);
		
		ChnlConverterAttached<Chnl,ByteBuffer> converter = listConverters.get(index);
		
		if (converter!=null) {
			ConversionPolicy policy = alwaysNew ? ConversionPolicy.ALWAYS_NEW : ConversionPolicy.DO_NOT_CHANGE_EXISTING;
			return converter.convert(chnl, policy );
		} else {
			if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.instance)) {
				// Datatype is not supported
				assert false;
			}
			
			return delegate.getChnl(index);
		}
	}
	
	
	
	
	// Always creates a new channel
	public Chnl createChnlDuplicate(int index) {
		
		Chnl chnl = delegate.getChnl(index);
		
		ChnlConverterAttached<Chnl,ByteBuffer> converter = listConverters.get(index);
		
		if (converter!=null) {
			return converter.convert(chnl, ConversionPolicy.ALWAYS_NEW );
		} else {
			if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.instance)) {
				// Datatype is not supported
				assert false;
			}
			
			return delegate.getChnl(index).duplicate();
		}
	}
	
	// Always creates a new channel, but just capturing a portion of the channel
	public Chnl createChnlDuplicateForBBox(int index, BoundingBox bbox) {
			
		Chnl chnl = delegate.getChnl(index);
		
		ChnlConverterAttached<Chnl,ByteBuffer> converter = listConverters.get(index);
		
		Chnl out = ChnlFactory.instance().createEmptyInitialised( new ImageDim(bbox.extent(), chnl.getDimensions().getRes()), VoxelDataTypeUnsignedByte.instance);
		
		if (converter!=null) {
			copyPixelsTo(index, bbox, out.getVoxelBox().asByte(), new BoundingBox(bbox.extent()) );
		} else {
			if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.instance)) {
				// Datatype is not supported
				assert false;
			}
			
			delegate.getChnl(index).getVoxelBox().copyPixelsTo(bbox, out.getVoxelBox(), new BoundingBox(bbox.extent()) );
		}
		return out;
	}
	

	public Stack createImgStack( boolean alwaysNew ) {
		Stack stackOut = new Stack();
		for( int c=0; c<delegate.getNumChnl(); c++ ) {
			try {
				stackOut.addChnl( createChnl(c, alwaysNew) );
			} catch (IncorrectImageSizeException e) {
				assert false;
			}
		}
		return stackOut;
	}
	
	public Stack createImgStackDuplicate() {
		Stack stackOut = new Stack();
		for( int c=0; c<delegate.getNumChnl(); c++ ) {
			try {
				stackOut.addChnl( createChnlDuplicate(c) );
			} catch (IncorrectImageSizeException e) {
				assert false;
			}
		}
		return stackOut;
	}
	
	// Copies pixels on a particular channel to an output buffer
	public void copyPixelsTo(int chnlIndex, BoundingBox sourceBox, VoxelBox<ByteBuffer> destVoxelBox, BoundingBox destBox) {
		
		Chnl chnl = delegate.getChnl(chnlIndex);
		
		ChnlConverterAttached<Chnl,ByteBuffer> converter = listConverters.get(chnlIndex);
		
		if (converter!=null) {
			BoundingBox allLocalBox = new BoundingBox(destBox.extent());
			
			VoxelBoxWrapper destBoxNonByte = VoxelBoxFactory.instance().create( destBox.extent(), chnl.getVoxelDataType() );
			chnl.getVoxelBox().copyPixelsTo(sourceBox, destBoxNonByte, allLocalBox );
			
			VoxelBox<ByteBuffer> destBoxByte = VoxelBoxFactory.instance().getByte().create( destBox.extent());
			converter.getVoxelBoxConverter().convertFrom(destBoxNonByte, destBoxByte);
			
			destBoxByte.copyPixelsTo(allLocalBox, destVoxelBox, destBox);
			
		} else {
			if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.instance)) {
				// Datatype is not supported
				assert false;
			}
			
			delegate.getChnl(chnlIndex).getVoxelBox().asByte().copyPixelsTo(sourceBox, destVoxelBox, destBox);
		}
	}
	
	public Optional<VoxelDataType> unconvertedDataType() {
		VoxelDataType dataType = delegate.getChnl(0).getVoxelDataType();
		// If they don't all have the same dataType we return null
		if (!delegate.allChnlsHaveType(dataType)) {
			return Optional.empty();
		}
		return Optional.of(dataType);
	}
	
	public int getUnconvertedVoxelAt( int c, int x, int y, int z) {
		Chnl chnl = delegate.getChnl(c);
		return chnl.getVoxelBox().any().getVoxel(x, y, z);
	}
	
	public RegionExtracter createRegionExtracter() {
		return new RegionExtracterFromDisplayStack( delegate, listConverters );
	}
	
	public DisplayStack maxIntensityProj() throws OperationFailedException {
		try {
			return new DisplayStack( delegate.maxIntensityProj(), listConverters );
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}

	public DisplayStack extractSlice(int z)
			throws CreateException {
		return new DisplayStack( delegate.extractSlice(z), listConverters );
	}
	
	public BufferedImage createBufferedImage() throws CreateException {
		if (delegate.getNumChnl()==3) {
			return BufferedImageFactory.createRGB(
				bufferForChnl(0),
				bufferForChnl(1),
				bufferForChnl(2),
				delegate.getDimensions().getExtnt()
			);
		}
		return BufferedImageFactory.createGrayscale(bufferForChnl(0));
	}
	
	public BufferedImage createBufferedImageBBox( BoundingBox bbox ) throws CreateException {
		
		if (bbox.extent().getZ()!=1) {
			throw new CreateException("BBox must have a single pixel z-height");
		}
		
		if (delegate.getNumChnl()==3) {
			return BufferedImageFactory.createRGB(
				bufferForChnlBBox(0,bbox),
				bufferForChnlBBox(1,bbox),
				bufferForChnlBBox(2,bbox),
				bbox.extent()
			);
		}
		return BufferedImageFactory.createGrayscale(bufferForChnlBBox(0,bbox));
	}
	
	
	private VoxelBox<ByteBuffer> bufferForChnl( int chnlNum ) {
		
		Chnl chnl = delegate.getChnl(chnlNum);
		
		ChnlConverterAttached<Chnl,ByteBuffer> converter = listConverters.get(chnlNum);
		
		if (converter!=null) {
			return converter.getVoxelBoxConverter().convertFrom(
				chnl.getVoxelBox(),
				VoxelBoxFactory.instance().getByte()
			);
		} else {
			if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.instance)) {
				// Datatype is not supported
				assert false;
			}
			return chnl.getVoxelBox().asByte();
		}
	}
	
	@SuppressWarnings("unchecked")
	private VoxelBox<ByteBuffer> bufferForChnlBBox( int chnlNum, BoundingBox bbox ) {
		
		Chnl chnl = delegate.getChnl(chnlNum);
		
		ChnlConverterAttached<Chnl,ByteBuffer> converter = listConverters.get(chnlNum);
		
		VoxelBox<?> vbUnconverted = chnl.getVoxelBox().any().createBufferAvoidNew(bbox);
		
		if (converter!=null) {
			return converter.getVoxelBoxConverter().convertFrom(
				new VoxelBoxWrapper(vbUnconverted),
				VoxelBoxFactory.instance().getByte()
			);
		} else {
			if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.instance)) {
				// Datatype is not supported
				assert false;
			}
			return (VoxelBox<ByteBuffer>) vbUnconverted;
		}
	}

}
