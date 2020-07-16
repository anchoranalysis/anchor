/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart;

import java.util.ArrayList;
import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;

/**
 * @author Owen Feehan
 * @param <T> part-type
 */
public class IndexByChnl<T> {

    private ArrayList<IndexByRegion<T>> delegate = new ArrayList<>();

    public boolean add(IndexByRegion<T> e) {
        return delegate.add(e);
    }

    public void init(PixelPartFactory<T> factory, int numChnl, int numRegions, int numSlices) {

        for (int i = 0; i < numChnl; i++) {
            delegate.add(new IndexByRegion<>(factory, numRegions, numSlices));
        }
    }

    public IndexByRegion<T> get(int index) {
        return delegate.get(index);
    }

    public int size() {
        return delegate.size();
    }

    public void cleanUp(PixelPartFactory<T> factory) {
        for (int i = 0; i < delegate.size(); i++) {
            delegate.get(i).cleanUp(factory);
        }
    }
}
