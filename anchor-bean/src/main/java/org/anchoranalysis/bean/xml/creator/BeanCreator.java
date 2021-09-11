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

package org.anchoranalysis.bean.xml.creator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * Creates a single {@link AnchorBean} from XML.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanCreator {

    /**
     * Creates a bean from XML.
     *
     * @param <T> type of bean to create.
     * @param declaration the XML to create from.
     * @param configurationKey name of top-level element in XML file.
     * @param param an additionally parameter passed to {@link
     *     BeanHelper#createBean(BeanDeclaration)} when the bean is created.
     * @return the created bean.
     */
    public static <T> T createBeanFromXML(
            XMLBeanDeclaration declaration, String configurationKey, Object param) {
        HierarchicalConfiguration itemConfig =
                declaration.getConfiguration().configurationAt(configurationKey);
        T bean = BeanCreator.createBeanFromConfig(itemConfig, param); // NOSONAR
        return bean;
    }

    /***
     * Creates a bean from a {@link HierarchicalConfiguration}.
     *
     * @param <T> type of bean to create.
     * @param config the configuration.
     * @param param a parameter passed to {@link BeanHelper#createBean}.
     * @return a newly created bean, as derived from {@code config}.
     */
    public static <T> T createBeanFromConfig(HierarchicalConfiguration config, Object param) {
        BeanDeclaration subDeclaration = new XMLBeanDeclaration(config, ".");

        // Kept as separate lines to avoid confusion in the compiler
        @SuppressWarnings("unchecked")
        T bean = (T) BeanHelper.createBean(subDeclaration, null, param);

        return bean;
    }
}
