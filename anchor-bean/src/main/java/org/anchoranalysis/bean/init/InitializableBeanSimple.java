package org.anchoranalysis.bean.init;

/*
 * #%L
 * anchor-bean
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


import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.params.IInitParams;
import org.anchoranalysis.bean.init.property.PropertyDefiner;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * 
 * @author Owen Feehan
 *
 * @param <B> bean type
 * @param <P> init-param type
 */
public abstract class InitializableBeanSimple<B,P extends BeanInitParams> extends InitializableBean<B, P> implements IInitParams<P> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean hasBeenInit = false;
	private transient LogErrorReporter logger;
	private PropertyDefiner<P> propertyDefiner;
	
	protected InitializableBeanSimple(
			PropertyInitializer<P> propertyInitializer,
			PropertyDefiner<P> propertyDefiner) {
		super(propertyInitializer);
		this.propertyDefiner = propertyDefiner;
	}
	
	// Dummy method, that children can optionally override
	@Override
	public final void init(P params, LogErrorReporter logger) throws InitException {
		this.hasBeenInit = true;
		this.logger = logger;
		assert logger!=null;
		
		onInit(params);
	}
	
	public void onInit(P params) throws InitException {

	}
	
	protected LogErrorReporter getLogger() {
		return logger;
	}

	public boolean isHasBeenInit() {
		return hasBeenInit;
	}

	@Override
	public PropertyDefiner<P> getPropertyDefiner() {
		return propertyDefiner;
	}
}
