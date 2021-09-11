/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.bean.xml.factory;

import java.lang.reflect.InvocationTargetException;
import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.xml.creator.BeanCreator;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * Replaces a property (an XML element of attribute) on a bean before loading it.
 *
 * <p>The String attribute {@code key} specifies the name of the element or attribute to replace.
 *
 * <p>The replacement attribute or element specifies the replacement value, depending of whether
 * it's an attribute or element.
 *
 * @param <T> type of bean to load
 */
@AllArgsConstructor
public class ReplacePropertyBeanFactory<T extends AnchorBean<T>> extends AnchorBeanFactory {

    private static final String ITEM = "item";
    private static final String REPLACEMENT = "replacement";
    private static final String KEY = "key";

    /** Default instances of different family-types of beans. */
    private final BeanInstanceMap defaultInstances;

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;

        checkElement(declXML, ITEM);
        checkAttribute(declXML, KEY);

        // is the replacement an attribute or an element?
        boolean attribute = isReplaceAttribute(declXML);

        // The element we wish to replace
        String key = (String) declXML.getBeanProperties().get(KEY);

        T bean = extractBeanAndReplace(declXML, key, attribute, param);

        // We run this again, as the contents of the Bean have changed, and possibly
        //  there are other activities hanging from the checkMisconfigured
        bean.checkMisconfigured(defaultInstances);
        return bean;
    }

    private T extractBeanAndReplace(
            XMLBeanDeclaration declXML, String key, boolean attribute, Object param)
            throws BeanXMLException {
        T bean = BeanCreator.createBeanFromXML(declXML, ITEM, param);

        // Update bean with replacement
        Object replacement = extractReplacement(declXML, attribute, param);

        // Check that the key exists to replace
        if (!hasProperty(bean, key)) {
            throw new BeanXMLException(
                    String.format(
                            "There is no property '%s' on the item on which the replacement occurs",
                            key));
        }

        try {
            BeanUtils.setProperty(bean, key, replacement); // NOSONAR
        } catch (IllegalAccessException | InvocationTargetException exc) {
            throw new BeanXMLException(String.format("Cannot set property '%s'", key), exc);
        }
        return bean;
    }

    private static <S extends AnchorBean<S>> boolean hasProperty(S bean, String property) {
        try {
            BeanUtils.getProperty(bean, property);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    /*** Depending of we are replacing an attribute or an element (field or bean) we extract it differently */
    private Object extractReplacement(XMLBeanDeclaration declXML, boolean attribute, Object param) {
        if (attribute) {
            return declXML.getBeanProperties().get(REPLACEMENT);
        } else {
            return BeanCreator.createBeanFromXML(declXML, REPLACEMENT, param);
        }
    }

    private boolean isReplaceAttribute(XMLBeanDeclaration declXML) throws BeanXMLException {
        boolean element = hasElement(declXML, REPLACEMENT);
        boolean attribute = hasAttribute(declXML, REPLACEMENT);

        if (element && attribute) {
            throw new BeanXMLException(
                    "The 'replacement' property is set on the bean as both an attribute and element. Only one is allowed.");
        }

        if (!element && !attribute) {
            throw new BeanXMLException("The 'replacement' property is NOT set on the bean");
        }

        return attribute;
    }

    private void checkAttribute(XMLBeanDeclaration declXML, String key) throws BeanXMLException {
        if (!hasAttribute(declXML, key)) {
            throw new BeanXMLException(
                    String.format("This bean must have a '%s' attribute set", key));
        }
    }

    private void checkElement(XMLBeanDeclaration declXML, String key) throws BeanXMLException {
        if (!hasElement(declXML, key)) {
            throw new BeanXMLException(
                    String.format("This bean must have a '%s' element set", key));
        }
    }

    private boolean hasAttribute(XMLBeanDeclaration declXML, String key) {
        return declXML.getBeanProperties().containsKey(key);
    }

    private boolean hasElement(XMLBeanDeclaration declXML, String key) {
        return declXML.getNestedBeanDeclarations().containsKey(key);
    }
}
