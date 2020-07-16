/* (C)2020 */
package org.anchoranalysis.io.namestyle;

public class SimpleOutputNameStyle extends OutputNameStyle {

    /** */
    private static final long serialVersionUID = 7800246042849181557L;

    private String outputName;

    public SimpleOutputNameStyle(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public String getPhysicalName() {
        return getOutputName();
    }

    @Override
    public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
        return new IntegerSuffixOutputNameStyle(getOutputName(), numDigits);
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
    public OutputNameStyle duplicate() {
        return new SimpleOutputNameStyle(outputName);
    }
}
