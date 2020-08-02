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

package org.anchoranalysis.bean.xml;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.define.DefineFactory;
import org.anchoranalysis.bean.xml.factory.AnchorBeanFactory;
import org.anchoranalysis.bean.xml.factory.AnchorDefaultBeanFactory;
import org.anchoranalysis.bean.xml.factory.ICreateFromList;
import org.anchoranalysis.bean.xml.factory.IncludeBeanFactory;
import org.anchoranalysis.bean.xml.factory.IncludeListFactory;
import org.anchoranalysis.bean.xml.factory.ListBeanFactory;
import org.anchoranalysis.bean.xml.factory.ReplacePropertyBeanFactory;
import org.anchoranalysis.bean.xml.factory.StringListFactory;
import org.anchoranalysis.bean.xml.factory.StringSetFactory;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.apache.commons.configuration.beanutils.BeanHelper;

/**
 * Utility class for registering the Bean-factories that are found in anchor-bean (or any other)
 *
 * <p>Any new BeanFactory must first be registered before it can be read from BeanXML when the
 * config-factory attribute is parsed.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegisterBeanFactories {

    // START keys for factories
    // i.e. what is found in the config-factory attribute in the XML
    private static final String FACTORY_STRING_SET = "stringSet";
    private static final String FACTORY_STRING_LIST = "stringList";
    private static final String FACTORY_INCLUDE = "include";
    private static final String FACTORY_OBJECT_LIST_INCLUDE = "listInclude";
    private static final String FACTORY_OBJECT_LIST = "list";
    private static final String FACTORY_DEFINE = "define";
    private static final String FACTORY_REPLACE_PROPERTY = "replaceProperty";
    // END keys for factories

    // A check that registerAllPackageBeanFactories() has been called
    private static boolean calledRegisterAllPackage = false;

    /**
     * Registers the factories for the beans that exist in this package
     *
     * <p>It additionally registers the AnchorDefaultBeanFactory as the default
     *
     * <p>If it's already been called, we simply do nothing
     *
     * @param returns the default factory for creating objects
     */
    public static AnchorDefaultBeanFactory registerAllPackageBeanFactories() {

        if (calledRegisterAllPackage) {
            return (AnchorDefaultBeanFactory) BeanHelper.getDefaultBeanFactory();
        }

        register(FACTORY_STRING_SET, new StringSetFactory());

        register(FACTORY_STRING_LIST, new StringListFactory());

        register(FACTORY_INCLUDE, new IncludeBeanFactory());
        register(FACTORY_OBJECT_LIST_INCLUDE, new IncludeListFactory<>());
        register(FACTORY_DEFINE, new DefineFactory());

        register(FACTORY_OBJECT_LIST, list -> list);

        // It's not been called
        calledRegisterAllPackage = true;

        BeanInstanceMap defaultInstances = new BeanInstanceMap();
        AnchorDefaultBeanFactory defaultFactory = new AnchorDefaultBeanFactory(defaultInstances);
        BeanHelper.setDefaultBeanFactory(defaultFactory);

        register(FACTORY_REPLACE_PROPERTY, new ReplacePropertyBeanFactory<>(defaultInstances));

        return defaultFactory;
    }

    public static BeanInstanceMap getDefaultInstances() {
        if (calledRegisterAllPackage) {
            return ((AnchorDefaultBeanFactory) BeanHelper.getDefaultBeanFactory())
                    .getDefaultInstances();
        } else {
            throw new AnchorFriendlyRuntimeException(
                    "Unable to retrieve defaultInstances as registerAllPackageBeanFactories() has never been called");
        }
    }

    /**
     * Registers a specific factory
     *
     * @param name name associated with factory i.e. for config-factory attribute in Xml
     * @param factory the factory to register
     */
    public static void register(String name, AnchorBeanFactory factory) {
        BeanHelper.registerBeanFactory(name, factory);
    }

    /**
     * Registers a bean that creates a {@code List<>}
     *
     * @param factoryName a specific factory
     * @param creator creator used to make list-bean
     * @param <T> list item type
     */
    public static <T> void register(String factoryName, ICreateFromList<T> creator) {
        register(factoryName, new ListBeanFactory<>(creator));
    }

    public static boolean isCalledRegisterAllPackage() {
        return calledRegisterAllPackage;
    }
}
