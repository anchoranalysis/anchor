/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramCreator;

public class PixelPartHistogram implements PixelPart<Histogram> {

    private Histogram combined;
    private List<Histogram> list;

    public PixelPartHistogram(int numSlices, HistogramCreator histogramFactory) {

        combined = histogramFactory.create();

        list = new ArrayList<>();
        for (int i = 0; i < numSlices; i++) {
            list.add(histogramFactory.create());
        }
    }

    @Override
    public Histogram getSlice(int sliceID) {
        return list.get(sliceID);
    }

    @Override
    public void addForSlice(int sliceID, int val) {
        list.get(sliceID).incrVal(val);
        combined.incrVal(val);
    }

    @Override
    public Histogram getCombined() {
        return combined;
    }

    @Override
    public void cleanUp(PixelPartFactory<Histogram> factory) {
        factory.addUnused(combined);
        for (int i = 0; i < list.size(); i++) {
            factory.addUnused(list.get(i));
        }
    }

    @Override
    public int numSlices() {
        return list.size();
    }
}
