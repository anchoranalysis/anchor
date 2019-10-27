package org.anchoranalysis.bean.permute.setter;

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


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.index.SetOperationFailedException;

public class PermutationSetterSingle extends PermutationSetter {

	/**
	 * Intermediate properties that must be traversed before getting to the final field
	 */
	private List<Field> listFields = new ArrayList<>();
	
	public PermutationSetterSingle() {
		// Nothing is initialized, one must do it themselves
	}
	
	public PermutationSetterSingle(Field field) {
		super();
		addField(field);
	}
	
	public void addField( Field field ) {
		this.listFields.add( field );
	}

	@Override
	public void setPermutation( AnchorBean<?> bean, Object val ) throws SetOperationFailedException {
		
		try {
			int numIntermediate = listFields.size() - 1;
			
			// We iterate through all the intermediate fields
			AnchorBean<?> currentBean = bean;
			for( int i=0; i<numIntermediate; i++ ) {
				Field p = listFields.get(i);
				currentBean = next(p, currentBean);
			}
			
			// Now do the final field, where we actually set the value
			Field finalField = listFields.get(numIntermediate);
			
			Object valMaybeConverted = maybeConvert( val, finalField.getType() );
			finalField.set(currentBean, valMaybeConverted);
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SetOperationFailedException(e);
		}
	}
	
	/**
	 * We hardcore special cases, where the object conversion occurs.
	 * 
	 *  1. From any object to a string.
	 * 
	 * @return
	 */
	private static Object maybeConvert( Object val, Class<?> targetType ) {
		
		if (targetType.equals(String.class)) {
			return val.toString();
		}
		return val;
	}
	
	private static AnchorBean<?> next( Field field, AnchorBean<?> in ) throws IllegalArgumentException, IllegalAccessException {
		return (AnchorBean<?>) field.get(in);
	}
}
