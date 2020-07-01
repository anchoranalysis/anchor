package org.anchoranalysis.bean.xml.factory;

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

import org.anchoranalysis.bean.StringBeanCollection;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

class HelperUtilities {
	
	private HelperUtilities() {}
	
	public static <T> T createBeanFromXML( XMLBeanDeclaration declXML, String configurationKey, Object param ) {
		HierarchicalConfiguration itemConfig = declXML.getConfiguration().configurationAt(configurationKey);
		T bean = HelperUtilities.createBeanFromConfig(itemConfig, param);
		return bean;
	}
	
	/*** Creates a bean from a HierarchicalConfiguration */
	public static <T> T createBeanFromConfig(HierarchicalConfiguration config, Object param) {
		BeanDeclaration subDecl = new XMLBeanDeclaration(config, ".");
		
		@SuppressWarnings("unchecked")
		T bean = (T) BeanHelper.createBean(subDecl, null, param);
		
		return bean;
	}
	
	public static StringBeanCollection populateStringCollectionFromXml( StringBeanCollection collection, BeanDeclaration decl ) {
    	
    	XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
    	SubnodeConfiguration subConfig = declXML.getConfiguration();
    	
    	String[] stringArr =  subConfig.getStringArray( "item" );
    	for( String item : stringArr) {
    		collection.add( item );
    	}
    	
    	return collection;
	}
}
