/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.error;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class UnitValueException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public UnitValueException(String string) {
        super(string);
    }

    public UnitValueException(Exception exc) {
        super(exc);
    }
}
