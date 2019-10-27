package org.anchoranalysis.bean.permute;

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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.bean.permute.property.PermuteProperty;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;

/**
 * Applies a PermuteProperty to a bean to create new duplicated beans
 *   each with a permutation
 *   
 * @author Owen Feehan
 *
 */
public class ApplyPermutations<T extends AnchorBean<T>> {

	@FunctionalInterface
	public interface INameGetter<S> {
		String getName( S obj );
	}
	
	@FunctionalInterface
	public interface INameSetter<S> {
		void setName( S obj, String name );
	}
	
	private INameGetter<T> nameGetter;
	private INameSetter<T> nameSetter;
	
	public ApplyPermutations(INameGetter<T> nameGetter, INameSetter<T> nameSetter) {
		super();
		this.nameGetter = nameGetter;
		this.nameSetter = nameSetter;
	}

	/**
	 * Takes a list of beans, and creates a permuted version, updating the custom names 
	 * 
	 * If both nameGetter and nameSetter are non-NULL, then a .XXX is appended to the name
	 *  of the duplicated bean where XXX identifies the permutation.
	 * 
	 * @param listIn		FeatureList in
	 * @param pp			Which property to permute, and all the values for the permutation
	 * @param setter		for setting the permutation onto the property
	 * @param <T>			bean-type
	 * @return a list containing the permuted beans
	 * @throws CreateException if something goes wrong 
	 */
	public <S> List<T> applyPermutationsToCreateDuplicates(
		List<T> listIn,
		PermuteProperty<S> pp,
		PermutationSetter setter
	) throws CreateException {
		
		List<T> listOut = new ArrayList<>();
		
		try {
			for( T featIn : listIn ) {
				Iterator<S> vals = pp.propertyValues();	
				
				while( vals.hasNext() ) {
					S propVal = vals.next();
					assert propVal!=null;
					
					T featDup = featIn.duplicateBean();
					assert featDup!=null;
					
					setter.setPermutation(featDup, propVal);
					listOut.add(featDup);
					
					maybeSetNewName(featDup, pp, propVal);
				}
			}
			
			return listOut;
			
		} catch (BeanDuplicateException | IllegalArgumentException | SetOperationFailedException e) {
			throw new CreateException(e);
		}
	}
	
	private <S> void maybeSetNewName(T feature, PermuteProperty<S> pp, S propVal) throws CreateException {
		
		// If we have both a nameGetter and nameSetter we give a sensible name to our output
		// Otherwise we give up
		if (nameGetter==null || nameSetter==null) {
			return;
		}
			
		try {
			String nameNew = sensibleName(feature, pp, propVal);
			nameSetter.setName(feature, nameNew);
		} catch (OperationFailedException e) {
			throw new CreateException(
				String.format("Cannot create new name for permutation %s", propVal ),
				e
			);
		}
	}
	
	private <S> String sensibleName(T feature, PermuteProperty<S> pp, S propVal) throws OperationFailedException {
		String appendStr = pp.nameForPropValue(propVal);
		
		String exstName = nameGetter.getName(feature);
		if (!exstName.isEmpty()) {
			// We update the custom name
			return String.format("%s.%s", exstName, appendStr );
		} else {
			// When there's no existing custom name (first PermuteProperty)
			return appendStr;
		}		
	}
}
