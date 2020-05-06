package org.anchoranalysis.io.namestyle;

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
