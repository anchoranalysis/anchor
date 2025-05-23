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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;

/**
 * This is a factory that is similar to {@link BeanHelper} but changes behavior so that it will keep
 * passing the current <i>param</i> onto new beans which are created.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DefaultBeanFactoryHelperInit {

    /// Initializing a collection
    private static class InitCollection {

        private final Collection<Object> beanCollection;
        private final Optional<Class<?>> defaultClass;

        public InitCollection(Collection<Object> beanCollection, String propName) {
            this.beanCollection = beanCollection;
            this.defaultClass = getDefaultClass(beanCollection, propName);
        }

        @SuppressWarnings("unchecked")
        public void initialize(Object propVal, Object parameter) {
            if (propVal instanceof List) {
                // This is safe, provided that the bean declaration is implemented
                // correctly.
                for (BeanDeclaration decl : (List<BeanDeclaration>) propVal) {
                    addWithParam(decl, parameter);
                }

            } else {
                addWithoutParam((BeanDeclaration) propVal);
            }
        }

        private void addWithoutParam(BeanDeclaration decl) {
            beanCollection.add(BeanHelper.createBean(decl, defaultClass.orElse(null)));
        }

        private void addWithParam(BeanDeclaration decl, Object parameter) {
            beanCollection.add(BeanHelper.createBean(decl, defaultClass.orElse(null), parameter));
        }
    }

    @AllArgsConstructor
    private static class InitNested {

        private final Map<String, Object> nestedBeans;
        private final Object parameter;

        @SuppressWarnings("unchecked")
        public void initBean(Object bean) throws BeanXMLException {

            if (bean instanceof Collection) {
                initCollection((Collection<Object>) bean);
            } else {
                initNonCollection(bean);
            }
        }

        private void initCollection(Collection<Object> beanCollection) {
            // This is safe because the collection stores the values of the
            // nested beans.
            if (nestedBeans.size() != 1) {
                return;
            }

            // Extract propery key and value from the first item
            Map.Entry<String, Object> e = nestedBeans.entrySet().iterator().next();
            new InitCollection(beanCollection, e.getKey()).initialize(e.getValue(), parameter);
        }

        private void initNonCollection(Object bean) throws BeanXMLException {
            for (Map.Entry<String, Object> e : nestedBeans.entrySet()) {
                String propName = e.getKey();
                Optional<Class<?>> defaultClass = getDefaultClass(bean, propName);

                if (e.getValue() instanceof BeanDeclaration value) {
                    initProperty(
                            bean,
                            propName,
                            BeanHelper.createBean(value, defaultClass.orElse(null), parameter));
                } else {
                    throw new BeanXMLException(
                            "The value of a Bean-Property field is neither a scalar primitive nor a bean.");
                }
            }
        }

        /**
         * Sets a property on the given bean using Common Beanutils.
         *
         * @param bean the bean
         * @param propName the name of the property
         * @param value the property's value
         * @throws ConfigurationRuntimeException if the property is not writeable or an error
         *     occurred
         */
        private void initProperty(Object bean, String propName, Object value) {
            if (!PropertyUtils.isWriteable(bean, propName)) {
                throw new ConfigurationRuntimeException(
                        "Property " + propName + " cannot be set on " + bean.getClass().getName());
            }

            try {
                BeanUtils.setProperty(bean, propName, value); // NOSONAR
            } catch (IllegalAccessException | InvocationTargetException iaex) {
                throw new ConfigurationRuntimeException(iaex);
            }
        }
    }

    public static void initBean(Object bean, BeanDeclaration data, Object parameter)
            throws BeanXMLException {
        BeanHelper.initBeanProperties(bean, data);

        Map<String, Object> nestedBeans = data.getNestedBeanDeclarations();
        if (nestedBeans != null) {
            new InitNested(nestedBeans, parameter).initBean(bean);
        }
    }

    /**
     * Return the Class of the property if it can be determined.
     *
     * @param bean The bean containing the property.
     * @param propName The name of the property.
     * @return The class associated with the property or null.
     */
    private static Optional<Class<?>> getDefaultClass(Object bean, String propName) {
        try {
            PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(bean, propName);
            if (desc == null) {
                return Optional.empty();
            }
            return Optional.of(desc.getPropertyType());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
