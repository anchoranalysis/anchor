package org.anchoranalysis.io.namestyle;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

public abstract class IntegerOutputNameStyle extends IndexableOutputNameStyle {

	private int numDigits;
	
	protected IntegerOutputNameStyle() {
		// Needed for deserialization
	}

	protected IntegerOutputNameStyle(IndexableOutputNameStyle src) {
		super(src);
	}

	protected IntegerOutputNameStyle(String outputName, int numDigits) {
		super(outputName);
		this.numDigits = numDigits;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected String nameFromOutputFormatString(String outputFormatString, String index) {
		int indexInt = Integer.parseInt(index);
		return String.format( outputFormatString, indexInt );
	}
	
	private static String integerFormatSpecifier(int numDigits ) {
		return "%0" + Integer.toString(numDigits) + "d";
	}

	@Override
	protected String outputFormatString() {
		return combineIntegerAndOutputName(
			getOutputName(),
			integerFormatSpecifier(numDigits)
		);
	}

	protected abstract String combineIntegerAndOutputName( String outputName, String integerFormatString );
}
