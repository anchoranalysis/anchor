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

package org.anchoranalysis.bean.define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.FunctionalList;

/**
 * A bean where the definitions of many different {@link NamedBean}s can be specified.
 *
 * <p>These definitions are indexed by string identifiers.
 *
 * @author Owen Feehan
 */
public class Define extends AnchorBean<Define> {

    /**
     * A map from {@link GroupingRoot} to a list of {@link NamedBean}s that must subclass from this
     * root
     */
    private Map<Class<?>, List<NamedBean<?>>> map = new HashMap<>();

    /**
     * Adds a named-bean to our definitions, using the {#link GroupingRoot} annotation to determine
     * a group where definitions are stored.
     *
     * <p>Any added-bean must of a type that contains the {@link GroupingRoot} annotation in its
     * class hierarchy.
     *
     * @param bean a named-bean to add.
     * @throws DefineAddException if a {#link GroupingRoot} cannot be found in the class-hierarchy.
     */
    public void add(NamedBean<?> bean) throws DefineAddException {

        Class<?> itemType = bean.getItem().getClass();

        // Find the grouping-root from the item
        Class<?> groupingRoot = findGroupingRoot(itemType);

        listForGroup(groupingRoot).add(bean);
    }

    /**
     * Adds all the named-beans from source to the current map.
     *
     * <p>This is a shallow copy.
     *
     * @param source where to copy from.
     */
    public void addAll(Define source) {
        for (Class<?> key : source.keySet()) {
            try {
                List<NamedBean<AnchorBean<?>>> list = source.listFor(key);
                addAll(list);
            } catch (DefineAddException e) {
                throw new AnchorImpossibleSituationException();
            }
        }
    }

    /**
     * Adds all the named-beans from source to the current map.
     *
     * <p>This is a shallow copy.
     *
     * @param list where to copy from.
     * @param <T> type of bean to add.
     * @throws DefineAddException if a {#link GroupingRoot} cannot be found in the class-hierarchy.
     */
    public <T extends AnchorBean<?>> void addAll(List<NamedBean<T>> list)
            throws DefineAddException {
        for (NamedBean<?> bean : list) {
            add(bean);
        }
    }

    /**
     * Retrieves the list of elements associated with a grouping-root.
     *
     * @param <T> type of elements in the list to retrieve. This should correspond to {@code
     *     groupingRoot} or a subclass.
     * @param groupingRoot a class corresponding to a grouping-root (family-type) that may exist in
     *     {@link Define}.
     * @return a newly created list with all associated elements, or an empty-list if no elements
     *     are associated.
     */
    public <T extends AnchorBean<?>> List<NamedBean<T>> listFor(Class<?> groupingRoot) {

        List<NamedBean<?>> list = map.get(groupingRoot);

        if (list == null) {
            // Nothing there, so we exit early
            return new ArrayList<>();
        }

        // We always create a new list, as a workaround for our inability to cast
        return FunctionalList.mapToList(list, NamedBean.class::cast);
    }

    @Override
    public Define duplicateBean() {

        // We must also copy the map, and duplicate its contents, as otherwise a new empty
        Define out = new Define();
        for (Entry<Class<?>, List<NamedBean<?>>> entry : map.entrySet()) {
            out.map.put(entry.getKey(), duplicateList(entry.getValue()));
        }
        return out;
    }

    private Set<Class<?>> keySet() {
        return map.keySet();
    }

    /**
     * Gets an existing list for a group, or creates one if it doesn't already exist
     *
     * @return an existing or newly-created list
     */
    private List<NamedBean<?>> listForGroup(Class<?> groupingRoot) {
        return map.computeIfAbsent(groupingRoot, key -> new ArrayList<>());
    }

    private static List<NamedBean<?>> duplicateList(List<NamedBean<?>> in) {
        return FunctionalList.mapToList(in, NamedBean::duplicateBean);
    }

    private static Class<?> findGroupingRoot(Class<?> leaf) throws DefineAddException {

        Class<?> consider = leaf;
        do {
            if (consider.isAnnotationPresent(GroupingRoot.class)) {
                return consider;
            }

            consider = consider.getSuperclass();
        } while (consider != null);

        throw new DefineAddException(
                String.format(
                        "Bean-class %s is missing a groupingRoot. This must exist in the class-hierarchy for any item in a NamedDefinitions",
                        leaf));
    }
}
