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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.FunctionWithException;
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
		this( new ArrayList<>() );
	}
	
	FeatureList( Stream<Feature<T>> stream ) {
		this.list = stream.collect( Collectors.toList() );
	}
	
	// We wrap an existing list
	FeatureList( List<Feature<T>> list ) {
		this.list = list;
	}
	
	public void initRecursive( FeatureInitParams featureInitParams, LogErrorReporter logger ) throws InitException {
		for( Feature<T> f : list) {
			f.initRecursive(featureInitParams, logger);
		}
	}
	
	/**
	 * Creates a new feature-list where each feature is the result of applying a map-function to an existing feature
	 * 
	 * @param <S> input-type of feature to be created as result of mapping
	 * @param <E> exception that can be thrown during mapping
	 * @param mapFunc function to perform the mapping of each item
	 * @return a newly created feature-list (with the same number of items) containing the mapped features.
	 * @throws E if the mapping-function throws this exception
	 */
	public <S extends FeatureInput, E extends Throwable> FeatureList<S> map(FunctionWithException<Feature<T>,Feature<S>,E> mapFunc) throws E {
		FeatureList<S> out = new FeatureList<>();
		for(Feature<T> feature : list) {
			out.add(
				mapFunc.apply(feature)
			);
		}
		return out;
	}
	
	
	/**
	 * Performs a {@link filter} and then a {@link map}
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 *
 	 * @param <S> input-type of feature to be created as result of mapping
	 * @param <E> exception that can be thrown during mapping
	 * @param mapFunc performs mapping
	 * @param predicate iff true object is included, otherwise excluded
	 * @return a newly created feature-list, a filtered version of all features, then mapped
	 * @throws E if the mapping-function throws this exception
	 */
	public <S extends FeatureInput, E extends Throwable> FeatureList<S> filterAndMap(Predicate<Feature<T>> predicate, FunctionWithException<Feature<T>,Feature<S>,E> mapFunc) throws E {
		FeatureList<S> out = new FeatureList<>();
		for(Feature<T> feature : list) {
			if (predicate.test(feature)) {
				out.add(
					mapFunc.apply(feature)
				);
			}
		}
		return out;
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
	
	/**
	 * Creates a new feature-list sorted in a particular order.
	 * 
	 * @return
	 */
	public FeatureList<T> sort( Comparator<Feature<T>> comparator ) {
		// Creates a duplicate list, and sorts the items in place.
		FeatureList<T> out = new FeatureList<>( this.list.stream() );
		Collections.sort(out.asList(), comparator);
		return out;
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

	public Feature<T> get(int index) {
		return list.get(index);
	}

	public boolean addAll(FeatureList<T> other) {
		return list.addAll( other.asList() );
	}

	public void clear() {
		list.clear();
	}
}
