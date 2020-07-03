package org.anchoranalysis.image.convert;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

public class IJWrap {

	private static final String IMAGEJ_UNIT_MICRON = "micron";
	private static final String IMAGEJ_IMAGE_NAME = "imagename";
	
	/** A multiplication-factor to convert microns to meters */
	private static final int MICRONS_TO_METERS = 1000000;
	
	private static VoxelDataType dataTypeByte = VoxelDataTypeUnsignedByte.INSTANCE;
	private static VoxelDataType dataTypeShort = VoxelDataTypeUnsignedShort.INSTANCE;
	
	private IJWrap() {}
	
	public static Channel chnlFromImageStackByte( ImageStack imageStack, ImageResolution res, ChannelFactorySingleType factory ) {
		
		ImageDimensions sd = new ImageDimensions(
			new Extent(imageStack.getWidth(), imageStack.getHeight(), imageStack.getSize()),
			res
		);
		
		Channel chnlOut = factory.createEmptyUninitialised( sd );
		
		VoxelBox<ByteBuffer> vbOut = chnlOut.getVoxelBox().asByte();
		copyImageStackIntoVoxelBoxByte( imageStack, vbOut );
		return chnlOut;
	}

	public static Channel chnlFromImagePlus( ImagePlus imagePlus, ImageResolution res ) throws IncorrectVoxelDataTypeException {
		
		ChannelFactory factory = ChannelFactory.instance();
		
		ImageDimensions sd = new ImageDimensions(
			new Extent(
				imagePlus.getWidth(),
				imagePlus.getHeight(),
				imagePlus.getStackSize()
			),
			res
		);
				
		if (imagePlus.getType()==ImagePlus.GRAY8) {
			return chnlFromImagePlusByte(
				imagePlus,
				sd,
				factory.get( VoxelDataTypeUnsignedByte.INSTANCE )
			);
		} else if (imagePlus.getType()==ImagePlus.GRAY16) {
			return chnlFromImagePlusShort(
				imagePlus,
				sd,
				factory.get( VoxelDataTypeUnsignedShort.INSTANCE )
			);
		} else {
			throw new IncorrectVoxelDataTypeException("Only unsigned-8 and unsigned 16bit supported");
		}
	}
	
	public static VoxelBoxWrapper voxelBoxFromImagePlus( ImagePlus imagePlus ) throws IncorrectVoxelDataTypeException {
		
		if (imagePlus.getType()==ImagePlus.GRAY8) {
			return new VoxelBoxWrapper(voxelBoxFromImagePlusByte(imagePlus));
		} else if (imagePlus.getType()==ImagePlus.GRAY16) {
			return new VoxelBoxWrapper(voxelBoxFromImagePlusShort(imagePlus));
		} else {
			throw new IncorrectVoxelDataTypeException("Only unsigned-8 and unsigned 16bit supported");
		}
	}
	
	
	public static VoxelBox<ByteBuffer> voxelBoxFromImagePlusByte( ImagePlus imagePlus ) {
		VoxelBox<ByteBuffer> vbOut = VoxelBoxFactory.getByte().create( new Extent(imagePlus.getWidth(), imagePlus.getHeight(), imagePlus.getZ() ) );
		copyImageStackIntoVoxelBoxByte( imagePlus.getImageStack(), vbOut );
		return vbOut;
	}
	
	public static VoxelBox<ShortBuffer> voxelBoxFromImagePlusShort( ImagePlus imagePlus ) {
		VoxelBox<ShortBuffer> vbOut = VoxelBoxFactory.getShort().create( new Extent(imagePlus.getWidth(), imagePlus.getHeight(), imagePlus.getZ() ) );
		copyImageStackIntoVoxelBoxShort( imagePlus.getImageStack(), vbOut );
		return vbOut;
	}
	
	
	public static ImageProcessor imageProcessor( VoxelBoxWrapper vb, int z ) {
		
		assert( vb.any().extent().getVolumeXY()==vb.any().getPixelsForPlane(z).buffer().capacity() );
		
		if (vb.getVoxelDataType().equals(dataTypeByte)) {
			return imageProcessorByte( vb.asByte().getPlaneAccess(), z);
		} else if (vb.getVoxelDataType().equals(dataTypeShort)) {
			return imageProcessorShort( vb.asShort().getPlaneAccess(), z);
		} else {
			throw new IncorrectVoxelDataTypeException("Only byte or short data types are supported");
		}
	}
	

	public static ImageProcessor imageProcessorByte( IPixelsForPlane<ByteBuffer> planeAccess, int z ) {
		Extent e = planeAccess.extent();
		return new ByteProcessor( e.getX(), e.getY(), planeAccess.getPixelsForPlane(z).buffer().array(), null);
	}
	
