package org.anchoranalysis.image.bean.nonbean.parameters;

import java.util.Optional;

/*-
 * #%L
 * anchor-image-bean
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

import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.histogram.Histogram;

/** Parameters that are optionally associated with BinarySgmn */
public class BinarySegmentationParameters {

	private Optional<Histogram> histogram;
	private Optional<ImageResolution> res;
	
	public BinarySegmentationParameters() {
		this.res = Optional.empty();
		this.histogram = Optional.empty();
	}
	
	public BinarySegmentationParameters(ImageResolution res) {
		this(res, Optional.empty());
	}
	
	/**
	 * Constructor
	 * 
	 * @param res image-resolution
	 * @param histogram a histogram describing the intensity-values of the entire channel
	 */
	public BinarySegmentationParameters(ImageResolution res, Optional<Histogram> histogram) {
		this.res = Optional.of(res);
		this.histogram = histogram;
	}

	public Optional<Histogram> getIntensityHistogram() {
		return histogram;
	}

	public Optional<ImageResolution> getRes() {
		return res;
	}
}