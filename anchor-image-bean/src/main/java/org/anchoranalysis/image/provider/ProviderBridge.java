package org.anchoranalysis.image.provider;

import org.anchoranalysis.bean.Provider;

/*
 * #%L
 * anchor-image-bean
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

import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;

public class ProviderBridge<SrcType extends InitializableBean<?,InitParamType> & Provider<DestType>, DestType, InitParamType extends BeanInitParams> implements IObjectBridge<SrcType, DestType> {

	private PropertyInitializer<?> pi;
	private LogErrorReporter logger;
	
	public ProviderBridge(PropertyInitializer<?> pi, LogErrorReporter logger ) {
		super();
		this.pi = pi;
		this.logger = logger;
	}

	@Override
	public DestType bridgeElement(SrcType sourceObject)
			throws GetOperationFailedException {
		try {
			sourceObject.initRecursiveWithInitializer(pi, logger);
			return sourceObject.create();
		} catch (CreateException e) {
			throw new GetOperationFailedException(e);
		} catch (InitException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
}