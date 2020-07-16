/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;

/**
 * @author Owen Feehan
 * @param <T> part-type
 */
public class IndexByRegion<T> {

    private List<PixelPart<T>> list;

    public IndexByRegion(PixelPartFactory<T> factory, int numRegions, int numSlices) {
        list = new ArrayList<>();
        for (int i = 0; i < numRegions; i++) {
            list.add(factory.create(numSlices));
        }
    }

    // Should only be used RO, if we want to maintain integrity with the combined list
    public T getForAllSlices(int regionID) {
        return list.get(regionID).getCombined();
    }

    // Should only be used RO, if we want to maintain integrity with the combined list
    public T getForSlice(int regionID, int sliceID) {
        return list.get(regionID).getSlice(sliceID);
    }

    public void addToPxlList(int regionID, int sliceID, int val) {
        list.get(regionID).addForSlice(sliceID, val);
    }

    public void cleanUp(PixelPartFactory<T> factory) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).cleanUp(factory);
        }
    }

    public int numSlices() {
        return list.get(0).numSlices();
    }
}
