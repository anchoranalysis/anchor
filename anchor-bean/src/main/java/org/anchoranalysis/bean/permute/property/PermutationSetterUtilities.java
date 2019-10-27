package org.anchoranalysis.bean.permute.property;

/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.bean.permute.setter.PermutationSetterSingle;
import org.anchoranalysis.core.error.CreateException;

class PermutationSetterUtilities {

	// Searches through a list of property fields to find one that matches the propertyName
	public static PermutationSetter createForSingle( AnchorBean<?> parentBean, String propertyPath ) throws CreateException {
				
		// Get all the components of propertyPath
		String[] tokens = propertyPath.split("\\.");
		
		if (tokens.length==1) {
			// Simple case when there is a direct property
			Field finalField = findMatchingField( parentBean.getOrCreateBeanFields(), propertyPath );
			return new PermutationSetterSingle(finalField);
		} else {
			return createWithIntermediateProperties( parentBean, tokens );
		}
	}
	
	private static Field findMatchingField( List<Field> list, String propertyName ) throws CreateException {
		for( Field f : list ) {
			if (f.getName().equals(propertyName)) {
				return f;
			}
		}
		throw new CreateException( String.format("Cannot find matching field for property '%s'", propertyName) );
	}
	
	private static PermutationSetter createWithIntermediateProperties( AnchorBean<?> parentBean, String[] tokens ) throws CreateException {
		PermutationSetterSingle setter = new PermutationSetterSingle();
		
		try {
			AnchorBean<?> currentBean = parentBean;
			List<Field> currentList = currentBean.getOrCreateBeanFields();
			
			int intermediateFields = tokens.length - 1;
			
			// We add each child along the way, keep the final field property
			for( int i=0; i<intermediateFields; i++ ) {
				
				String currentPropertyName = tokens[i];
				
				// Find a matching property for the first child
				Field matchedField = findMatchingField(currentList, currentPropertyName);
				
				setter.addField(matchedField);
				
				AnchorBean<?> childBean = (AnchorBean<?>) matchedField.get(currentBean);
				currentBean = childBean;
				currentList = currentBean.getOrCreateBeanFields();
			}
			
			// Find a matching property for the final setter
			Field matchedField = findMatchingField(currentList, tokens[intermediateFields] );
			
			setter.addField(matchedField);
			
			return setter;		
			
		} catch (IllegalAccessException e) {
			throw new CreateException(e);
		}
	}
}
