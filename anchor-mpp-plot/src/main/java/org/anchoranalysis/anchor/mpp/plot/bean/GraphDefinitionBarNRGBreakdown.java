/* (C)2020 */
package org.anchoranalysis.anchor.mpp.plot.bean;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.plot.NRGGraphItem;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.BarChart;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarNRGBreakdown extends GraphDefinition<NRGGraphItem> {

    private BarChart<NRGGraphItem> delegate;

    public GraphDefinitionBarNRGBreakdown() throws InitException {

        delegate =
                new BarChart<>(
                        getTitle(),
                        new String[] {"NRG Total"},
                        (NRGGraphItem item, int seriesNum) -> item.getObjectID(),
                        (NRGGraphItem item, int seriesNum) -> item.getNrg(),
                        (NRGGraphItem item, int seriesNum) -> item.getPaint(),
                        false);
        delegate.getLabels().setX("Mark");
        delegate.getLabels().setY("NRG Coefficient");
    }

    @Override
    public GraphInstance create(
            Iterator<NRGGraphItem> itr,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        return delegate.createWithRangeLimits(itr, rangeLimits);
    }

    @Override
    public String getTitle() {
        return "NRG Breakdown";
    }

    @Override
    public boolean isItemAccepted(NRGGraphItem item) {
        return true;
    }

    // START BEAN PROPERTIES
    public boolean isShowDomainAxis() {
        return delegate.isShowDomainAxis();
    }

    public void setShowDomainAxis(boolean showDomainAxis) {
        delegate.setShowDomainAxis(showDomainAxis);
    }
    // END BEAN PROPERTIES

    @Override
    public String getShortTitle() {
        return getTitle();
    }

    public GraphColorScheme getGraphColorScheme() {
        return delegate.getGraphColorScheme();
    }

    public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
        delegate.setGraphColorScheme(graphColorScheme);
    }
}
