package org.anchoranalysis.anchor.plot.index;

/*-
 * #%L
 * anchor-plot
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Iterator;

import org.anchoranalysis.anchor.plot.index.GraphIndexBase;
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
public abstract class GraphIndexBaseCategorical<T,S extends CategoryDataset> extends GraphIndexBase<T,S> {

	private GetForSeries<T,String> labelGetter;
	private GetForSeries<T,Paint> colorGetter;
		
	private ArrayList<Paint> seriesColors = new ArrayList<>(); 

	/**
	 * 
	 * @param graphName
	 * @param seriesNames
	 * @param labelGetter
	 * @param colorGetter color-getter or NULL to use default colors
	 * @throws InitException
	 */
	public GraphIndexBaseCategorical(
		String graphName,
		String[] seriesNames,
		GetForSeries<T,String> labelGetter,
		GetForSeries<T,Paint> colorGetter
	) throws InitException {
		super(graphName, seriesNames);
		this.colorGetter = colorGetter;
		this.labelGetter = labelGetter;
	}
	
	/**
     * Creates a sample dataset 
	 * @throws GeneralException 
	 * @throws GetOperationFailedException 
     */
    @Override
	protected S createDataset( Iterator<T> itr ) throws GetOperationFailedException {

    	final S dataset = createDefaultDataset();
    	
		seriesColors.clear();
    	while( itr.hasNext() ) {
    		T item = itr.next();
    		
    		for (int s=0; s<getNumSeries(); s++) {
    			String label = labelGetter.get(item, s);
    			
    			addLabelToDataset(dataset, item, s, getSeriesNameFor(s), label);
	    		
	    		if (colorGetter!=null) {
	    			seriesColors.add( colorGetter.get(item,s) );
	    		}
    		}
    	}
    	
    	return dataset;
	}
    
	protected abstract void addLabelToDataset(S dataset, T item, int index, String seriesName, String label) throws GetOperationFailedException;
	
	protected abstract S createDefaultDataset();
	
	protected boolean hasColorGetter() {
    	return colorGetter!=null;
    }

	protected ArrayList<Paint> getSeriesColors() {
		return seriesColors;
	}
}