package org.anchoranalysis.io.output.namestyle;

/*
 * #%L
 * anchor-io
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


// TODO.  It doesn't seem correct that we use outputFormatString as our outputName
// We should fix
public class StringSuffixOutputNameStyle extends IndexableOutputNameStyle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4582344790084798682L;
	
	private String outputName;
	private String outputFormatString;
	
	public StringSuffixOutputNameStyle(String outputName, String outputFormatString) {
		this.outputName = outputName;
		this.outputFormatString = outputFormatString;
	}

	@Override
	public String getPhysicalName(String index) {
		
		return String.format( outputFormatString, index );
	}

	@Override
	public String getPhysicalName() {
		throw new UnsupportedOperationException("an index is required for getPhysicalName in this class");
	}

	@Override
	public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
		return new StringSuffixOutputNameStyle( this.getOutputName(), outputFormatString );
	}
	
	@Override
	public String getOutputName() {
		return outputName;
	}

	@Override
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	@Override
	public IndexableOutputNameStyle duplicate() {
		return new StringSuffixOutputNameStyle(outputName,outputFormatString);
	}

}
