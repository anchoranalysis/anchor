/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart.factory;

import org.anchoranalysis.anchor.mpp.pixelpart.PixelPart;
import org.anchoranalysis.anchor.mpp.pixelpart.PixelPartHistogram;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramCreator;
import org.anchoranalysis.image.histogram.HistogramCreatorSimple;

public class PixelPartFactoryHistogram implements PixelPartFactory<Histogram> {

    private HistogramCreator factorySimple = new HistogramCreatorSimple();

    @Override
    public PixelPart<Histogram> create(int numSlices) {
        return new PixelPartHistogram(numSlices, factorySimple);
    }

    @Override
    public void addUnused(Histogram part) {
        // NOTHING TO DO
    }
}
