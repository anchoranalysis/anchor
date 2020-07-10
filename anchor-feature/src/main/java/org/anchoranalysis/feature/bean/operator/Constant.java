package org.anchoranalysis.feature.bean.operator;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Constant<T extends FeatureInput> extends Feature<T> {

	// START BEAN PARAMETERS
	@BeanField
	private double value;
	// END BEAN PARAMETERS

	/**
	 * Constructor with a particular value
	 * 
	 * @param value value
	 */
	public Constant( double value ) {
		this.value = value;
	}
	
	/**
	 * Constructor with a particular value and a custom-name
	 * 
	 * @param customName custom-name for feature
	 * @param value value
	 */
	public Constant( String customName, double value ) {
		this.value = value;
		setCustomName(customName);
	}
	
	@Override
	public double calc( SessionInput<T> input ) {
		return value;
	}

	@Override
	public String getParamDscr() {
		return String.format("%2.2f", value );
	}
	
	@Override
	public String getDscrLong() {
		return getParamDscr();
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public Class<? extends FeatureInput> inputType() {
		return FeatureInput.class;
	}

}
