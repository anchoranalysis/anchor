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


import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyDefiner;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * A bean that must be initialized with some parameters before being used
 * 
 * @author Owen Feehan
 *
 * @param <B> bean-type
 * @param <P> init-param type
 */
public abstract class InitializableBean<B,P extends BeanInitParams> extends AnchorBean<B> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient PropertyInitializer<P> propertyInitializer; 
	
	protected InitializableBean(PropertyInitializer<P> propertyInitializer) {
		super();
		this.propertyInitializer = propertyInitializer;
	}
	
	/**
	 * 
	 * Initializes the bean
	 * 
	 * @param pi    	the property-initializer to use
	 * @param logger 	logger
	 * @throws InitException if the initialization fails
	 */
	public void initRecursiveWithInitializer( PropertyInitializer<?> pi, LogErrorReporter logger ) throws InitException {
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
	public void initRecursive( P params, LogErrorReporter logger ) throws InitException {
		propertyInitializer.setParam(params);
		HelperInit.initRecursive( this, propertyInitializer, logger );
	}
	
	public abstract PropertyDefiner<P> getPropertyDefiner();

	protected PropertyInitializer<P> getPropertyInitializer() {
		return propertyInitializer;
	}
}
