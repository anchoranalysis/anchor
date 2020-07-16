/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.set;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class UpdateMarkSetException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -3084951824689469370L;

    public UpdateMarkSetException(String string) {
        super(string);
    }

    public UpdateMarkSetException(Exception exc) {
        super(exc);
    }
}
