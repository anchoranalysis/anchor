package org.anchoranalysis.feature.shared;

/*
 * #%L
 * anchor-feature
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


import java.util.Iterator;
import java.util.Set;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.init.FeatureInitParams;

/**
 * A group of features made available for references to other features
 * 
 * @author owen
 *
 * @param <T> feature-calc params type
 */
public class SharedFeatureSet<T extends FeatureInput> implements INamedProvider<Feature<T>>, Iterable<INameValue<Feature<T>>> {

	private NameValueSet<Feature<T>> delegate;

	// Create an empty set
	public SharedFeatureSet() {
		delegate = new NameValueSet<>();
	}
	
	@SuppressWarnings("unchecked")
	public SharedFeatureSet<FeatureInput> upcast() {
		return (SharedFeatureSet<FeatureInput>) this;
	}
	
	// TODO go through all uses of downcast, and replace with something valid
	@SuppressWarnings("unchecked")
	public <S extends T> SharedFeatureSet<S> downcast() {
		return (SharedFeatureSet<S>) this;
	}
	
	@Override
	public String toString() {
		return delegate.keys().toString();
	}
	
	// TODO inefficient, let's move towards a map
	public boolean contains( Feature<FeatureInput> feature ) {
		for( INameValue<Feature<T>> nv : this) {
			Feature<T> f = nv.getValue();
			if (feature.equals(f)) {
				return true;
			}
		}
		return false;
	}
		
	public SharedFeatureSet<T> duplicate() {
		SharedFeatureSet<T> out = new SharedFeatureSet<>();
		for( String key : delegate.keys() ) {
			Feature<T> item = delegate.getNull(key);
			out.delegate.add( new String(key), item.duplicateBean());
		}
		return out;
	}
	
	public void initRecursive( FeatureInitParams featureInitParams, LogErrorReporter logger ) throws InitException {
		for( String name : delegate.keys() ) {
			try {
				Feature<T> feat = delegate.getException(name);
				feat.initRecursive( featureInitParams, logger);
			} catch (NamedProviderGetException e) {
				throw new InitException(e.summarize());
			}
			
		}
	}
	
	public void add( SharedFeatureSet<T> other ) {
		delegate.add( other.delegate );
	}
	
	// Uses names of features
	public void addDuplicate( FeatureList<T> features ) {
		for( Feature<T> f : features ) {
			delegate.add( f.getFriendlyName(), f.duplicateBean() );
		}
	}
	
	public void addNoDuplicate( FeatureList<T> features) {
		
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature<T> f : features) {
			add( new NameValue<>(f.getFriendlyName(), f) );
		}
	}
	
	public void addDuplicate( NameValueSet<Feature<T>> src ) {
		addDuplicate( src, delegate );
	}
	
	public void addDuplicate( SharedFeatureSet<T> other ) {
		addDuplicate( other.delegate, delegate );
	}
	
	public void removeIfExists( FeatureList<T> features) {
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature<T> f : features) {
			delegate.removeIfExists(f);
		}
	}
	
	/** Returns an arbitrary-item from the set, or null if the set is empty */
	public Feature<T> arbitraryItem() {
		
		if (keys().isEmpty()) {
			return null;
		}
		
		return getNull(
			keys().iterator().next()
		);
	}
	
	private void addDuplicate( NameValueSet<Feature<T>> src, NameValueSet<Feature<T>> target ) {
		for ( String key : src.keys() ) {
			try {
				target.add( key, src.getException(key).duplicateBean() );
			} catch (NamedProviderGetException e) {
				assert false;
			}
		}
	}

	public void add(INameValue<Feature<T>> ni) {
		delegate.add(ni);
	}

	public NameValueSet<Feature<T>> getSet() {
		return delegate;
	}

	@Override
	public Iterator<INameValue<Feature<T>>> iterator() {
		return delegate.iterator();
	}

	@Override
	public Feature<T> getException(String key) throws NamedProviderGetException {
		return delegate.getException(key);
	}

	@Override
	public Feature<T> getNull(String key) {
		return delegate.getNull(key);
	}

	@Override
	public Set<String> keys() {
		return delegate.keys();
	}

	public void add(String name, Feature<T> item) {
		delegate.add(name, item);
	}
}
