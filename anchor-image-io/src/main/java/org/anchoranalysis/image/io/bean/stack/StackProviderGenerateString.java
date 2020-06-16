package org.anchoranalysis.image.io.bean.stack;

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


import java.nio.ShortBuffer;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.generator.raster.StringRasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverterToUnsignedShort;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToShortScaleByType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class StackProviderGenerateString extends StackProvider {

	// START BEANS
	@BeanField
	private StringRasterGenerator stringRasterGenerator;
	
	@BeanField
	private boolean createShort;
	
	@BeanField
	private StackProvider intensityProvider;	// The string is the maximum-value of the image
	
	@BeanField
	private StackProvider repeatZProvider;		// Repeats the generated (2D) string in z, so it's the same z-extent as repeatZProvider
	// END BEANS

	@Override
	public Stack create() throws CreateException {
		
		Stack label2D = create2D();
		
		if (repeatZProvider!=null) {
		
			Stack repeatZ = repeatZProvider.create();
			int zHeight = repeatZ.getDimensions().getZ();
			
			Stack out = new Stack();
			for( Channel chnl : label2D ) {
				try {
					out.addChnl( createExpandedChnl(chnl, zHeight) );
				} catch (IncorrectImageSizeException e) {
					throw new CreateException(e);
				}
			}
			return out;
			
		} else {
			return label2D;
		}
	}
	
	private static int maxValueFromStack( Stack stack ) {
		int max = 0;
		for( Channel chnl : stack ) {
			int chnlVal = chnl.getVoxelBox().any().ceilOfMaxPixel();
			if (chnlVal>max) {
				max = chnlVal;
			}
		}
		return max;
	}
	
	private Stack create2D() throws CreateException {
		try {
			Stack stack = stringRasterGenerator.generateStack();
			
			if (createShort) {
				ChannelConverter<ShortBuffer> cc = new ChannelConverterToUnsignedShort( new VoxelBoxConverterToShortScaleByType() );
					
				stack = cc.convert(stack, ConversionPolicy.CHANGE_EXISTING_CHANNEL );
			}
			
			
			if (intensityProvider!=null) {
				int maxTypeValue = createShort ? VoxelDataTypeUnsignedShort.MAX_VALUE_INT : VoxelDataTypeUnsignedByte.MAX_VALUE_INT;
				
				Stack stackIntensity = intensityProvider.create();
				double maxValue = maxValueFromStack(stackIntensity);
				double mult = (double) maxValue / maxTypeValue; 
				
				for( Channel c : stack) {
					c.getVoxelBox().any().multiplyBy(mult);
				}
			}
			
			return stack;
		} catch (OutputWriteFailedException e) {
			throw new CreateException(e);
		}
	}
	
	private Channel createExpandedChnl( Channel chnl, int zHeight ) throws CreateException {
		assert (chnl.getDimensions().getZ()==1);
		
		ImageDim sdNew = chnl.getDimensions().duplicateChangeZ(zHeight);
		
		BoundingBox bboxSrc = new BoundingBox(chnl.getDimensions().getExtnt());
		BoundingBox bboxDest = bboxSrc;
		
		Channel chnlNew = ChannelFactory.instance().createEmptyInitialised(sdNew, chnl.getVoxelDataType());
		for( int z=0; z<zHeight; z++) {
			
			// Adjust dfestination box
			bboxDest = bboxDest.shiftToZ(z);
			
			chnl.getVoxelBox().copyPixelsTo(bboxSrc, chnlNew.getVoxelBox(), bboxDest);
		}
		return chnlNew;
	}

	public StringRasterGenerator getStringRasterGenerator() {
		return stringRasterGenerator;
	}

	public void setStringRasterGenerator(StringRasterGenerator stringRasterGenerator) {
		this.stringRasterGenerator = stringRasterGenerator;
	}

	public boolean isCreateShort() {
		return createShort;
	}

	public void setCreateShort(boolean createShort) {
		this.createShort = createShort;
	}
	
	public void setInstensityProvider( StackProvider stackProvider ) {
		intensityProvider = stackProvider;
	}

	public StackProvider getIntensityProvider() {
		return intensityProvider;
	}

	public void setIntensityProvider(StackProvider intensityProvider) {
		this.intensityProvider = intensityProvider;
	}

	public StackProvider getRepeatZProvider() {
		return repeatZProvider;
	}

	public void setRepeatZProvider(StackProvider repeatZProvider) {
		this.repeatZProvider = repeatZProvider;
	}
}
