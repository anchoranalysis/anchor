/*-
 * #%L
 * anchor-plot
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

package org.anchoranalysis.plot.index;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Iterator;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.plot.GetForSeries;
import org.jfree.data.category.CategoryDataset;

/**
 * Base class for plots involving categorical data types.
 *
 * @param <T>
 * @param <S>
 */
public abstract class PlotIndexBaseCategorical<T, S extends CategoryDataset>
        extends PlotIndexBase<T, S> {

    private final GetForSeries<T, String> labelGetter;
    private final GetForSeries<T, Paint> colorGetter;

    private ArrayList<Paint> seriesColors = new ArrayList<>();

    /**
     * @param graphName
     * @param seriesNames
     * @param labelGetter
     * @param colorGetter color-getter or null to use default colors
     */
    protected PlotIndexBaseCategorical(
            String graphName,
            String[] seriesNames,
            GetForSeries<T, String> labelGetter,
            GetForSeries<T, Paint> colorGetter) {
        super(graphName, seriesNames);
        this.colorGetter = colorGetter;
        this.labelGetter = labelGetter;
    }

    /**
     * Creates a sample dataset
     *
     * @throws GetOperationFailedException
     */
    @Override
    protected S createDataset(Iterator<T> itr) throws GetOperationFailedException {

        final S dataset = createDefaultDataset();

        seriesColors.clear();
        while (itr.hasNext()) {
            T item = itr.next();

            for (int s = 0; s < getNumberSeries(); s++) {
                String label = labelGetter.get(item, s);

                addLabelToDataset(dataset, item, s, getSeriesNameFor(s), label);

                if (colorGetter != null) {
                    seriesColors.add(colorGetter.get(item, s));
                }
            }
        }

        return dataset;
    }

    protected abstract void addLabelToDataset(
            S dataset, T item, int index, String seriesName, String label)
            throws GetOperationFailedException;

    protected abstract S createDefaultDataset();

    protected boolean hasColorGetter() {
        return colorGetter != null;
    }

    protected ArrayList<Paint> getSeriesColors() {
        return seriesColors;
    }
}
