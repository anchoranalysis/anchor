package org.anchoranalysis.anchor.graph.bean;

/*
 * #%L
 * anchor-graph
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
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;

// The sub classes don't have any BeanFields as a rule
public abstract class GraphDefinition<ItemType> extends AnchorBean<GraphDefinition<ItemType>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8558887934356230941L;

	/**
	 * Creates a graph
	 * 
	 * @param items the items which determine the graph contents
	 * @param domainLimits limits on X axis
	 * @param rangeLimits limits on Y axis or NULL (and then they are guessed automatically) 
	 * @return
	 * @throws CreateException
	 */
	public abstract GraphInstance create( Iterator<ItemType> items, AxisLimits domainLimits, AxisLimits rangeLimits ) throws CreateException;
	
	public abstract boolean isItemAccepted( ItemType item );
	
	public abstract String getTitle();
	
	public abstract String getShortTitle();
}
