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


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class HelperReflection {

	private HelperReflection() {
		
	}
	
	public static PropertyDescriptor[] findAllPropertyDescriptors( Class<?> beanClass ) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, Object.class);
		return beanInfo.getPropertyDescriptors();
	}
	
	// Find a property-descriptor with a particular name from an array
	public static PropertyDescriptor findMatchingPropertyDescriptor( PropertyDescriptor[] descriptors, String fieldName ) {
		
		for( PropertyDescriptor pd : descriptors ) {
			if (pd.getName().equals(fieldName)) {
				return pd;
			}
		}
		return null;
	}
	
	// Gets all fields, including inherited fields
	public static List<Field> findAllFields( Class<?> c ) {
		
		List<Field> out = new ArrayList<>();
		
		Class<?> current = c;
		do {
			for( Field f : current.getDeclaredFields() ) {
				out.add(f);
			}
		} while( (current=current.getSuperclass()) !=null ); 
		
		return out;
	}
}
