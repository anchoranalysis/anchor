package org.anchoranalysis.image.bean.provider.stack;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.arrangeraster.RasterArranger;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterBean;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactoryShort;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;

// Creates new stacks that tile each provider
public class StackProviderArrangeRaster extends StackProvider {

	// START BEAN
	@BeanField
	private ArrangeRasterBean arrangeRaster;
	
	@BeanField
	private boolean forceRGB = false;	// Makes sure every stack is converted into 3 channels
	
	@BeanField
	private List<StackProvider> list = new ArrayList<>();
	
	@BeanField
	private boolean createShort = false;
	// END BEAN
	
	private void copyFirstChnlUntil3( Stack stack ) {
		while (stack.getNumChnl()<3) {
			try {
				stack.addChnl( stack.getChnl(0).duplicate() );
			} catch (IncorrectImageSizeException e) {
				assert false;
			}
		}		
	}
	
	@Override
	public Stack create() throws CreateException {
		
		if (list.size()==0) {
			throw new CreateException("At least one stack must be present in list");
		}
		
		List<RGBStack> rasterList = new ArrayList<>(); 
		for (StackProvider provider : list) {
			
			Stack stack = provider.create();
			
			if (forceRGB) {
				copyFirstChnlUntil3( stack );
			}
			rasterList.add( new RGBStack(stack) );
		}

		RasterArranger rasterArranger = new RasterArranger();
		try {
			rasterArranger.init(arrangeRaster,rasterList);
		} catch (InitException e) {
			throw new CreateException(e);
		}
		
		ChannelFactorySingleType factory = createShort ? new ChannelFactoryShort() : new ChannelFactoryByte();

		return rasterArranger.createStack(rasterList,factory).asStack();
	}

	public List<StackProvider> getList() {
		return list;
	}

	public void setList(List<StackProvider> list) {
		this.list = list;
	}
	
	public ArrangeRasterBean getArrangeRaster() {
		return arrangeRaster;
	}

	public void setArrangeRaster(ArrangeRasterBean arrangeRaster) {
		this.arrangeRaster = arrangeRaster;
	}

	public boolean isForceRGB() {
		return forceRGB;
	}

	public void setForceRGB(boolean forceRGB) {
		this.forceRGB = forceRGB;
	}

	public boolean isCreateShort() {
		return createShort;
	}

	public void setCreateShort(boolean createShort) {
		this.createShort = createShort;
	}


}
