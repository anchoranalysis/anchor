/* (C)2020 */
package org.anchoranalysis.io.namestyle;

public class StringSuffixOutputNameStyle extends IndexableOutputNameStyle {

    /** */
    private static final long serialVersionUID = 4582344790084798682L;

    private String outputFormatString;

    public StringSuffixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    public StringSuffixOutputNameStyle(String outputName, String outputFormatString) {
        super(outputName);
        this.outputFormatString = outputFormatString;
    }

    private StringSuffixOutputNameStyle(StringSuffixOutputNameStyle src) {
        super(src);
    }

    @Override
    protected String nameFromOutputFormatString(String outputFormatString, String index) {
        return String.format(outputFormatString, index);
    }

    @Override
    public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
        // Number-of-digits is ignored and not relevant
        return duplicate();
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new StringSuffixOutputNameStyle(this);
    }

    @Override
    protected String outputFormatString() {
        return outputFormatString;
    }
}
