/* (C)2020 */
package org.anchoranalysis.anchor.plot.index;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Iterator;
import org.anchoranalysis.anchor.plot.GetForSeries;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.jfree.data.category.CategoryDataset;

/**
 * Base for categorical data types
 *
 * @param <T>
 * @param <S>
 */
public abstract class GraphIndexBaseCategorical<T, S extends CategoryDataset>
        extends GraphIndexBase<T, S> {

    private GetForSeries<T, String> labelGetter;
    private GetForSeries<T, Paint> colorGetter;

    private ArrayList<Paint> seriesColors = new ArrayList<>();

    /**
     * @param graphName
     * @param seriesNames
     * @param labelGetter
     * @param colorGetter color-getter or NULL to use default colors
     * @throws InitException
     */
    public GraphIndexBaseCategorical(
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
     * @throws GeneralException
     * @throws GetOperationFailedException
     */
    @Override
    protected S createDataset(Iterator<T> itr) throws GetOperationFailedException {

        final S dataset = createDefaultDataset();

        seriesColors.clear();
        while (itr.hasNext()) {
            T item = itr.next();

            for (int s = 0; s < getNumSeries(); s++) {
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
