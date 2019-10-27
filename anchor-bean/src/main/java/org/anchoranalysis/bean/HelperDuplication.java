package org.anchoranalysis.bean;

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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;

class HelperDuplication {
	
	private HelperDuplication() {
		
	}
	
	private static Object duplicateCollection( Collection<?> collection, String propertyName, AnchorBean<?> parentBean ) {
		
		 ArrayList<Object> collectionNew = new ArrayList<>();
		 
		 for( Object o : collection ) {
			 collectionNew.add( duplicatePropertyValue(o, propertyName, false, parentBean) );
		 }
		 
		 return collectionNew;
	}
	
	@SuppressWarnings("rawtypes")
	private static Object duplicatePropertyValue( Object propertyValue, String propertyName, boolean optional, AnchorBean<?> parentBean ) {

		if (propertyValue==null) {
			if (optional) {
				return null;
			} else {
				throw new BeanDuplicateException( String.format("Property '%s' of '%s' is null, but non-optional",propertyName,parentBean.getBeanName()) );
			}
		}

		// TODO upgrade commons lang3 and use the isPrimitiveOrWrapper method to replace several cases below
		if (propertyValue instanceof StringSet) {
			StringSet propertyValueCast = (StringSet) propertyValue;
       	 	return propertyValueCast.duplicateBean();
		} else if (propertyValue instanceof AnchorBean) {
        	 // Our first priority is to duplicate a bean if we can, as it is possible for a Bean to be a Collection as well, and it's better
        	 //  to use the Bean's normal duplication method
			AnchorBean propertyValueCast = (AnchorBean) propertyValue;
        	 return propertyValueCast.duplicateBean();
         } else if (propertyValue instanceof Collection) {
 			// If it's a collection, then we do it item by item in the collection, and then exit
 			 Collection<?> propertyValueCast = (Collection<?>) propertyValue;
 			 return duplicateCollection( propertyValueCast, propertyName, parentBean );        	 
         } else if (propertyValue instanceof String) {
        	 return (String) propertyValue;	// String is immutable
         } else if (propertyValue instanceof Integer) {
        	 return (Integer) propertyValue;	// Primitive-types are immutable
         } else if (propertyValue instanceof Double) {
        	 return (Double) propertyValue;		// Primitive-types are immutable
         } else if (propertyValue instanceof Float) {
        	 return (Float) propertyValue;		// Primitive-types are immutable
         } else if (propertyValue instanceof Short) {
        	 return (Short) propertyValue;		// Primitive-types are immutable
         } else if (propertyValue instanceof Long) {
        	 return (Long) propertyValue;		// Primitive-types are immutable
         } else if (propertyValue instanceof Byte) {
        	 return (Byte) propertyValue;		// Primitive-types are immutable
         } else if (propertyValue instanceof Boolean) {
        	 return (Boolean) propertyValue;	// Primitive-types are immutable
         } else if (propertyValue instanceof Class) {
        	 // This is assumed to be immutable, so we don't bother changing it	// TODO eventually remove this
        	 return (Class) propertyValue;
       	 
         } else {
        	 throw new BeanDuplicateException( String.format("Unsupported property type: %s", propertyValue.getClass().toString()) );
         }
			
				
	}
	
	

	
	
	public static AnchorBean<?> duplicate( AnchorBean<?> bean ) {
		
		try {
			AnchorBean<?> beanOut = bean.getClass().getConstructor().newInstance();
			
			for(Field field  : bean.getOrCreateBeanFields()) {
	
		    	Object propertyOld = field.get(bean);
		    	Object propertyNew = duplicatePropertyValue(propertyOld,field.getName(),field.isAnnotationPresent(Optional.class), beanOut );
		    	
		    	field.set(beanOut, propertyNew);
			}

			// We also copy the localization information for the new bean
			beanOut.localise( bean.getLocalPath() );
			return beanOut;
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | BeanMisconfiguredException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
			throw new BeanDuplicateException(e);
		}
	}
}
