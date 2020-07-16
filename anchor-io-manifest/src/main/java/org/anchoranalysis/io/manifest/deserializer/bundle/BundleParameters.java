/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.bundle;

import java.io.Serializable;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class BundleParameters implements Serializable {

    /** */
    private static final long serialVersionUID = 2877914366652685850L;

    private int bundleSize;
    private SequenceType sequenceType;

    public int getBundleSize() {
        return bundleSize;
    }

    public void setBundleSize(int bundleSize) {
        this.bundleSize = bundleSize;
    }

    public SequenceType getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(SequenceType sequenceType) {
        this.sequenceType = sequenceType;
    }
}
