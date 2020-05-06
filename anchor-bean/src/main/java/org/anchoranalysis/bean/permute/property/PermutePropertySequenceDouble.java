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
 */
public class PermutePropertySequenceDouble extends PermutePropertySequence<Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	/**
	 * Divide by
	 */
	@BeanField
	private double divisor = 1.0;


	@Override
	public Iterator<Double> propertyValues() {
		return new DoubleRange();
	}
	// END BEAN PROPERTIES
	
	// http://stackoverflow.com/questions/371026/shortest-way-to-get-an-iterator-over-a-range-of-integers-in-java

	
	public class DoubleRange implements Iterator<Double>
	{
	    private Iterator<Integer> itr;

	    public DoubleRange() {
	        itr = range();
	    }

	    @Override
	    public boolean hasNext() {
	        return itr.hasNext();
	    }

	    @Override
	    public Double next() {
	    	return itr.next() / divisor;
	    }

	    @Override
	    public void remove() {
	        throw new UnsupportedOperationException();
	    }
	}

	public double getDivisor() {
		return divisor;
	}

	public void setDivisor(double divisor) {
		this.divisor = divisor;
	}

	@Override
	public String nameForPropValue(Double value)  {
		return value.toString();
	}

	
}
