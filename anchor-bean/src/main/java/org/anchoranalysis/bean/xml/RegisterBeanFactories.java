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

import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.define.DefineFactory;
import org.anchoranalysis.bean.xml.factory.AnchorBeanFactory;
import org.anchoranalysis.bean.xml.factory.AnchorDefaultBeanFactory;
import org.anchoranalysis.bean.xml.factory.IncludeBeanFactory;
import org.anchoranalysis.bean.xml.factory.IncludeListBeanFactory;
import org.anchoranalysis.bean.xml.factory.ListBeanFactory;
import org.anchoranalysis.bean.xml.factory.ReplacePropertyBeanFactory;
import org.anchoranalysis.bean.xml.factory.primitive.DoubleListFactory;
import org.anchoranalysis.bean.xml.factory.primitive.DoubleSetFactory;
import org.anchoranalysis.bean.xml.factory.primitive.IntegerListFactory;
import org.anchoranalysis.bean.xml.factory.primitive.IntegerSetFactory;
import org.anchoranalysis.bean.xml.factory.primitive.StringListFactory;
import org.anchoranalysis.bean.xml.factory.primitive.StringSetFactory;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.apache.commons.configuration.beanutils.BeanHelper;

/**
 * Utility class for registering the Bean-factories that are found in anchor-bean (or any other).
 *
 * <p>Any new BeanFactory must first be registered before it can be read from BeanXML when the
 * config-factory attribute is parsed.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegisterBeanFactories {

    /** A check that {@link #registerAllPackageBeanFactories} has been called */
    private static boolean calledRegisterAllPackage = false;

    /**
     * Registers the factories for the beans that exist in this package
     *
     * <p>It additionally registers the AnchorDefaultBeanFactory as the default
     *
     * <p>If it's already been called, we simply do nothing
     *
     * @return the default factory for creating objects
     */
    public static AnchorDefaultBeanFactory registerAllPackageBeanFactories() {

        if (calledRegisterAllPackage) {
            return (AnchorDefaultBeanFactory) BeanHelper.getDefaultBeanFactory();
        }

        register("stringSet", new StringSetFactory());
        register("integerSet", new IntegerSetFactory());
        register("doubleSet", new DoubleSetFactory());

        register("stringList", new StringListFactory());
        register("integerList", new IntegerListFactory());
        register("doubleList", new DoubleListFactory());

        register("include", new IncludeBeanFactory());
        register("listInclude", new IncludeListBeanFactory<>());
        register("define", new DefineFactory());

        register("list", list -> list);

        // It's not been called
        calledRegisterAllPackage = true;

        BeanInstanceMap defaultInstances = new BeanInstanceMap();
        AnchorDefaultBeanFactory defaultFactory = new AnchorDefaultBeanFactory(defaultInstances);
        BeanHelper.setDefaultBeanFactory(defaultFactory);

        register("replaceProperty", new ReplacePropertyBeanFactory<>(defaultInstances));

        return defaultFactory;
    }

    /**
     * Default instances of different family-types of beans.
     *
     * @return a mapping between family-types and instances.
     */
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
     * Registers a specific factory.
     *
     * @param name name associated with factory i.e. for config-factory attribute in Xml
     * @param factory the factory to register
     */
    public static void register(String name, AnchorBeanFactory factory) {
        BeanHelper.registerBeanFactory(name, factory);
    }

    /**
     * Registers a bean that creates a {link java.util.List}.
     *
     * @param factoryName a specific factory
     * @param creator creator used to make list-bean
     * @param <T> list item type
     */
    public static <T> void register(String factoryName, Function<List<T>, Object> creator) {
        register(factoryName, new ListBeanFactory<>(creator));
    }

    /**
     * Has the method {@link #registerAllPackageBeanFactories} already been called?
     *
     * @return true if the method has been called (at least once), false otherwise.
     */
    public static boolean isCalledRegisterAllPackage() {
        return calledRegisterAllPackage;
    }
}
