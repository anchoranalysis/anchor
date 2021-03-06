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

package org.anchoranalysis.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

/**
 * Maps a {@link AnchorBean} family-type to some beans belong to the family.
 *
 * @author Owen Feehan
 */
public class BeanInstanceMap {

    private Map<Class<?>, Object> map = new HashMap<>();

    public boolean containsKey(Class<?> arg0) {
        return map.containsKey(arg0);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<? extends T> arg0) {
        return (T) map.get(arg0);
    }

    public <T> void put(Class<? extends T> arg0, T arg1) {
        map.put(arg0, arg1);
    }

    /**
     * Populates the entries from another BeanInstanceMap
     *
     * <p>It is a "shallow-copy". No duplication of values occurs. So after the function is
     * completed, any object references from other will also exist in this map.
     *
     * <p>Any existing map-entries are retained.
     *
     * @param other provides entries that are added to the current map
     */
    public void addFrom(BeanInstanceMap other) {

        for (Entry<Class<?>, Object> entry : other.map.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Populates from a list of {@link NamedBean}.
     *
     * <p>Any existing map-entries are retained.
     *
     * @param listNamedInstances list of {@link NamedBean}. The name of each bean maps to the class
     *     in the map.
     * @throws BeanMisconfiguredException if the list of {@link NamedBean} contains an invalid
     *     class.
     */
    public void addFrom(List<NamedBean<?>> listNamedInstances) throws BeanMisconfiguredException {

        try {
            for (NamedBean<?> namedBean : listNamedInstances) {
                map.put(Class.forName(namedBean.getName()), namedBean.getValue());
            }
        } catch (ClassNotFoundException e) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "Cannot find class %s that is referred to in bean XML",
                            e.getClass().toString()),
                    e);
        }
    }
}
