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

import java.util.List;
import org.anchoranalysis.bean.xml.creator.BeanListCreator;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * Like {@link ListBeanFactory} but also includes elements in other lists defined in the {@code
 * include} tag. as elements.
 *
 * <p>This is particularly useful when combined with the {@code include} factory, to include
 * elements from other XML files.
 *
 * <p>For example:
 *
 * <pre>{@code <config>
 * <bean config-class="java.util.List" config-factory="listInclude">
 *
 * <include>
 *      <item config-class="java.util.List" config-factory="include" filePath="fileToInclude.xml"/>
 *      <item config-class="java.util.List" config-factory="include" filePath="anotherFileToInclude.xml"/>
 * </include>
 *
 * <item config-class="org.anchoranalysis.bean.SomeBean"/>
 * <item config-class="org.anchoranalysis.bean.SomeBean"/>
 * <item config-class="org.anchoranalysis.bean.SomeBean"/>
 *
 * </bean>
 * </config>}</pre>
 *
 * @author Owen Feehan
 * @param <T> list-item type
 */
public class IncludeListBeanFactory<T> extends AnchorBeanFactory {

    /**
     * Default constructor.
     */
    public IncludeListBeanFactory() {
        // FOR BEAN INITIALIZATION
    }

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declaration = (XMLBeanDeclaration) decl;
        SubnodeConfiguration subConfig = declaration.getConfiguration();

        List<T> list = BeanListCreator.createListBeans("item", subConfig, param);

        if (!subConfig.configurationsAt("include").isEmpty()) {
            addFromInclude(subConfig.configurationAt("include"), param, list);
        }
        return list;
    }

    /** Add beans declared in files in the {@code include} element. */
    private void addFromInclude(SubnodeConfiguration includeConfig, Object param, List<T> list) {
        List<List<T>> listInclude = BeanListCreator.createListBeans("item", includeConfig, param);
        for (List<T> include : listInclude) {
            list.addAll(0, include);
        }
    }
}
