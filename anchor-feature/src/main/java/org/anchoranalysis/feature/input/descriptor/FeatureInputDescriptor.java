package org.anchoranalysis.feature.input.descriptor;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInput;

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


/** Defines the type of inputs the feature accepts */
public interface FeatureInputDescriptor {
	
	/**
	 * Parameters-type
	 * 
	 * @return
	 */
	Class<? extends FeatureInput> inputClass();
	
	/** 
	 * Can these parameters be used with a particular feature?
	 *  
	 * @param paramTypeClass the class of input
	 * @return true iff the feature-input is compatible with {@code paramTypeClass} 
	 *  */
	default boolean isCompatibleWith(Class<? extends FeatureInput> paramTypeClass) {
		return inputClass().isAssignableFrom( paramTypeClass );
	}

	/**
	 * Prefer to keep descriptor whose input-class is a sub-class rather than a super-class
	 * 
	 * @param dscr
	 * @return the favoured descriptor of the two, or Optional.empty() if there is no favourite
	 */
	default Optional<FeatureInputDescriptor> preferToBidirectional( Class<? extends FeatureInput> classFirst, FeatureInputDescriptor dscr ) {
		if (isCompatibleWith(dscr.inputClass())) {
			return Optional.of(dscr);
		}
		
		if (dscr.isCompatibleWith(classFirst)) {
			return Optional.of(this);
		}
		
		return Optional.empty();
	}
}