	public static ImageProcessor imageProcessorShort( IPixelsForPlane<ShortBuffer> planeAccess, int z ) {
		Extent e = planeAccess.extent();
		assert( e.getVolumeXY()==planeAccess.getPixelsForPlane(z).buffer().array().length );
		return new ShortProcessor( e.getX(), e.getY(), planeAccess.getPixelsForPlane(z).buffer().array(), null);
	}

	
	public static ImageProcessor imageProcessorByte( VoxelBuffer<ByteBuffer> vb, Extent e ) {
		return new ByteProcessor( e.getX(), e.getY(), vb.buffer().array(), null);
	}
	
	public static ImageProcessor imageProcessorShort( VoxelBuffer<ShortBuffer> vb, Extent e ) {
		return new ShortProcessor( e.getX(), e.getY(), vb.buffer().array(), null);
	}
	
	
	public static ImagePlus createImagePlus( VoxelBoxWrapper voxelBox ) {
	
		ImageStack stackNew = createStackForVoxelBox( voxelBox );
		return createImagePlus( stackNew, new ImageDimensions( voxelBox.any().extent(), new ImageResolution() ), 1, 1, false );
	}
	
	public static ImagePlus createImagePlus( Channel chnl ) throws CreateException {
		Stack stack = new Stack( chnl );
		return createImagePlus(stack,false);
	}
	
	public static ImagePlus createImagePlus( Stack stack, boolean makeRGB ) throws CreateException {
		
		ImageDimensions sd = stack.getChnl(0).getDimensions();
		
		// If we're making an RGB then we need to convert our stack
		
		ImageStack stackNew = null;
		if (makeRGB) {
			stackNew = createColorProcessorStack( new RGBStack( (Stack) stack) );
		} else {
			stackNew = createInterleavedStack( sd.getExtnt(), stack);
		}
		
		ImagePlus imp = createImagePlus( stackNew, sd, stack.getNumChnl(), 1, !makeRGB );
		
		maybeCorrectComposite(stack, imp);
		
		assert( imp.getNSlices()==sd.getZ() );
		return imp;
	}
	
	private static void maybeCorrectComposite( Stack stack, ImagePlus imp ) {
		
		// Avoids IMP being set to composite mode, if it's a single channel stack
		if (stack.getNumChnl()==1) {
			
			if (imp instanceof CompositeImage) {
				((CompositeImage)imp).setMode(IJ.GRAYSCALE);
			}
		}		
	}
	
	public static ImagePlus createImagePlus( ImageStack stack, ImageDimensions sd, int num_chnl, int num_frames, boolean makeComposite ) {
		
		// If we're making an RGB then we need to convert our stack
		ImagePlus imp = null;
		if (makeComposite) {
			imp = createCompositeImagePlus( stack, num_chnl, sd.getZ(), num_frames, IMAGEJ_IMAGE_NAME );
		} else {
			imp = createNonCompositeImagePlus( stack, 1, sd.getZ(), num_frames, IMAGEJ_IMAGE_NAME );
		}
		
		imp.getCalibration().setXUnit(IMAGEJ_UNIT_MICRON);
		imp.getCalibration().setYUnit(IMAGEJ_UNIT_MICRON);
		imp.getCalibration().setZUnit(IMAGEJ_UNIT_MICRON);
		imp.getCalibration().pixelWidth = sd.getRes().getX() * MICRONS_TO_METERS;
		imp.getCalibration().pixelHeight = sd.getRes().getY() * MICRONS_TO_METERS;
		imp.getCalibration().pixelDepth = sd.getRes().getZ() * MICRONS_TO_METERS;
		
		assert( imp.getNSlices()==sd.getZ() );
		
		return imp;
	}
	
	
	// Creates an ImageJ colour processor stack (RGB in one processor) from an existing stack of three separate RGB channels 
	public static ImageStack createColorProcessorStack( RGBStack stack ) {
		
		ImageDimensions sd = stack.getChnl(0).getDimensions();

		ImageStack stackNew = new ImageStack( sd.getX(), sd.getY() );

		int srcSliceNum = 0;
		
		VoxelBox<ByteBuffer> vbRed = stack.getChnl(srcSliceNum++).getVoxelBox().asByte();
		VoxelBox<ByteBuffer> vbGreen = stack.getChnl(srcSliceNum++).getVoxelBox().asByte();
		VoxelBox<ByteBuffer> vbBlue = stack.getChnl(srcSliceNum++).getVoxelBox().asByte();

		for (int z=0; z<sd.getZ(); z++) {
			ColorProcessor cp = new ColorProcessor( sd.getX(), sd.getY() );
			
			byte[] redPixels = vbRed.getPlaneAccess().getPixelsForPlane(z).buffer().array();
			byte[] greenPixels = vbGreen.getPlaneAccess().getPixelsForPlane(z).buffer().array();
			byte[] bluePixels = vbBlue.getPlaneAccess().getPixelsForPlane(z).buffer().array();

			cp.setRGB(redPixels, greenPixels, bluePixels);
			stackNew.addSlice( String.valueOf(z), cp );	
		}
		
		return stackNew;
	}
	
