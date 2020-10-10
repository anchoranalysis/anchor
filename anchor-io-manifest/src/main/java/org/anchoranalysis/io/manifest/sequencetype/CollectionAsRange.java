package org.anchoranalysis.io.manifest.sequencetype;

import java.util.Collection;
import java.util.Iterator;
import lombok.AllArgsConstructor;

@AllArgsConstructor class CollectionAsRange implements IncompleteElementRange {
    
    private Collection<String> collection;
    
    @Override
    public int nextIndex(int index) {
        if (index < 0) {
            return -1;
        }
        if (index >= (collection.size() - 1)) {
            return -1;
        }
        return index + 1;
    }

    @Override
    public int previousIndex(int index) {
        if (index <= 0) {
            return -1;
        }
        if (index >= collection.size()) {
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
        return collection.size() - 1;
    }
    
    @Override
    public String stringRepresentationForElement(int elementIndex) {

        assert (!collection.isEmpty());

        Iterator<String> itr = collection.iterator();

        String ret = "";
        for (int i = 0; i <= elementIndex; i++) {
            assert (itr.hasNext());
            ret = itr.next();
        }
        return ret;
    }
}