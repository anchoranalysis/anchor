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

import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.store.BeanStoreAdder;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;

/**
 * Helps populates a {@link NamedProviderStore} from the contents of a {@link Define}.
 *  
 * <p>Objects can be added directly (no initialization) or with initialization.</p>
 * 
 * @author Owen Feehan
 *
 * @param <V> initialization-parameters for provider
 */
public class PopulateStoreFromDefine<V extends BeanInitParams> {
	
	private PropertyInitializer<?> pi;
	private Define define;
	private LogErrorReporter logger;
	
	/***
	 * Constructor
	 *
	 * @param define source for objects
	 * @param pi used to intitialize the properties of objects added with initialization
	 * @param logger passed to objects added with initialization
	 */
	public PopulateStoreFromDefine(Define define, PropertyInitializer<?> pi, LogErrorReporter logger) {
		super();
		this.pi = pi;
		this.define = define;
		this.logger = logger;
	}

	/**
	 * Copies objects of a particular class from the define WITHOUT doing any initialization
	 * 
	 * @param <S> type of objects
	 * @param defineClss class to identify objects in Define
	 * @param destination where to copy to
	 * @throws OperationFailedException
	 */
	public <S extends AnchorBean<S>> void copyWithoutInit( Class<?> defineClss, NamedProviderStore<S> destination ) throws OperationFailedException {
		
		List<NamedBean<S>> listItem = define.getList(defineClss); 
		BeanStoreAdder.addPreserveName(
			listItem,
			destination,
			name->name	// Named unchanged
		);
	}
	
	
	/**
	 * Copies objects of a particular class from the define AND initializes
	 * 
	 * @param <S> type of objects
	 * @param defineClss class to identify objects in Define
	 * @param destination where to copy to
	 * @throws OperationFailedException
	 */
	public <S extends InitializableBean<S,V>> void copyInit(
		Class<?> defineClss,
		NamedProviderStore<S> destination
	) throws OperationFailedException {
		
		List<NamedBean<S>> listItem = define.getList(defineClss); 
		BeanStoreAdder.addPreserveName(
			listItem,
			destination,
			new InitBridge<>(
				pi,
				logger,
				s->s	// Initializes and returns the input
			)		
		);
	}

	
	/**
	 * Copies objects of a particular class from the define AND initializes as a provider
	 * 
	 * <p>Specifically, each object will be lazily initialized once when first retrieved from the store.</p>
	 * 
	 * @param <S> type of provider-objects
	 * @param <T> type of objects created by the provider
	 * @param defineClss class to identify objects in Define
	 * @param destination where to copy to
	 * @return the provider-bridge created for the initialization
	 * @throws OperationFailedException
	 */
	public <S extends InitializableBean<?,V> & Provider<T>,T> FunctionWithException<S,T,OperationFailedException> copyProvider(
		Class<?> defineClss,
		NamedProviderStore<T> destination
	) throws OperationFailedException {
		
		List<NamedBean<S>> listItem = define.getList(defineClss);
		
		InitBridge<S,T,V> bridge = new InitBridge<S,T,V>(
			pi,
			logger,
			s->s.create()		// Initializes and then gets whats provided
		);		
		
		BeanStoreAdder.addPreserveName(
			listItem,
			destination,
			bridge
		);
		
		return bridge;
	}
}
