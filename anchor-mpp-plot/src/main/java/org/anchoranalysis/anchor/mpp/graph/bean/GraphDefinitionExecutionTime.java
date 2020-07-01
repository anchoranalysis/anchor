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
import java.util.Optional;
import java.util.function.Function;

import org.anchoranalysis.anchor.graph.AxisLimits;
import org.anchoranalysis.anchor.graph.GraphInstance;
import org.anchoranalysis.anchor.graph.bean.GraphDefinition;
import org.anchoranalysis.anchor.graph.index.BarChart;
import org.anchoranalysis.anchor.mpp.graph.execution.ExecutionTimeItem;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

public class GraphDefinitionExecutionTime extends GraphDefinition<ExecutionTimeItem> {

	private BarChart<ExecutionTimeItem> delegate;
	
	public GraphDefinitionExecutionTime() throws InitException {
		delegate = new BarChart<>(
				getTitle(),
				new String[]{"Execution Time"},
				(ExecutionTimeItem item, int seriesNum) -> checkFirstSeries(
					item,
					seriesNum,
					a -> a.getObjectID()
				),
				(ExecutionTimeItem item, int seriesNum) -> checkFirstSeries(
					item,
					seriesNum,
					a -> (double) a.getExecutionTime()
				),
				null,
				false
		);
		delegate.getLabels().setX("");
		delegate.getLabels().setY("Execution Time");
	}
	
	private static <S,T> T checkFirstSeries( S item, int seriesNum, Function<S,T> func ) {
		switch( seriesNum ) {
		case 0:
			return func.apply(item);
		default:
			throw new AnchorFriendlyRuntimeException(
				String.format("seriesNum must be 0 but instead it is %d", seriesNum)
			);	
		}
	}
	
	@Override
	public GraphInstance create(Iterator<ExecutionTimeItem> items,
			Optional<AxisLimits> domainLimits, Optional<AxisLimits> rangeLimits) throws CreateException {
		return delegate.create(items, domainLimits, rangeLimits);
	}

	@Override
	public boolean isItemAccepted(ExecutionTimeItem item) {
		return true;
	}

	@Override
	public String getTitle() {
		return "Execution Times";
	}

	@Override
	public String getShortTitle() {
		return getTitle();
	}
}
