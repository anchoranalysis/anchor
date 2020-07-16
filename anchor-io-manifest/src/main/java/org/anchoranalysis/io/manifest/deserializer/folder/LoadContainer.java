/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder;

import org.anchoranalysis.core.index.container.BoundedIndexContainer;

public class LoadContainer<T> {

    // What do we need to create a frame generically
    private BoundedIndexContainer<T> cntr;

    // If true, then it takes a long time to load a state when requested (use of Bundles, expensive
    // deserialization etc.)
    // If false, then it assumes it does not take a long time to load a state when requested (quick
    // deserialization, in memory, etc.)
    private boolean expensiveLoad;

    public boolean isExpensiveLoad() {
        return expensiveLoad;
    }

    public void setExpensiveLoad(boolean expensiveLoad) {
        this.expensiveLoad = expensiveLoad;
    }

    public BoundedIndexContainer<T> getCntr() {
        return cntr;
    }

    public void setCntr(BoundedIndexContainer<T> cntr) {
        this.cntr = cntr;
    }
}
