package org.anchoranalysis.bean.init;

import org.anchoranalysis.bean.AnchorBean;

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
import org.anchoranalysis.bean.init.params.ParamsInitializer;
import org.anchoranalysis.bean.init.property.PropertyDefiner;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

import lombok.Getter;

/**
 * A bean that must be initialized with some parameters before being used.
 * 
 * @author Owen Feehan
 *
 * @param <B> bean-family type
 * @param <P> initialization-parameters type
 */
public abstract class InitializableBean<B,P extends BeanInitParams> extends AnchorBean<B> implements ParamsInitializer<P> {

	private final PropertyInitializer<P> propertyInitializer; 
	
	@Getter
	private final PropertyDefiner propertyDefiner;
	
	/** Has the bean been initialized yet? */
	@Getter
	private boolean initialized = false;
	
	/** the logger */
	private Logger logger;
		
	protected InitializableBean(PropertyInitializer<P> propertyInitializer, PropertyDefiner propertyDefiner) {
		this.propertyInitializer = propertyInitializer;
		this.propertyDefiner = propertyDefiner;
	}
	
	// Dummy method, that children can optionally override
	@Override
	public void init(P params, Logger logger) throws InitException {
		this.initialized = true;
		this.logger = logger;
		onInit(params);
	}
	
	/** Called after initialization. An empty impelmentation is provided, to be overridden as needed in the sub-classes. */
	public void onInit(P params) throws InitException {
		// Empty implementation to be replaced in sub-classes
	}
		
	/**
	 * 
	 * Initializes the bean and recursively all contained beans (who have compatible initialization requirements)
	 * <p>
	 * The correct initialization parameters are found for each bean via the {@code pi} parameter;
	 * 
	 * @param  pi the property-initializer to use
	 * @param  logger logger
	 * @throws InitException if the initialization fails, including if correct initialization-parameters cannot be derived
	 */
	public void initRecursiveWithInitializer( PropertyInitializer<?> pi, Logger logger ) throws InitException {
		HelperInit.initRecursive(this, pi, logger);
	}
	
	/**
	 * Inits this object, and all children objects, so long as they have P
	 * Once a Bean doesn't have P, the children are not evaluated
	 * 
	 * @param params init-params
	 * @param logger logger
	 * @throws InitException if the initialization fails
	 */
	public void initRecursive( P params, Logger logger ) throws InitException {
		propertyInitializer.setParam(params);
		HelperInit.initRecursive( this, propertyInitializer, logger );
	}

	protected PropertyInitializer<P> getPropertyInitializer() {
		return propertyInitializer;
	}
	
	/** The logger */
	protected Logger getLogger() {
		return logger;
	}
}
