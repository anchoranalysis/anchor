package org.anchoranalysis.bean.store;

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


import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.name.store.NamedProviderStore;

public class BeanStoreAdder {

	private BeanStoreAdder() {
		
	}
		
	/**
	 * Adds an item a container (using a bridge) and explicitly specifying
	 *   a new name
	 *
	 * @param <S> item-type as input
	 * @param <D> item-type in the container
	 * @param name how the item will be named in the container
	 * @param item item to be added
	 * @param cntr container the item is added to (destination)
	 * @param bridge bridge applied to item so it matches the type of cntr
	 * @throws OperationFailedException if the operation cannot be completed
	 */
	public static <S extends AnchorBean<?>, D> void add(
		String name,
		S item,
		NamedProviderStore<D> cntr,
		FunctionWithException<S,D,OperationFailedException> bridge
	) throws OperationFailedException {
		cntr.add(
			name,
			new CurriedObjectBridge<>(bridge, item )
		);
	}

	
	
	/**
	 * Adds a list of named-items a container (using a bridge) using the same
	 *   names in the destination container
	 *   
	 *  N.B. Only the Item is added to the container (not the named-item)
	 * 
	 * @param <S> item-type as input
	 * @param <D> item-type in the container
	 * @param listItem list of named-items (source)
	 * @param cntr container the item is added to (destination)
	 * @param bridge bridge applied to item so it matches the type of cntr
	 * @throws OperationFailedException if the operation cannot be completed
	 */
	public static <S extends AnchorBean<?>, D> void addPreserveName(
		List<NamedBean<S>> listItem,
		NamedProviderStore<D> cntr,
		FunctionWithException<S,D,OperationFailedException> bridge
	) throws OperationFailedException {
		
		for ( NamedBean<S> ni : listItem) {
			NamedBean<S> niDup = ni.duplicateBean();
			add(niDup.getName(), niDup.getValue(), cntr, bridge);
		}
	}
	
	
	
	/**
	 * Adds a list of named-items to a container (using a bridge) using the same
	 *   names in the destination container
	 *   
	 *  N.B. The entire NamedItem object is added to the container, and thus
	 *   the name is also "embedded" into the object itself inside the container
	 * 
	 * @param <S> item-type as input
	 * @param <D> item-type in the container
	 * @param listItem list of named-items (source)
	 * @param cntr container the item is added to (destination)
	 * @param bridge bridge applied to item so it matches the type of cntr
	 * @throws OperationFailedException if the operation cannot be completed
	 */
	public static <S extends AnchorBean<?>, D> void addPreserveNameEmbedded(
		List<NamedBean<S>> listItem,
		NamedProviderStore<D> cntr,
		FunctionWithException<NamedBean<S>, D, OperationFailedException> bridge
	) throws OperationFailedException {
		
		for ( NamedBean<S> ni : listItem) {
			NamedBean<S> niDup = ni.duplicateBean();
			add(niDup.getName(), niDup, cntr, bridge);
		}
	}
}
