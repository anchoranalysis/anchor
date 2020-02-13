package org.anchoranalysis.bean.init;

import java.lang.reflect.Field;

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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;

class HelperInit {

	private HelperInit() {
		
	}
		
	/**
	 *
	 * Initializes the bean
	 * 
	 * @param bean  bean to initialise
	 * @param pi    the property-initializer to use
	 * @param logger logger
	 * @param parent an optional-bean parent to use, otherwise NULL
	 * @throws InitException
	 */
	public static void initRecursive( AnchorBean<?> bean, PropertyInitializer<?> pi, LogErrorReporter logger ) throws InitException {
		
		List<BeanAndParent> everything = new ArrayList<>();
		everything.add( new BeanAndParent(bean, null) );
		
		Set<AnchorBean<?>> done = new HashSet<>();
		
		while( !everything.isEmpty() ) {
			
			BeanAndParent removedObj = everything.remove( everything.size()-1 );

			if (done.contains(removedObj.getBean())) {
				continue;
			}
			
			maybeInitChildren(removedObj, everything, bean.getBeanName(), pi, logger);
			
			done.add(removedObj.getBean() );
		}

	}
	
	private static void maybeInitChildren( BeanAndParent beanAndParent, List<BeanAndParent> listInit, String beanNameFollowingFrom, PropertyInitializer<?> pi, LogErrorReporter logger ) throws InitException {
		
		try {
			boolean didInitChild = pi.execIfInheritsFrom(beanAndParent.getBean(), beanAndParent.parentBean(), logger); 
			if (didInitChild) {
				// Add children of the object, but only if PropertyInitializer initialize the object
				addChildrenForInit(listInit, beanAndParent);		
			} else {
				throwExceptionIfInitializable( beanAndParent.getBean(), beanNameFollowingFrom, pi.getInitParamType().toString() );
			}
		
		
		} catch (InitException e) {
			String msg = String.format(
				"Cannot initialize a field '%s' when initializing '%s'",
				beanAndParent.pathFromRootAsString(),
				beanNameFollowingFrom
			);
			throw new InitException(msg,e);
		}
	}
	
	
	private static void addChildrenForInit( List<BeanAndParent> listInit, BeanAndParent parentBean ) throws InitException {
		
		List<Field> beanFields = parentBean.getBean().getOrCreateBeanFields();
		
		for(Field field  : beanFields) {
			
			if (!field.isAnnotationPresent(SkipInit.class)) {
			
				// Create a FIFO queue of items to be initialised
				addChildrenMany( listInit, parentBean, field, field.isAnnotationPresent(Optional.class) );
			}
		}
	}
	
	private static void addChildrenMany( List<BeanAndParent> listInit, BeanAndParent parentBean, Field field, boolean optional ) throws InitException {
	 
		try {
			Object propertyValue = field.get(parentBean.getBean());
			
			 // If it's a collection, then we do it item by item in the collection, and then exit
			 if (propertyValue instanceof Collection) {
				 
				 Collection<?> collection = (Collection<?>) propertyValue;
				 for( Object o : collection ) {
					 addChildren( listInit, parentBean, o, false );
				 }
				 
				 return;
	         }
			
			addChildren( listInit, parentBean, propertyValue, optional );
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new InitException(e);
		}
	}
	
	private static void addChildren( List<BeanAndParent> listInit, BeanAndParent parentBean, Object propertyValue, boolean optional ) throws InitException {
		
		// If it's an optional attribute we check that it's not null
    	if (optional && propertyValue==null) {
   			 return;
    	}
    	
    	// We only add if its a bean itself
    	if (propertyValue instanceof AnchorBean) {
    		listInit.add(
    			new BeanAndParent( (AnchorBean<?>) propertyValue, parentBean )
    		);
    	}
	}
	
	private static void throwExceptionIfInitializable( AnchorBean<?> bean, String beanNameFollowingFrom, String provider ) throws InitException {
		
		if (bean instanceof InitializableBean) {
			InitializableBean<?,?> objCast = (InitializableBean<?,?>) bean;
			throw new InitException(
				String.format("Could not find matching params to initialise %s (%s) (recursively following from %s). Requires %s. Provider is %s.",
					objCast.getBeanName(),
					objCast.getClass().getName(),
					beanNameFollowingFrom,
					objCast.getPropertyDefiner().describeAcceptedClasses(),
					provider
				)
			);
		}
	}

}
