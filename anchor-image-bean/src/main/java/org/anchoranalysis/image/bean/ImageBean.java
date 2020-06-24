package org.anchoranalysis.image.bean;

import java.util.Arrays;
import java.util.List;

/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.bean.init.InitializableBeanSimple;
import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;

public abstract class ImageBean<T> extends InitializableBeanSimple<T,ImageInitParams> {

	private transient ImageInitParams so;
	
	protected ImageBean() {
		super(
			new PropertyInitializer<>( ImageInitParams.class, paramExtracters() ),
			new SimplePropertyDefiner<ImageInitParams>(ImageInitParams.class)
		);
	}
	
	@Override
	public void onInit(ImageInitParams so) throws InitException {
		super.onInit(so);
		this.so = so;
	}

	public ImageInitParams getSharedObjects() {
		return so;
	}
	
	private static List<ExtractFromParam<ImageInitParams,?>> paramExtracters() {
		return Arrays.asList(
			new ExtractFromParam<>(
				KeyValueParamsInitParams.class,
				ImageInitParams::getFeature
			),				
			new ExtractFromParam<>(
				KeyValueParamsInitParams.class,
				ImageInitParams::getParams
			)
		);
	}
}
