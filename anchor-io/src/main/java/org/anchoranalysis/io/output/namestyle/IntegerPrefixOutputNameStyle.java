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


public class IntegerPrefixOutputNameStyle extends IndexableOutputNameStyle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3128734431534880903L;
	
	private String outputFormatString;

	private String outputName;
	
	private String prefix;
	private String integerFormatString;
	
	// Only for deserialization
	public IntegerPrefixOutputNameStyle() {
		super();
	}
	
	public IntegerPrefixOutputNameStyle(String outputName, int numDigitsInteger ) {
		this(outputName, "",  "%0" + Integer.toString(numDigitsInteger) + "d_");
	}
	
	public IntegerPrefixOutputNameStyle(String outputName, String integerFormatString) {
		this(outputName, "", integerFormatString);
	}
	
	public IntegerPrefixOutputNameStyle(String outputName, String prefix, String integerFormatString) {
		this.outputName = outputName;
		this.prefix = prefix;
		this.integerFormatString = integerFormatString;
		updateFormatString();
	}
	
	private void updateFormatString() {
		this.outputFormatString = integerFormatString + prefix + outputName;
	}
	

	@Override
	public String getPhysicalName(String index) {
		
		int indexInt = Integer.parseInt(index);
		return String.format( outputFormatString, indexInt );
	}

	@Override
	public String getPhysicalName() {
		throw new UnsupportedOperationException("an index is required for getPhysicalName in this class");
	}

	public String getOutputFormatString() {
		return outputFormatString;
	}

	public void setOutputFormatString(String outputFormatString) {
		this.outputFormatString = outputFormatString;
	}

	@Override
	public IndexableOutputNameStyle deriveIndexableStyle( int numDigits ) {
		return new IntegerPrefixOutputNameStyle( this.getOutputName(), numDigits );
	}

	@Override
	public String getOutputName() {
		return outputName;
	}

	@Override
	public void setOutputName(String outputName) {
		this.outputName = outputName;
		updateFormatString();
	}

	@Override
	public IndexableOutputNameStyle duplicate() {
		return new IntegerPrefixOutputNameStyle(outputName, integerFormatString);
	}

}
