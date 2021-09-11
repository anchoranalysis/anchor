/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Like {@link BeanCreator} but creates a list of beans.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanListCreator {

    /**
     * A list of instantiated bean objects from an XML configuration.
     *
     * @param key key to match inside bean XML definition (name of the bean).
     * @param config the config associated with the XML definition.
     * @param param the parameter that is used in creation of beans.
     * @return a newly created list of the instantiated bean objects.
     */
    public static <T> List<T> createListBeans(
            String key, HierarchicalConfiguration config, Object param) {
        List<HierarchicalConfiguration> fields = config.configurationsAt(key);

        ArrayList<T> out = new ArrayList<>(fields.size());

        createAndConsumeListBeans(key, config, param, out);
        return out;
    }

    /**
     * Extracts a list of instantiated bean objects from an XML configuration, and adds them to
     * {@code addTo}.
     *
     * @param key key to match inside bean XML definition (name of the bean).
     * @param config the config associated with the XML definition.
     * @param param the parameter that is used in creation of beans
     * @param addTo the collection the created beans are added to.
     */
    public static <T> void createAndConsumeListBeans(
            String key, HierarchicalConfiguration config, Object param, Collection<T> addTo) {
        List<HierarchicalConfiguration> fields = config.configurationsAt(key);

        for (Iterator<HierarchicalConfiguration> iterator = fields.iterator();
                iterator.hasNext(); ) {
            HierarchicalConfiguration subConfig = iterator.next();

            T bean = BeanCreator.createBeanFromConfig(subConfig, param);
            addTo.add(bean);
        }
    }
}
