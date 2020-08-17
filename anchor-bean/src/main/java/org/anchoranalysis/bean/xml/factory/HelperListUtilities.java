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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Utilities for manipulating list of beans
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HelperListUtilities {

    /**
     * A list of instantiated bean objects from an XML configuration
     *
     * @param key key to match inside bean XML definition (name of the bean)
     * @param config the config associated with the XML definition
     * @param param the parameter that is used in creation of beans
     * @return
     */
    public static <T> List<T> listOfBeans(
            String key, HierarchicalConfiguration config, Object param) {
        List<HierarchicalConfiguration> fields = config.configurationsAt(key);

        ArrayList<T> retList = new ArrayList<>(fields.size());

        addListOfBeansToCollection(key, config, retList, param);
        return retList;
    }

    // gets a list of instantiated bean objects from an XML configuration
    public static <T> void addListOfBeansToCollection(
            String key, HierarchicalConfiguration config, Collection<T> addList, Object param) {
        List<HierarchicalConfiguration> fields = config.configurationsAt(key);

        for (Iterator<HierarchicalConfiguration> it = fields.iterator(); it.hasNext(); ) {
            HierarchicalConfiguration subsubConfig = it.next();

            T bean = HelperUtilities.createBeanFromConfig(subsubConfig, param);
            addList.add(bean);
        }
    }
}
