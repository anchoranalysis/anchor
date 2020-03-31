package org.anchoranalysis.anchor.mpp.graph.bean;

/*
 * #%L
 * anchor-mpp-graph
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.Iterator;

import org.anchoranalysis.anchor.graph.AxisLimits;
import org.anchoranalysis.anchor.graph.GraphInstance;
import org.anchoranalysis.anchor.graph.bean.GraphDefinition;
import org.anchoranalysis.anchor.graph.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.graph.index.BarChart;
import org.anchoranalysis.anchor.mpp.graph.NRGGraphItem;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarNRGBreakdown extends GraphDefinition<NRGGraphItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8653709262024962761L;
	private BarChart<NRGGraphItem> delegate;
	
	public GraphDefinitionBarNRGBreakdown() throws InitException {
		
		delegate = new BarChart<>(
				getTitle(),
				new String[]{"NRG Total"},
				(NRGGraphItem item, int seriesNum) -> item.getObjectID(), 
				(NRGGraphItem item, int seriesNum) ->  item.getNrg(),
				(NRGGraphItem item, int seriesNum) -> item.getPaint(),
				false
		);
		delegate.getLabels().setX("Mark");
		delegate.getLabels().setY("NRG Coefficient");
	}

	@Override
	public GraphInstance create( Iterator<NRGGraphItem> itr, AxisLimits domainLimits, AxisLimits rangeLimits ) throws CreateException {
		return delegate.create( itr, domainLimits, rangeLimits );
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