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
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * Factory for creating a java.util.List of Beans
 *
 * <p>Several {@code <item>someitem</item>} tags can be placed in the BeanXML and each becomes an
 * item in the list
 *
 * <pre>{@code
 * <list config-class="java.util.List" config-factory="list">
 * 	<item config-class="someclass"/>
 * 	<item config-class="someclass"/>
 *   <item config-class="someclass"/>
 * </list>
 * }</pre>
 *
 * @author Owen Feehan
 * @param <T> list-item type
 */
public class ListBeanFactory<T> extends AnchorBeanFactory {

    private CreateFromList<T> creator;

    public ListBeanFactory() {
        // FOR BEAN INITIALIZATION
    }

    public ListBeanFactory(CreateFromList<T> creator) {
        super();
        this.creator = creator;
    }

    // Creates the bean. Checks if already an instance exists.
    @Override
    @SuppressWarnings("rawtypes")
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl, Object param)
            throws Exception {
        XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
        SubnodeConfiguration subConfig = declXML.getConfiguration();

        List<T> list = HelperListUtilities.listOfBeans("item", subConfig, param);
        if (creator != null) {
            return creator.create(list);
        } else {
            return list;
        }
    }
}
