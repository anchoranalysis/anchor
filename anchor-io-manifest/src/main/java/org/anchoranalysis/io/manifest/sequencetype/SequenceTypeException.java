/* (C)2020 */
package org.anchoranalysis.io.manifest.sequencetype;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class SequenceTypeException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 8838380239853844984L;

    public SequenceTypeException(String string) {
        super(string);
    }

    public SequenceTypeException(Exception exc) {
        super(exc);
    }
}
