/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel.proposer;

import java.util.ArrayList;
import java.util.Iterator;

public class WeightedKernelList<T> implements Iterable<WeightedKernel<T>> {

    private ArrayList<WeightedKernel<T>> delegate = new ArrayList<>();

    public boolean add(WeightedKernel<T> e) {
        return delegate.add(e);
    }

    @Override
    public Iterator<WeightedKernel<T>> iterator() {
        return delegate.iterator();
    }

    public int size() {
        return delegate.size();
    }

    public WeightedKernel<T> get(int index) {
        return delegate.get(index);
    }
}
