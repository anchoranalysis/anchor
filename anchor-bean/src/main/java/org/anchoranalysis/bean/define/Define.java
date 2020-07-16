/* (C)2020 */
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
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * A bean where the definitions of many different NamedBeans can be specified and are indexed by a
 * collection of keys.
 *
 * @author Owen Feehan
 */
public class Define extends AnchorBean<Define> {

    /** A map from GroupingRoot to a list of NamedBeans that must subclass from this root */
    private Map<Class<?>, List<NamedBean<?>>> map = new HashMap<>();

    /**
     * Adds a named-bean to our definitions, using the {#link
     * org.anchoranalysis.bean.annotation.GroupingRoot} annotation to determine a group where
     * definitions are stored.
     *
     * <p>Any added-bean must of a type that contains the GroupingRoot annotation in its class
     * hierarchy
     *
     * @param bean a named-bean to add
     * @throws OperationFailedException
     */
    public void add(NamedBean<?> bean) throws OperationFailedException {

        Class<?> itemType = bean.getItem().getClass();

        // Find the grouping-root from the item
        Class<?> groupingRoot = findGroupingRoot(itemType);

        listForGroup(groupingRoot).add(bean);
    }

    /**
     * Adds all the named-beans (shallow copy) from source to th ecurrent map
     *
     * @param source where to copy from
     * @throws OperationFailedException
     */
    public void addAll(Define source) throws OperationFailedException {
        for (Class<?> key : source.keySet()) {
            List<NamedBean<AnchorBean<?>>> beans = source.getList(key);
            addList(beans);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AnchorBean<?>> List<NamedBean<T>> getList(Class<?> listType) {

        List<NamedBean<?>> listIn = map.get(listType);

        if (listIn == null) {
            // Nothing there, so we exit early
            return new ArrayList<>();
        }

        // We always create a new list, as a workaround for our inability to cast
        List<NamedBean<T>> listOut = new ArrayList<>();
        for (NamedBean<?> ni : listIn) {
            listOut.add((NamedBean<T>) ni);
        }
        return listOut;
    }

    @Override
    public Define duplicateBean() {

        // We must also copy the map, and duplicate its contents, as otherwise a new empty
        Define out = new Define();
        for (Entry<Class<?>, List<NamedBean<?>>> entry : map.entrySet()) {
            List<NamedBean<?>> dupList = duplicateList(entry.getValue());
            out.map.put(entry.getKey(), dupList);
        }

        return out;
    }

    private void addList(List<NamedBean<AnchorBean<?>>> beans) throws OperationFailedException {
        for (NamedBean<?> nb : beans) {
            add(nb);
        }
    }

    private Set<Class<?>> keySet() {
        return map.keySet();
    }

    private static List<NamedBean<?>> duplicateList(List<NamedBean<?>> in) {
        List<NamedBean<?>> out = new ArrayList<>();
        for (NamedBean<?> nb : in) {
            out.add(nb.duplicateBean());
        }
        return out;
    }

    private static Class<?> findGroupingRoot(Class<?> leaf) throws OperationFailedException {

        Class<?> consider = leaf;
        do {
            if (consider.isAnnotationPresent(GroupingRoot.class)) {
                return consider;
            }

            consider = consider.getSuperclass();
        } while (consider != null);

        throw new OperationFailedException(
                String.format(
                        "Bean-class %s is missing a groupingRoot. This must exist in the class-hierarchy for any item in a NamedDefinitions",
                        leaf));
    }

    /**
     * Gets an existing list for a group, or creates one if it doesn't already exist
     *
     * @return an existing or newly-created list
     */
    private List<NamedBean<?>> listForGroup(Class<?> groupingRoot) {
        return map.computeIfAbsent(groupingRoot, key -> new ArrayList<>());
    }
}
