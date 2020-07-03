package org.anchoranalysis.feature.input.descriptor;

import org.anchoranalysis.feature.input.FeatureInput;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*
 * #%L
 * anchor-feature
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


/**
 * For features that are compatible with all types of inputs
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FeatureInputGenericDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputGenericDescriptor INSTANCE = new FeatureInputGenericDescriptor();
	
	@Override
	public boolean isCompatibleWithEverything() {
		return true;
	}

	@Override
	public Class<?> inputClass() {
		return FeatureInput.class;
	}
}
