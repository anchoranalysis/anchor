package org.anchoranalysis.image.init;

/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * A bridge that performs initialization of objects
 * 
 * @author Owen Feehan
 *
 * @param <S> source (bean) type
 * @param <T> destination type
 * @param <V> initialization-parameters type
 */
class InitBridge<
	S extends InitializableBean<?,V>,
	T,
	V extends BeanInitParams
> implements FunctionWithException<S,T,OperationFailedException> {

	private PropertyInitializer<?> pi;
	private LogErrorReporter logger;
	private FunctionWithException<S, T, CreateException> beanBridge;
	
	/**
	 * Constructor
	 * 
	 * @param pi used to initialize properties in the source-bean
	 * @param logger passed to initialized object
	 * @param beanBridge maps the bean to another type
	 */
	public InitBridge(
		PropertyInitializer<?> pi,
		LogErrorReporter logger,
		FunctionWithException<S, T, CreateException> beanBridge
	) {
		super();
		this.pi = pi;
		this.logger = logger;
		this.beanBridge = beanBridge;
	}
	
	@Override
	public T apply(S sourceObject) throws OperationFailedException {
		assert logger!=null;
		try {
			// Initialize
			sourceObject.initRecursiveWithInitializer(pi, logger);
			
			// Bridge the source to the destination
			return beanBridge.apply(sourceObject);
		} catch (InitException | CreateException e) {
			throw new OperationFailedException(e);
		}
	}
}
