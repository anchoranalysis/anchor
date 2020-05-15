package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.mark.Mark;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInputParams;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;

public class FeatureInputMark extends FeatureInputParams {

	private Mark mark;
	private Optional<ImageDim> dim;
	private Optional<KeyValueParams> params;
		
	public FeatureInputMark(Mark mark, Optional<ImageDim> dim) {
		this.mark = mark;
		this.dim = dim;
		this.params = Optional.empty();
	}
	
	public FeatureInputMark(Mark mark, ImageDim dim, KeyValueParams params) {
		this(
			mark,
			Optional.of(dim),
			Optional.of(params)
		);
	}
	
	public FeatureInputMark(Mark mark, Optional<ImageDim> dim, Optional<KeyValueParams> params) {
		super();
		this.mark = mark;
		this.dim = dim;
		this.params = params;
	}

	public Mark getMark() {
		return mark;
	}

	public void setMark(Mark mark) {
		this.mark = mark;
	}

	@Override
	public Optional<ImageRes> getResOptional() {
		return dim.map( ImageDim::getRes );
	}

	@Override
	public Optional<KeyValueParams> getParamsOptional() {
		return params;
	}
	
	public Optional<ImageDim> getDimensionsOptional() {
		return dim;
	}
	
	public ImageDim getDimensionsRequired() throws FeatureCalcException {
		return dim.orElseThrow(
			() -> new FeatureCalcException("Dimensions are required in the input for this operation")
		);
	}
}
