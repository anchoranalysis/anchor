package org.anchoranalysis.bean.permute.property;

/*
 * #%L
 * anchor-bean
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


import java.util.Iterator;

import org.anchoranalysis.bean.annotation.BeanField;

/**
 * An arithmetic sequence of doubles, obtained by dividing an integer sequence by a divider
 * 
 * @author Owen Feehan
 * 
 * @param T permutation type
 *
 */
public class PermutePropertySequenceInteger extends PermutePropertyWithPath<Integer> {

	// START BEAN PROPERTIES
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Where the sequence starts
	 */
	@BeanField
	private int start = 0;
	
	/**
	 * How much to increment by
	 */
	@BeanField
	private int increment = 1;

	/**
	 * The final value. Equal to this value is included. Anything higher is not allowed. 
	 */
	@BeanField
	private int end = 1;
	// END BEAN PROPERTIES

	@Override
	public Iterator<Integer> propertyValues() {
		return propertyValuesDefinitelyInteger();
	}
	
	public Iterator<Integer> propertyValuesDefinitelyInteger() {
		return new IntegerRange(start,end,increment);
	}
		
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	
	@Override
	public String nameForPropValue(Integer value) {
		int numDigitsEnd = Integer.toString(end).length();
		if (numDigitsEnd==1) {
			return value.toString();
		} else {
			String format = String.format("%%0%dd", numDigitsEnd);
			return String.format(format, value);
		}
	}

}
