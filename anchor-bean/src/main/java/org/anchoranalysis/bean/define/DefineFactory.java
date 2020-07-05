package org.anchoranalysis.bean.define;

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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.anchoranalysis.bean.define.adder.DefineAdder;
import org.anchoranalysis.bean.define.adder.DefineAdderBean;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.bean.xml.factory.AnchorBeanFactory;
import org.anchoranalysis.bean.xml.factory.HelperListUtilities;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * Creates a new Define
 * 
 * The following rules are applied to populate the Define
 * 1. Elements named "add" are treated as instances of DefineAdderBean 
 * 2. Otherwise every subelement of NamedDefinitions is simply assumed to be a list of beans (the names of the subelements are irrelevant)
 * 
 * @author Owen Feehan
 *
 */
public class DefineFactory extends AnchorBeanFactory {
	
	private static final String ELEMENT_NAME_ADD = "add";
	
    // Creates the bean. Checks if already an instance exists.
	@Override
	@SuppressWarnings("rawtypes")
	public synchronized Object createBean(Class beanClass, BeanDeclaration decl,
        Object param) throws Exception
    {
		Define out = new Define();
		
		XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
    	SubnodeConfiguration subConfig = declXML.getConfiguration();

    	// We find all items ELEMENT_NAME_ADD and treat them as adder
    	List<DefineAdderBean> list = HelperListUtilities.listOfBeans(ELEMENT_NAME_ADD, subConfig, param);
    	addMany(out, list);
    	
    	// We iterate through all other beans, and create an adder for the assumption that each is a list of NamedBeans
    	Set<DefineAdder> subconfigAdder = adderForOtherElements(subConfig, param);
    	addMany(out, subconfigAdder);
    	
		return out;
    }
	
	private static <T extends DefineAdder> void addMany( Define out, Iterable<T> adderIterable ) throws BeanXmlException {
		for( DefineAdder adder : adderIterable ) {
    		adder.addTo(out);
    	}
	}
	
	private static Set<DefineAdder> adderForOtherElements( SubnodeConfiguration subConfig, Object param ) {
    	Set<String> keys = SubNodeKeysHelper.extractKeys(subConfig);
    	keys.remove(ELEMENT_NAME_ADD);
    	return adderForKeys(keys, subConfig, param);
	}
	
	private static Set<DefineAdder> adderForKeys( Set<String> keys, SubnodeConfiguration subConfig, Object param ) {
		return keys.stream().map( k ->
			new AddFromSubconfigKey(k, subConfig, param)
		).collect( Collectors.toSet() );
	}
	
}
