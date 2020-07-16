/* (C)2020 */
package org.anchoranalysis.io.manifest.sequencetype;

import java.util.HashSet;
import java.util.Iterator;
import org.anchoranalysis.core.index.container.OrderProvider;

public class SetSequenceType extends SequenceType {

    /** */
    private static final long serialVersionUID = 8292809183713424555L;

    private HashSet<String> set;

    public SetSequenceType() {
        set = new HashSet<>();
    }

    @Override
    public int nextIndex(int index) {
        if (index < 0) {
            return -1;
        }
        if (index >= (set.size() - 1)) {
            return -1;
        }
        return index + 1;
    }

    @Override
    public int previousIndex(int index) {
        if (index <= 0) {
            return -1;
        }
        if (index >= set.size()) {
            return -1;
        }
        return index - 1;
    }

    @Override
    public int previousEqualIndex(int index) {
        return index;
    }

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return set.size() - 1;
    }

    @Override
    public String getName() {
        return "setSequenceType";
    }

    @Override
    public void update(String index) throws SequenceTypeException {
        set.add(index);
    }

    @Override
    public int getNumElements() {
        return set.size();
    }

    @Override
    public OrderProvider createOrderProvider() {

        OrderProviderHashMap map = new OrderProviderHashMap();
        map.addStringSet(set);
        return map;
    }

    @Override
    public String indexStr(int index) {

        assert (!set.isEmpty());

        Iterator<String> itr = set.iterator();

        String ret = "";
        for (int i = 0; i <= index; i++) {
            assert (itr.hasNext());
            ret = itr.next();
        }
        return ret;
    }
}
