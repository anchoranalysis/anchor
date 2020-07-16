/* (C)2020 */
package org.anchoranalysis.io.namestyle;

public abstract class IndexableOutputNameStyle extends OutputNameStyle {

    /** */
    private static final long serialVersionUID = -2393013576294162543L;

    private String outputName;

    // Only for deserialization
    public IndexableOutputNameStyle() {
        super();
    }

    public IndexableOutputNameStyle(String outputName) {
        this.outputName = outputName;
    }

    /**
     * Copy constructor
     *
     * @param src source
     */
    protected IndexableOutputNameStyle(IndexableOutputNameStyle src) {
        this.outputName = src.outputName;
    }

    protected abstract String outputFormatString();

    public String getPhysicalName(int index) {
        return getPhysicalName(Integer.toString(index));
    }

    /** The full physical name written to the file, including prefix, suffix, index etc. */
    public String getPhysicalName(String index) {
        return nameFromOutputFormatString(outputFormatString(), index);
    }

    @Override
    public abstract IndexableOutputNameStyle duplicate();

    /** Constructs a full name from the output format string and an index */
    protected abstract String nameFromOutputFormatString(String outputFormatString, String index);

    @Override
    public String getOutputName() {
        return outputName;
    }

    @Override
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public String getPhysicalName() {
        throw new UnsupportedOperationException(
                "an index is required for getPhysicalName in this class");
    }
}
