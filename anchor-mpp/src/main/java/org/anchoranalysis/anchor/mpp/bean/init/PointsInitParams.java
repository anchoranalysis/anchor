package org.anchoranalysis.anchor.mpp.bean.init;

import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;

/*
 * #%L
 * anchor-mpp
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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.init.PopulateStoreFromDefine;

public class PointsInitParams extends BeanInitParams {
	
	// START: InitParams
	private ImageInitParams soImage;
	// END: InitParams
	
	// START: Stores
	private NamedProviderStore<PointsFitter> storePointsFitter;
	// END: Stores
	
	private PointsInitParams( ImageInitParams soImage, SharedObjects so ) {
		super();
		this.soImage = soImage;
		
		storePointsFitter = so.getOrCreate(PointsFitter.class);
	}
	
	public static PointsInitParams create( ImageInitParams soImage, SharedObjects so ) {
		return new PointsInitParams(soImage, so);
	}
	
	public NamedProviderStore<PointsFitter> getPointsFitterSet() {
		return storePointsFitter;
	}

	public void populate( PropertyInitializer<?> pi, Define define, LogErrorReporter logger ) throws OperationFailedException {
		
		PopulateStoreFromDefine<PointsInitParams> populater = new PopulateStoreFromDefine<>(define, pi, logger);
		populater.copyInit(PointsFitter.class, storePointsFitter);
	}

	public ImageInitParams getImage() {
		return soImage;
	}
}
