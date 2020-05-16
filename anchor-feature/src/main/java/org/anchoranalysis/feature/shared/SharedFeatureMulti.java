package org.anchoranalysis.feature.shared;

import java.util.Collection;
import java.util.HashSet;

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

import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

/**
 * A group of features (of possibly heterogeneous type) made available to other features to reference
 * 
 * @author owen
 */
public class SharedFeatureMulti implements INamedProvider<Feature<FeatureInput>>, Iterable<NameValue<Feature<FeatureInput>>> {
	
	/** For searching by key */
	private NameValueSet<Feature<FeatureInput>> mapByKey;
	
	/**
	 *  For subsetting by descriptor-type
	 *  
	 *  <pre>Key=<FeatureInputDescriptor</pre>
	 *  <pre>Value=INameValue<Feature<FeatureInput>>></pre>
	 */
	private MultiMap mapByDescriptor;
	
	/** For checking if a feature already exists */
	private Set<Feature<FeatureInput>> setFeatures;

	public SharedFeatureMulti() {
		mapByKey = new NameValueSet<>();
		mapByDescriptor = new MultiValueMap();
		setFeatures = new HashSet<>();
	}
	
	/** Extracts the subset of inputs that are compatible with a particular input-type */ 
	@SuppressWarnings("unchecked")
	public <S extends FeatureInput> SharedFeatureSet<S> subsetCompatibleWith(Class<?> inputType) {
		
		 NameValueSet<Feature<S>> out = new  NameValueSet<>();
		 
		 for(FeatureInputDescriptor descriptor : (Set<FeatureInputDescriptor>) mapByDescriptor.keySet()) {
			 if (descriptor.isCompatibleWith(inputType)) {
				 transferToSet(
					(Collection<NameValue<Feature<S>>>) mapByDescriptor.get(descriptor),
					out
				);
			 }
		 }
				
		return new SharedFeatureSet<S>(out);	
	}
	
	@Override
	public String toString() {
		return mapByKey.keys().toString();
	}
	
	public boolean contains( Feature<FeatureInput> feature ) {
		return setFeatures.contains(feature);
	}
		
	public SharedFeatureMulti duplicate() {
		SharedFeatureMulti out = new SharedFeatureMulti();
		
		for (NameValue<Feature<FeatureInput>> nv : mapByKey) {
			out.addNoDuplicate(nv);
		}
		return out;
	}
	
	public void addNoDuplicate(FeatureList<FeatureInput> features) {
		
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature<FeatureInput> f : features) {
			
			addNoDuplicate(
				new SimpleNameValue<>(f.getFriendlyName(), f)
			);
		}
	}
	
	private void addNoDuplicate(NameValue<Feature<FeatureInput>> nv) {
		mapByKey.add(nv);
		mapByDescriptor.put(nv.getValue().inputDescriptor(), nv);
		setFeatures.add(nv.getValue());
	}
	
	public void removeIfExists(FeatureList<FeatureInput> features) {
		// We loop over all features in the ni, and call them all the same thing with a number
		for( Feature<FeatureInput> f : features) {
			mapByKey.removeIfExists(f);
		}
	}

	@Override
	public Iterator<NameValue<Feature<FeatureInput>>> iterator() {
		return mapByKey.iterator();
	}

	@Override
	public Feature<FeatureInput> getException(String key) throws NamedProviderGetException {
		return mapByKey.getException(key);
	}

	@Override
	public Feature<FeatureInput> getNull(String key) {
		return mapByKey.getNull(key);
	}

	@Override
	public Set<String> keys() {
		return mapByKey.keys();
	}
	
	/** Transfers from a collection of name-values into a {@link NameValueSet} */
	private static <S extends FeatureInput> void transferToSet(
		Collection<NameValue<Feature<S>>> in,
		NameValueSet<Feature<S>> out
	) {
		 for( NameValue<Feature<S>> nv : in) {
			 out.add(nv);
		 }
	}
}