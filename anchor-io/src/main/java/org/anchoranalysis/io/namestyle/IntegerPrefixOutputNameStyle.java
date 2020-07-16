/* (C)2020 */
package org.anchoranalysis.io.namestyle;

public class IntegerPrefixOutputNameStyle extends IntegerOutputNameStyle {

    /** */
    private static final long serialVersionUID = -3128734431534880903L;

    public IntegerPrefixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    private IntegerPrefixOutputNameStyle(IntegerPrefixOutputNameStyle src) {
        super(src);
    }

    public IntegerPrefixOutputNameStyle(String outputName, int numDigitsInteger) {
        super(outputName, numDigitsInteger);
    }

    @Override
    public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
        return new IntegerPrefixOutputNameStyle(this.getOutputName(), numDigits);
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new IntegerPrefixOutputNameStyle(this);
    }

    @Override
    protected String combineIntegerAndOutputName(String outputName, String integerFormatString) {
        return integerFormatString + "_" + outputName;
    }
}
