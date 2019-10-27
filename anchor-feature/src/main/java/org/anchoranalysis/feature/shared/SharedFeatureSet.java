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
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.init.FeatureInitParams;

public class SharedFeatureSet implements INamedProvider<Feature>, Iterable<INameValue<Feature>> {

	private NameValueSet<Feature> delegate;

	// Create an empty set
	public SharedFeatureSet() {
		delegate = new NameValueSet<>();
	}
	
	@Override
	public String toString() {
		return delegate.keys().toString();
	}
	
	// TODO inefficient, let's move towards a map
	public boolean contains( Feature feature ) {
		for( INameValue<Feature> nv : this) {
			Feature f = nv.getValue();
			if (feature.equals(f)) {
				return true;
			}
		}
		return false;
	}
		
	public SharedFeatureSet duplicate() {
		SharedFeatureSet out = new SharedFeatureSet();
		for( String key : delegate.keys() ) {
			Feature item = delegate.getNull(key);
			out.delegate.add( new String(key), item.duplicateBean());
		}
		return out;
	}
	
	public void initRecursive( FeatureInitParams featureInitParams, LogErrorReporter logger ) throws InitException {
		for( String name : delegate.keys() ) {
			try {
				Feature feat = delegate.getException(name);
				feat.initRecursive( featureInitParams, logger);
			} catch (GetOperationFailedException e) {
				throw new InitException(e);
			}
			
		}
	}
	
	public void add( SharedFeatureSet other ) {
		delegate.add( other.delegate );
	}
	
	// Uses names of features
	public void addDuplicate( FeatureList features ) {
		for( Feature f : features ) {
			delegate.add( f.getFriendlyName(), f.duplicateBean() );
		}
	}
	
	public void addNoDuplicate( FeatureList features) {
		
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature f : features) {
			add( new NameValue<>(f.getFriendlyName(), f) );
		}
	}
	
	public void addDuplicate( NameValueSet<Feature> src ) {
		addDuplicate( src, delegate );
	}
	
	public void addDuplicate( SharedFeatureSet other ) {
		addDuplicate( other.delegate, delegate );
	}
	
	public void removeIfExists( FeatureList features) {
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature f : features) {
			delegate.removeIfExists(f);
		}
	}
	
	private static void addDuplicate( NameValueSet<Feature> src, NameValueSet<Feature> target ) {
		for ( String key : src.keys() ) {
			try {
				target.add( key, src.getException(key).duplicateBean() );
			} catch (GetOperationFailedException e) {
				assert false;
			}
		}
	}

	public void add(INameValue<Feature> ni) {
		delegate.add(ni);
	}

	public NameValueSet<Feature> getSet() {
		return delegate;
	}

	@Override
	public Iterator<INameValue<Feature>> iterator() {
		return delegate.iterator();
	}

	@Override
	public Feature getException(String key) throws GetOperationFailedException {
		return delegate.getException(key);
	}

	@Override
	public Feature getNull(String key) {
		return delegate.getNull(key);
	}

	@Override
	public Set<String> keys() {
		return delegate.keys();
	}

	public void add(String name, Feature item) {
		delegate.add(name, item);
	}
}
