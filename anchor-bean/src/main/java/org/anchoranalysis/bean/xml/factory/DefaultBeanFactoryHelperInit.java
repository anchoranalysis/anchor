package org.anchoranalysis.bean.xml.factory;

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


import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;

// We change the behaviour of BeanHelper so that it will keep passing the current 'param' onto new beans which are created
class DefaultBeanFactoryHelperInit {

	private DefaultBeanFactoryHelperInit() {
		
	}
	
	

	public static void initBean(Object bean, BeanDeclaration data, Object parameter)
            throws ConfigurationRuntimeException
    {
        BeanHelper.initBeanProperties(bean, data);

        Map<String, Object> nestedBeans = data.getNestedBeanDeclarations();
        if (nestedBeans != null)
        {
            if (bean instanceof Collection)
            {
                // This is safe because the collection stores the values of the
                // nested beans.
                @SuppressWarnings("unchecked")
                Collection<Object> coll = (Collection<Object>) bean;
                if (nestedBeans.size() == 1)
                {
                    Map.Entry<String, Object> e = nestedBeans.entrySet().iterator().next();
                    String propName = e.getKey();
                    Class<?> defaultClass = getDefaultClass(bean, propName);
                    if (e.getValue() instanceof List)
                    {
                        // This is safe, provided that the bean declaration is implemented
                        // correctly.
                        @SuppressWarnings("unchecked")
                        List<BeanDeclaration> decls = (List<BeanDeclaration>) e.getValue();
                        for (BeanDeclaration decl : decls)
                        {
                            coll.add(BeanHelper.createBean(decl, defaultClass, parameter));
                        }
                    }
                    else
                    {
                        BeanDeclaration decl = (BeanDeclaration) e.getValue();
                        coll.add(BeanHelper.createBean(decl, defaultClass));
                    }
                }
            }
            else
            {
                for (Map.Entry<String, Object> e : nestedBeans.entrySet())
                {
                    String propName = e.getKey();
                    Class<?> defaultClass = getDefaultClass(bean, propName);
                    initProperty(bean, propName, BeanHelper.createBean(
                        (BeanDeclaration) e.getValue(), defaultClass, parameter));
                }
            }
        }
    }
	
	
	  /**
     * Sets a property on the given bean using Common Beanutils.
     *
     * @param bean the bean
     * @param propName the name of the property
     * @param value the property's value
     * @throws ConfigurationRuntimeException if the property is not writeable or
     * an error occurred
     */
    private static void initProperty(Object bean, String propName, Object value) {
        if (!PropertyUtils.isWriteable(bean, propName))
        {
            throw new ConfigurationRuntimeException("Property " + propName
                    + " cannot be set on " + bean.getClass().getName());
        }

        try
        {
            BeanUtils.setProperty(bean, propName, value);
        }
        catch (IllegalAccessException iaex)
        {
            throw new ConfigurationRuntimeException(iaex);
        }
        catch (InvocationTargetException itex)
        {
            throw new ConfigurationRuntimeException(itex);
        }
    }
	
    /**
     * Return the Class of the property if it can be determined.
     * @param bean The bean containing the property.
     * @param propName The name of the property.
     * @return The class associated with the property or null.
     */
    private static Class<?> getDefaultClass(Object bean, String propName)
    {
        try
        {
            PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(bean, propName);
            if (desc == null)
            {
                return null;
            }
            return desc.getPropertyType();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    
}
