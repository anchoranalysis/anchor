/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
