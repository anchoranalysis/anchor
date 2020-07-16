/* (C)2020 */
package org.anchoranalysis.anchor.plot.index;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

/**
 * @author Owen Feehan
 * @param <T> container item-type
 * @param <S> dataset-type
 */
public abstract class GraphIndexBase<T, S extends Dataset> {

    private String graphName;
    private String[] seriesNames;

    private int numSeries;

    private GraphColorScheme graphColorScheme = new GraphColorScheme();

    private GraphAxisLabels labels = new GraphAxisLabels();

    /**
     * Constructor
     *
     * @param graphName
     * @param seriesNames
     */
    public GraphIndexBase(String graphName, String[] seriesNames) {

        this.graphName = graphName;
        this.seriesNames = seriesNames;

        numSeries = seriesNames.length;
    }

    public GraphInstance createWithRangeLimits(
            Iterator<T> itr, Optional<AxisLimits> proposedRangeLimits) throws CreateException {

        try {
            if (numSeries <= 0) {
                throw new CreateException("There must be at least one seriesName");
            }

            // This will create the dataset
            S dataset = createDataset(itr);

            // We populate our outgoing limits from the plot
            if (!proposedRangeLimits.isPresent()) {
                proposedRangeLimits = rangeLimitsIfEmpty(dataset);
            }

            // based on the dataset we create the chart
            final JFreeChart chart = createChart(dataset, this.graphName, proposedRangeLimits);

            // For now we don't set any limits for bar graphs
            return new GraphInstance(chart, proposedRangeLimits);
        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    public GraphColorScheme getGraphColorScheme() {
        return graphColorScheme;
    }

    public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
        this.graphColorScheme = graphColorScheme;
    }

    public GraphAxisLabels getLabels() {
        return labels;
    }

    protected int getNumSeries() {
        return numSeries;
    }

    protected abstract Optional<AxisLimits> rangeLimitsIfEmpty(S dataset);

    protected abstract JFreeChart createChart(
            S dataset, String title, Optional<AxisLimits> rangeLimits);

    protected abstract S createDataset(Iterator<T> itr) throws GetOperationFailedException;

    protected boolean multipleSeries() {
        return numSeries > 1;
    }

    protected String getSeriesNameFor(int index) {
        return seriesNames[index];
    }
}
