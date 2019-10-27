package org.anchoranalysis.image.feature.pixelwise;

/*
 * #%L
 * anchor-image-feature
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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.image.histogram.Histogram;

public class PixelwiseFeatureInitParams extends FeatureInitParams {

	private List<Histogram> listHist = new ArrayList<>();
	private RandomNumberGenerator re;
	
	public PixelwiseFeatureInitParams( RandomNumberGenerator re ) {
		super();
		this.re = re;
	}
	
	protected PixelwiseFeatureInitParams( FeatureInitParams src ) {
		super(src);
	}
	
	@Override
	public FeatureInitParams duplicate() {
		PixelwiseFeatureInitParams out = new PixelwiseFeatureInitParams( super.duplicate() );
		out.listHist = listHist;
		out.re = re;
		return out;
	}

	public Histogram getHist( int index ) {
		return listHist.get(index);
	}

	public RandomNumberGenerator getRandomNumberGenerator() {
		return re;
	}

	public void addListHist( Histogram hist ) {
		listHist.add( hist );
	}

}
