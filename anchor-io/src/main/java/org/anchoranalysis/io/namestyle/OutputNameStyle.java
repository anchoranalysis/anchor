/* (C)2020 */
package org.anchoranalysis.io.namestyle;

import java.io.Serializable;

public abstract class OutputNameStyle implements Serializable {

    /** */
    private static final long serialVersionUID = 7757474603700575166L;

    // Only for deserialization
    public OutputNameStyle() {}

    public abstract String getPhysicalName();

    // The output name which refers to a particular category of output
    public abstract String getOutputName();

    public abstract void setOutputName(String outputName);

    public abstract OutputNameStyle duplicate();

    public abstract IndexableOutputNameStyle deriveIndexableStyle(int numDigits);
}
