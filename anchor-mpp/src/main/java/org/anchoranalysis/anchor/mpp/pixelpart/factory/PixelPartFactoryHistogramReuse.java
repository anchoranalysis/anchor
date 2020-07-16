/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart.factory;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.pixelpart.PixelPart;
import org.anchoranalysis.anchor.mpp.pixelpart.PixelPartHistogram;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramArray;

public class PixelPartFactoryHistogramReuse implements PixelPartFactory<Histogram> {

    private int maxSize = 100;
    private List<Histogram> listUnused = new ArrayList<>();

    @Override
    public PixelPart<Histogram> create(int numSlices) {
        return new PixelPartHistogram(numSlices, this::createHistogram);
    }

    @Override
    public void addUnused(Histogram part) {
        if (listUnused.size() < maxSize) {
            listUnused.add(part);
        }
    }

    private Histogram createHistogram() {
        if (!listUnused.isEmpty()) {
            // we retrieve one from the unused list and reset it

            Histogram h = listUnused.remove(0);
            h.reset();
            return h;

        } else {
            return new HistogramArray(255);
        }
    }
}
