package org.anchoranalysis.image.feature.bean.pixelwise;



import java.util.List;
import java.util.Optional;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.params.KeyValueParams;

/*-
 * #%L
 * anchor-image-feature
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

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.histogram.Histogram;

/** Calculates a per-pixel score */
public abstract class PixelScore extends AnchorBean<PixelScore> {
	
	/**
	 * Initializes the pixels-score.
	 * 
	 * <p>Must be called before calc()</p>
	 * 
	 * @param histograms one or more histograms associated with this calculation
	 * @param keyValueParams optional key-value params associated with this calculation
	 * @throws InitException if anything goes wrong
	 */
	public void init( List<Histogram> histograms, Optional<KeyValueParams> keyValueParams) throws InitException {
		// TO be overridden if needed
	}
	
	public abstract double calc(int[] pixelVals) throws FeatureCalcException;
}