	private static Channel chnlFromImagePlusByte( ImagePlus imagePlus, ImageDimensions sd, ChannelFactorySingleType factory ) {
		
		Channel chnlOut = factory.createEmptyUninitialised( sd );
		VoxelBox<ByteBuffer> vbOut = chnlOut.getVoxelBox().asByte();
		
		for( int z=0; z<chnlOut.getDimensions().getZ(); z++) {
			
			ImageProcessor ip = imagePlus.getImageStack().getProcessor(z+1);
			byte[] arr = (byte[]) ip.getPixels();
			vbOut.setPixelsForPlane(z, VoxelBufferByte.wrap(arr));
		}
		return chnlOut;
	}
	
	private static Channel chnlFromImagePlusShort( ImagePlus imagePlus, ImageDimensions sd, ChannelFactorySingleType factory ) {
		
		Channel chnlOut = factory.createEmptyUninitialised( sd );
		
		VoxelBox<ShortBuffer> vbOut = chnlOut.getVoxelBox().asShort();
		
		for( int z=0; z<sd.getZ(); z++) {

			ImageProcessor ip = imagePlus.getImageStack().getProcessor(z+1);
			short[] arr = (short[]) ip.getPixels();
			vbOut.setPixelsForPlane(z, VoxelBufferShort.wrap(arr));
		}
		return chnlOut;
	}
	
	
	private static ImagePlus createCompositeImagePlus( ImageStack stackNew, int num_chnl, int num_slices, int num_frames, String imageName ) {
		ImagePlus impNC = createNonCompositeImagePlus( stackNew, num_chnl, num_slices, num_frames, imageName);
		assert( impNC.getNSlices()==num_slices );
		ImagePlus impOut = new CompositeImage( impNC, CompositeImage.COLOR );
		
		// The Composite image sometimes sets these wrong, so we force the correct dimensionality
		impOut.setDimensions(num_chnl, num_slices, num_frames);
		return impOut;
	}
	
	
	// Create an interleaved stack of images
	private static ImageStack createInterleavedStack( Extent e, Stack stack) throws CreateException {
		
		ImageStack stackNew = new ImageStack( e.getX(), e.getY() );
		
		for (int z=0; z<e.getZ(); z++) {
			
			for (int c=0; c<stack.getNumChnl(); c++) {	
				Channel chnl = stack.getChnl(c);
				VoxelBoxWrapper vb = chnl.getVoxelBox();
				
				ImageProcessor ip = IJWrap.imageProcessor(vb,z);
				stackNew.addSlice( String.valueOf(z), ip );	
			}
		}
		
		return stackNew;
	}
	
	
	private static void copyImageStackIntoVoxelBoxByte( ImageStack stack, VoxelBox<ByteBuffer> vbOut ) {
		for( int z=0; z<vbOut.extent().getZ(); z++) {
			ImageProcessor ip = stack.getProcessor(z+1);
			vbOut.setPixelsForPlane(z, voxelBufferFromImageProcessorByte(ip) );
		}
	}
	
	private static void copyImageStackIntoVoxelBoxShort( ImageStack stack, VoxelBox<ShortBuffer> vbOut ) {
		for( int z=0; z<vbOut.extent().getZ(); z++) {
			ImageProcessor ip = stack.getProcessor(z+1);
			vbOut.setPixelsForPlane(z, voxelBufferFromImageProcessorShort(ip) );
		}
	}
	
	public static VoxelBuffer<ByteBuffer> voxelBufferFromImageProcessorByte( ImageProcessor ip ) {
		byte[] arr = (byte[]) ip.getPixels();
		return VoxelBufferByte.wrap(arr);
	}
	
	public static VoxelBuffer<ShortBuffer> voxelBufferFromImageProcessorShort( ImageProcessor ip ) {
		short[] arr = (short[]) ip.getPixels();
		return VoxelBufferShort.wrap(arr);
	}
	
	
	
	// Create a stack composed entirely of a single channel
	private static ImageStack createStackForVoxelBox( VoxelBoxWrapper voxelBox ) {
		
		Extent e = voxelBox.any().extent();
		ImageStack stackNew = new ImageStack( e.getX(), e.getY() );
		for (int z=0; z<e.getZ(); z++) {
			
			ImageProcessor ip = imageProcessor(voxelBox, z);
			stackNew.addSlice( String.valueOf(z), ip );	
		}
		return stackNew;
	}
	
	private static ImagePlus createNonCompositeImagePlus( ImageStack stackNew, int num_chnl, int num_slices, int num_frames, String imageName ) {
		ImagePlus imp = new ImagePlus();
		imp.setStack( stackNew, num_chnl, num_slices, num_frames );
		imp.setTitle( imageName ); 
		return imp;
	}
}
