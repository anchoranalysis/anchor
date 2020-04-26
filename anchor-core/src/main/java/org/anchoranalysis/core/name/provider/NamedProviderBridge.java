package org.anchoranalysis.core.name.provider;

/*
 * #%L
 * anchor-image
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

import org.anchoranalysis.core.bridge.IObjectBridge;

/**
 * 
 * @author Owen Feehan
 *
 * @param <S> src-type
 * @param <T> destination-type
 */
public class NamedProviderBridge<S,T> implements INamedProvider<T> {

	private INamedProvider<S> srcProvider;
	private IObjectBridge<S,T,? extends Exception> bridge;
	private boolean bridgeNulls = true;
	
	public NamedProviderBridge(INamedProvider<S> srcProvider,
			IObjectBridge<S, T, ? extends Exception> bridge) {
		super();
		assert(srcProvider!=null);
		this.srcProvider = srcProvider;
		this.bridge = bridge;
	}
	
	public NamedProviderBridge(INamedProvider<S> srcProvider,
			IObjectBridge<S, T, ? extends Exception> bridge, boolean bridgeNulls ) {
		super();
		assert(srcProvider!=null);
		this.srcProvider = srcProvider;
		this.bridge = bridge;
		this.bridgeNulls = bridgeNulls;
	}

	@Override
	public T getException(String key) throws NamedProviderGetException {
		try {
			return bridge.bridgeElement( srcProvider.getNull(key) );
		} catch (Exception e) {
			throw new NamedProviderGetException(key, e);
		}
	}

	@Override
	public Set<String> keys() {
		return srcProvider.keys();
	}

	@Override
	public T getNull(String key) throws NamedProviderGetException {
		S srcVal = srcProvider.getNull(key);
		
		if (!bridgeNulls && srcVal==null) {
			// Early exit if doNotBridgeNulls is witched on
			return null;
		}
		
		try {
			return bridge.bridgeElement( srcVal );
		} catch (Exception e) {
			throw new NamedProviderGetException(key, e);
		}
	}
}
