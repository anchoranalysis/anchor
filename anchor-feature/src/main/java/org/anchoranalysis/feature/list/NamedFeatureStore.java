package org.anchoranalysis.feature.list;

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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;

public class NamedFeatureStore<T extends FeatureInput> implements Iterable<NamedBean<Feature<T>>> {
	
	private List<NamedBean<Feature<T>>> list = new ArrayList<>();
	private Map<String,Integer> mapIndex = new HashMap<String,Integer>();
	
	// 
	
	/**
	 * Adds a named-feature to the store. The customName() of the feature is replaced with the name.
	 * 
	 * @param name name of the feature
	 * @param feature the feature to add (whose customName will be overridden with the name)
	 */
	public void add( String name, Feature<T> feature ) {
		mapIndex.put( name, list.size() );
		feature.setCustomName(name);
		list.add( new NamedBean<Feature<T>>(name,feature) );
	}
	
	public int getIndex( String name ) throws GetOperationFailedException {
		Integer index = mapIndex.get(name);
		if (index==null) {
			throw new GetOperationFailedException( String.format("The key '%s' is not found in the featureStore", name ) );
		}
		return index;
	}
	
	public NamedBean<Feature<T>> get( int index ) {
		return list.get(index);
	}
	
	public FeatureNameList createFeatureNames() {
		FeatureNameList out = new FeatureNameList();
		addFeatureNamesToCollection( out.asList() );
		return out;
	}
	
	public void addFeatureNamesToCollection( Collection<String> listOut ) {
		
		for( NamedBean<Feature<T>> item : list ) {
			listOut.add( item.getName() );
		}
	}
	
	public NamedBean<Feature<T>> get( String name ) {
		int index = mapIndex.get(name);
		return list.get(index);
	}
	
	@Override
	public Iterator<NamedBean<Feature<T>>> iterator() {
		return list.iterator();
	}
	

	
	public NamedFeatureStore<T> deepCopy() {
		NamedFeatureStore<T> out = new NamedFeatureStore<>();
		for( NamedBean<Feature<T>> ni : list ) {
			NamedBean<Feature<T>> niDup = ni.duplicateBean();
			out.add( niDup.getName(), niDup.getValue() );
		}
		return out;
	}
	
	public FeatureList<T> listFeatures() {
		FeatureList<T> out = new FeatureList<>();
		for( NamedBean<Feature<T>> ni : list ) {
			out.add( ni.getValue() );
		}
		return out;
	}

	
	public FeatureList<T> listFeaturesSubset(int start, int size) {
		FeatureList<T> out = new FeatureList<>();
		int end = start + size;
		for( int i=start; i<end; i++ ) {
			out.add(
				list.get(i).getValue()
			);
		}
		return out;
	}
	

	public void copyTo( NameValueSet<Feature<T>> out ) {
		for( NamedBean<Feature<T>> ni : list ) {
			out.add( ni.getName(), ni.getValue() );
		}
	}
	
	public void copyToDuplicate( NameValueSet<Feature<T>> out ) {
		for( NamedBean<Feature<T>> ni : list ) {
			out.add( ni.getName(), ni.getValue().duplicateBean() );
		}
	}

	public int size() {
		return list.size();
	}
}