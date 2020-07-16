/* (C)2020 */
package org.anchoranalysis.core.name.value;

import java.util.Comparator;

/**
 * Orders two name-values by their name (alphabetic order from standard String.compareTo(String))
 *
 * @author Owen Feehan
 * @param <V> value-type
 */
public class ComparatorOrderByName<V> implements Comparator<NameValue<V>> {

    @Override
    public int compare(NameValue<V> arg0, NameValue<V> arg1) {
        return arg0.getName().compareTo(arg1.getName());
    }
}
