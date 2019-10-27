package org.anchoranalysis.core.text;

/*
 * #%L
 * anchor-core
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


import java.text.DecimalFormat;

public class TypedValue {
	
	private String value;
	private boolean isNumeric;
	
	
	public TypedValue( String value ) {
		this.value = value;
		this.isNumeric = false;
	}
	
	public TypedValue(int value) {
		super();
		this.value = Integer.toString(value);
		this.isNumeric = true;
	}
	
	public TypedValue(double value, int numDecimalPlaces) {
		super();
		if (Double.isNaN(value)) {
			this.value = "NaN";
			this.isNumeric = false;
		} else {
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setMinimumFractionDigits(numDecimalPlaces);
			decimalFormat.setGroupingUsed(false);
			this.value = decimalFormat.format(value);
			this.isNumeric = true;
		}
	}
	
	
	public TypedValue(String value, boolean isNumeric) {
		super();
		this.value = value;
		this.isNumeric = isNumeric;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isNumeric() {
		return isNumeric;
	}

	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}
}
