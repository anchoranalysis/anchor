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


import java.util.List;

import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> list-item type
 */
public class IncludeListFactory<T> extends AnchorBeanFactory {

	public IncludeListFactory() {
		// FOR BEAN INITIALIZATION
	}

	// Creates the bean. Checks if already an instance exists.
	@Override
	@SuppressWarnings("rawtypes")
	public synchronized Object createBean(Class beanClass, BeanDeclaration decl,
        Object param) throws Exception
    {
    	XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
    	SubnodeConfiguration subConfig = declXML.getConfiguration();
    	
    	List<T> list = HelperListUtilities.listOfBeans("item", subConfig, param);
    	
    	if (!subConfig.configurationsAt("include").isEmpty()) {
	    	SubnodeConfiguration includeConfig = subConfig.configurationAt("include");
	    	
	    	List<List<T>> listInclude = HelperListUtilities.listOfBeans("item", includeConfig, param );
	    	for( List<T> include : listInclude ) {
	    		list.addAll(0,include);
	    	}
    	}
   		return list;
    }
}