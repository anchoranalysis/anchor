package org.anchoranalysis.core.name.provider;

import java.util.Optional;

/*
 * #%L
 * anchor-core
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


import java.util.Set;

import org.anchoranalysis.core.error.OperationFailedException;

public interface NamedProvider<T> {

	/**
	 * Retrieves the item if it exists, or throws an exception if it doesn't exist.
	 */
	default T getException(String key) throws NamedProviderGetException {
		return getOptional(key).orElseThrow( ()->
			NamedProviderGetException.nonExistingItem(key)
		);
	}
	
	/** 
	 * Retrieves the item if it exists, or returns empty() if it doesn't exist.
	 * <p>
	 * Note that a 'key' might still throw an exception for another reason (but never because a particular key is absent).
	 */
	Optional<T> getOptional( String key ) throws NamedProviderGetException;
	
	/** 
	 * Returns a set of keys associated with the provider.
	 * <p>
	 * There's no guarantee that it refers to all valid keys.
	 **/
	Set<String> keys();
	
	/**
	 * Gets one element of the provider (arbitrarily)
	 * 
	 * @return one of the elements of the array (arbitrary which one)
	 * @throws NamedProviderGetException if the array has no elements
	 */
	default T getArbitraryElement() throws OperationFailedException {
		
		Set<String> keys = keys();
		
		if (keys.isEmpty()) {
			throw new OperationFailedException("Provider is empty");
		}
		try {
			return getOptional( keys().iterator().next() ).get();	// NOSONAR
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
	}
}
