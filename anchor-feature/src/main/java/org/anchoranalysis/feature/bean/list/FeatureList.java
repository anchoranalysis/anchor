package org.anchoranalysis.feature.bean.list;

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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * 
 * @author owen
 *
 * @param <T> input type of features contained in the list
 */
public class FeatureList<T extends FeatureInput> extends AnchorBean<FeatureList<T>> implements Iterable<Feature<T>> {
	
	// START BEAN PARAMETERS
	@BeanField
	private List<Feature<T>> list;
	// END BEAN PARAMETERS
	
	public FeatureList() {
		super();
		this.list = new ArrayList<>();
	}
	
	// We wrap an existing list
	public FeatureList( List<Feature<T>> list ) {
		this.list = list;
	}
	
	public FeatureList( Iterable<Feature<T>> iterFeatures ) {
		this.list = StreamSupport
				.stream(iterFeatures.spliterator(), false)
				.collect(Collectors.toList());
	}

	
	public FeatureList( Feature<T> f ) {
		this();
		assert(f!=null);
		this.list.add(f);
	}
	
	public void initRecursive( FeatureInitParams featureInitParams, LogErrorReporter logErrorReporter ) throws InitException {
		for( Feature<T> f : list) {
			f.initRecursive(featureInitParams, logErrorReporter);
		}
	}
	
	@SuppressWarnings("unchecked")
	public FeatureList<FeatureInput> upcast() {
		return (FeatureList<FeatureInput>) this;
	}
	
	
	@SuppressWarnings("unchecked")
	public <S extends FeatureInput> FeatureList<S> downcast() {
		return (FeatureList<S>) this;
	}
	
	public FeatureNameList createNames() {
		FeatureNameList out = new FeatureNameList();
		for( Feature<T> f : list ) {
			out.add( f.getFriendlyName() );
		}
		return out;
	}
	
	// Copies all features using their CustomName to a set
	public void copyToCustomName( NameValueSet<Feature<T>> set, boolean duplicate ) {
		for( Feature<T> f : list ) {
			
			Feature<T> fToAdd = duplicate ? f.duplicateBean() : f;
			assert(fToAdd!=null);
			set.add( f.getCustomName(), fToAdd );
		}
	}
	
	public int findIndex( String customName ) {
		for( int i=0; i<list.size(); i++ ) {
			Feature<T> f = list.get(i);
			if (f.getCustomName().equals(customName)) {
				return i;
			}
		}
		return -1;
	}
	
	// Delegate Methods
	public boolean add(Feature<T> f) {
		assert(f!=null);
		return list.add(f);
	}
	
	public void addWithCustomName( Feature<T> f, String customName ) {
		f.setCustomName( customName );
		add(f);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<Feature<T>> iterator() {
		return list.iterator();
	}

	public int size() {
		return list.size();
	}

	public List<Feature<T>> asList() {
		return list;
	}

	@Override
	public String getBeanDscr() {
		return String.format(
			"%s with %d items",
			super.getBeanDscr(),
			list.size()
		);
	}

	public Feature<T> get(int arg0) {
		return list.get(arg0);
	}

	public boolean addAll(FeatureList<T> other) {
		return list.addAll( other.asList() );
	}
	
	public boolean addAll(Collection<? extends Feature<T>> c) {
		return list.addAll(c);
	}

	public void clear() {
		list.clear();
	}
}
