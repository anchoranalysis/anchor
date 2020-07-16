/* (C)2020 */
package org.anchoranalysis.bean;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set of strings.
 *
 * <p>We use TreeSet to maintain an ordering that is consistent and meaningful for the iterator().
 *
 * <p>An example:
 *
 * <pre>{@code
 *    <datasets config-class="org.anchoranalysis.bean.StringSet" config-factory="stringSet">
 * 	<item>first_dataset</item>
 * 	<item>second_dataset</item>
 * 	<item>some_other_dataset</item>
 *   </datasets>
 * }</pre>
 *
 * @author Owen Feehan
 */
public class StringSet extends AnchorBean<StringSet> implements StringBeanCollection {

    private Set<String> set = new TreeSet<>();

    @Override
    public void add(String s) {
        set.add(s);
    }

    @Override
    public boolean contains(String s) {
        return set.contains(s);
    }

    public Set<String> set() {
        return set;
    }

    @Override
    public Iterator<String> iterator() {
        return set.iterator();
    }

    //

    /**
     * Duplicate the bean
     *
     * <p>NOTE: We need to specifically-implement it as the GeneralBean functionality won't work
     * with this implementation, as it uses non-default initialization (using a config-factory)
     */
    @Override
    public StringSet duplicateBean() {
        StringSet out = new StringSet();
        out.set.addAll(set);
        return out;
    }
}
