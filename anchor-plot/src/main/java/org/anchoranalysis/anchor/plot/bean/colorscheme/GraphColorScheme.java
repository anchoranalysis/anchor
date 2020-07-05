package org.anchoranalysis.anchor.plot.bean.colorscheme;

/*
 * #%L
 * anchor-plot
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


import java.awt.Color;

import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.color.RGBColorBean;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;

public class GraphColorScheme extends AnchorBean<GraphColorScheme> {

	// START BEAN PROPERTIES
	@BeanField
	private RGBColorBean backgroundColor = new RGBColorBean(255,255,255);
	
	@BeanField
	private RGBColorBean axisColor = new RGBColorBean(0,0,0);
	
	@BeanField
	private RGBColorBean plotBackgroundColor = new RGBColorBean(Color.lightGray);
	
	@BeanField
	private RGBColorBean plotGridlineColor = new RGBColorBean(Color.white);
	// END BEAN PROPERTIES
	
	    
    public void colorAxis( Axis axis ) {
    	axis.setLabelPaint( axisColor.toAWTColor() );
        axis.setAxisLinePaint( axisColor.toAWTColor() );
        axis.setTickLabelPaint( axisColor.toAWTColor() );
        axis.setTickMarkPaint( axisColor.toAWTColor() );
    }
    
    public void colorChart( JFreeChart chart ) {
        chart.setBackgroundPaint( backgroundColor.toAWTColor() );
        chart.getTitle().setPaint( axisColor.toAWTColor() );
    }

    public void colorPlot( XYPlot plot ) {
    	plot.setBackgroundPaint( plotBackgroundColor.toAWTColor() );
    //  plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint( plotGridlineColor.toAWTColor() );
        plot.setRangeGridlinePaint( plotGridlineColor.toAWTColor() );
    }
    
    public void colorPlot( CategoryPlot plot ) {
    	plot.setBackgroundPaint( plotBackgroundColor.toAWTColor() );
    //  plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint( plotGridlineColor.toAWTColor() );
        plot.setRangeGridlinePaint( plotGridlineColor.toAWTColor() );
    }
    
    public void colorLegend( LegendTitle legend ) {
    	legend.setBackgroundPaint( backgroundColor.toAWTColor() );
    	legend.setItemPaint( axisColor.toAWTColor() );
    }
    
	public RGBColorBean getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(RGBColorBean backgroundColor) {
		this.backgroundColor = backgroundColor;
	}


	public RGBColorBean getAxisColor() {
		return axisColor;
	}


	public void setAxisColor(RGBColorBean color) {
		this.axisColor = color;
	}

	public RGBColorBean getPlotBackgroundColor() {
		return plotBackgroundColor;
	}

	public void setPlotBackgroundColor(RGBColorBean plotBackgroundColor) {
		this.plotBackgroundColor = plotBackgroundColor;
	}

	public RGBColorBean getPlotGridlineColor() {
		return plotGridlineColor;
	}

	public void setPlotGridlineColor(RGBColorBean plotGridlineColor) {
		this.plotGridlineColor = plotGridlineColor;
	}
	

}
