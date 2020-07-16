/* (C)2020 */
package org.anchoranalysis.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of strings (order is defined).
 *
 * <p>An example:
 *
 * <pre>{@code
 *    <datasets config-class="org.anchoranalysis.bean.StringList" config-factory="stringList">
 * 	<item>first_dataset</item>
 * 	<item>second_dataset</item>
 * 	<item>some_other_dataset</item>
 *   </datasets>
 * }</pre>
 *
 * @author Owen Feehan
 */
public class StringList extends AnchorBean<StringList> implements StringBeanCollection {

    private List<String> list = new ArrayList<>();

    @Override
    public void add(String s) {
        list.add(s);
    }

    @Override
    public boolean contains(String s) {
        return list.contains(s);
    }

    public List<String> list() {
        return list;
    }

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }

    //

    /**
     * Duplicate the bean
     *
     * <p>NOTE: We need to specifically-implement it as the GeneralBean functionality won't work
     * with this implementation, as it uses non-default initialization (using a config-factory)
     */
    @Override
    public StringList duplicateBean() {
        StringList out = new StringList();
        out.list.addAll(list);
        return out;
    }
}
