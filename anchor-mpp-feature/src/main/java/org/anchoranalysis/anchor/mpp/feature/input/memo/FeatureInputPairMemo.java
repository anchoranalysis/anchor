package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;

/*-
 * #%L
 * anchor-mpp-feature
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

import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class FeatureInputPairMemo extends FeatureInputNRG {

	private PxlMarkMemo obj1;
	private PxlMarkMemo obj2;
	
	public FeatureInputPairMemo(
		PxlMarkMemo obj1,
		PxlMarkMemo obj2,
		NRGStackWithParams nrgStack
	) {
		super(
			Optional.of(nrgStack)
		);
		this.obj1 = obj1;
		this.obj2 = obj2;
	}
	public PxlMarkMemo getObj1() {
		return obj1;
	}
	public void setObj1(PxlMarkMemo obj1) {
		this.obj1 = obj1;
	}
	public PxlMarkMemo getObj2() {
		return obj2;
	}
	public void setObj2(PxlMarkMemo obj2) {
		this.obj2 = obj2;
	}
}
