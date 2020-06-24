package org.anchoranalysis.anchor.mpp.bean.cfg;



import org.anchoranalysis.anchor.mpp.bean.mark.factory.MarkFactory;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.bean.NullParamsBean;

/*
 * #%L
 * anchor-mpp
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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;

public class CfgGen extends NullParamsBean<CfgGen> {
	
	// START BEAN PARAMETERS
	@BeanField
	private double referencePoissonIntensity = 1e-5;
	
	// A template mark from which all new marks are copied
	@BeanField
	private MarkFactory templateMark = null;
	// END BEAN PARAMETERS
	
	private IdCounter idCounter;
	
	// Configuration generation
	public CfgGen() {
		// Standard bean constructor
	}
	
	// Constructor
	public CfgGen(MarkFactory templateMark ) {
		this.templateMark = templateMark;
	}
	
	@Override
	public String getBeanDscr() {
		return String.format("%s templateMark=%s, referencePoissonIntensity=%f", getBeanName(), templateMark.toString(), referencePoissonIntensity );
	}


	@Override
	public void onInit() throws InitException {
		super.onInit();
		idCounter = new IdCounter(1);
	}

	public Mark newTemplateMark() {
		assert(templateMark!=null);
		Mark mark = this.templateMark.create();
		mark.setId( idAndIncrement() );
	    return mark;
	}
	
	public int idAndIncrement() {
		assert idCounter!=null;
		return idCounter.getIdAndIncrement();
	}


	public MarkFactory getTemplateMark() {
		return templateMark;
	}


	public void setTemplateMark(MarkFactory templateMark) {
		this.templateMark = templateMark;
	}

	public double getReferencePoissonIntensity() {
		return referencePoissonIntensity;
	}

	public void setReferencePoissonIntensity(double referencePoissonIntensity) {
		this.referencePoissonIntensity = referencePoissonIntensity;
	}

}
