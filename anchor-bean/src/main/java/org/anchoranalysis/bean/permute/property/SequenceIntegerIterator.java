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
import java.util.NoSuchElementException;

/**
 * Represents a sequence of integers to satisfy {@code start <= i <= end} (in certain increments)
 * 
 * @author Owen Feehan
 *
 */
class SequenceIntegerIterator implements Iterator<Integer> {
	
	private final int end;
	private final int increment;
	
    private int current;
    
    public SequenceIntegerIterator(int start, int end, int increment) {
		super();
		this.current = start;
		this.end = end;
		this.increment = increment;
	}

    @Override
    public boolean hasNext() {
        return current <= end;
    }

    @Override
    public Integer next() {
    	
    	int out = current;
    	
    	if (current>end) {
    		throw new NoSuchElementException();
    	}
    	
    	current += increment;
    	
    	return out;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}