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
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.apache.commons.lang3.ClassUtils;

class HelperDuplication {
	
	private HelperDuplication() {}
	
	@SuppressWarnings("unchecked")
	public static <T> AnchorBean<T> duplicate( AnchorBean<T> bean ) {
		
		try {
			AnchorBean<T> beanOut = bean.getClass().getConstructor().newInstance();
			
			for(Field field  : bean.getOrCreateBeanFields()) {
	
		    	Object propertyOld = field.get(bean);
		    	Object propertyNew = duplicatePropertyValue(
		    		propertyOld,
		    		field.getName(),
		    		field.isAnnotationPresent(OptionalBean.class),
		    		beanOut
		    	);
		    	field.set(
		    		beanOut,
		    		propertyNew
		    	);
			}

			// We also copy the localization information for the new bean
			beanOut.localise( bean.getLocalPath() );
			return beanOut;
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | BeanMisconfiguredException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
			throw new BeanDuplicateException(e);
		}
	}

	private static Object duplicateCollection( Collection<?> collection, String propertyName, AnchorBean<?> parentBean ) {
		 return collection.stream().map( obj->
		 	duplicatePropertyValue(obj, propertyName, false, parentBean)
		 ).collect( Collectors.toList() );
	}
	
	@SuppressWarnings("rawtypes")
	private static Optional<Object> duplicatePropertyValue( Object propertyValue, String propertyName, boolean optional, AnchorBean<?> parentBean ) {

		if (propertyValue==null) {
			if (optional) {
				return Optional.empty();
			} else {
				throw new BeanDuplicateException( String.format("Property '%s' of '%s' is null, but non-optional",propertyName,parentBean.getBeanName()) );
			}
		}

		if (propertyValue instanceof StringSet) {
			StringSet propertyValueCast = (StringSet) propertyValue;
      	 	return Optional.of(
      	 		propertyValueCast.duplicateBean()
      	 	);
      	 	
		} else if (propertyValue instanceof AnchorBean) {
			// Our first priority is to duplicate a bean if we can, as it is possible for a Bean to be a Collection as well, and it's better
       	//  to use the Bean's normal duplication method
			AnchorBean propertyValueCast = (AnchorBean) propertyValue;
       	return Optional.of(
       		propertyValueCast.duplicateBean()
       	);
       	
        } else if (propertyValue instanceof Collection) {
       	// If it's a collection, then we do it item by item in the collection, and then exit
			return Optional.of(
				duplicateCollection(
					(Collection<?>) propertyValue,
					propertyName,
					parentBean
				)
			);
			 
        } else if (isImmutableType(propertyValue.getClass())) {
       	 // Any supported immutable type can be returned without duplication
       	 return Optional.of(propertyValue);
       	 
        } else {
       	 throw new BeanDuplicateException( String.format("Unsupported property type: %s", propertyValue.getClass().toString()) );
        }
	}
	
	/** Is a particular class what Java considered an immutable type? */
	private static boolean isImmutableType( Class<?> cls ) {
		if (String.class.isAssignableFrom(cls)) {
			return true;
		} else if (Class.class.isAssignableFrom(cls)) {
			return true;
		} else {
			return ClassUtils.isPrimitiveOrWrapper(cls);
		}
	}
}
