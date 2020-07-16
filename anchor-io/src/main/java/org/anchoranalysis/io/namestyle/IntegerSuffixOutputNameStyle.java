/* (C)2020 */
package org.anchoranalysis.io.namestyle;

public class IntegerSuffixOutputNameStyle extends IntegerOutputNameStyle {

    /** */
    private static final long serialVersionUID = -3128734431534880903L;

    public IntegerSuffixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    private IntegerSuffixOutputNameStyle(IntegerSuffixOutputNameStyle src) {
        super(src);
    }

    public IntegerSuffixOutputNameStyle(String outputName, int numDigitsInteger) {
        super(outputName, numDigitsInteger);
    }

    @Override
    public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
        return new IntegerSuffixOutputNameStyle(this.getOutputName(), numDigits);
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new IntegerSuffixOutputNameStyle(this);
    }

    @Override
    protected String combineIntegerAndOutputName(String outputName, String integerFormatString) {
        return outputName + "_" + integerFormatString;
    }
}
