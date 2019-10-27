package org.anchoranalysis.anchor.mpp.proposer.error;

/*
 * #%L
 * anchor-mpp
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


import ch.ethz.biol.cell.mpp.mark.Mark;

public class ErrorNodeNull extends ErrorNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5512508477564211253L;

	@Override
	public ErrorNode add(String errorMessage) {
		// do nothing
		return this;
	}

	@Override
	public ErrorNode add(String errorMessage, Mark mark) {
		return this;
	}

	@Override
	public ErrorNode addFormatted(String formatString, Object... args) {
		return this;
	}

	@Override
	public ErrorNode addIter(int i) {
		return this;
	}

	@Override
	public ErrorNode addBean(String propertyName, Object object) {
		return this;
	}

	@Override
	public ErrorNode add(Exception e) {
		return this;
	}

	@Override
	public void addErrorDescription(StringBuilder sb) {
		
	}
	

}