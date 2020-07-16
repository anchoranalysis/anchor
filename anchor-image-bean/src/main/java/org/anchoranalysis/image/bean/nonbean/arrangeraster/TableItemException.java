/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.arrangeraster;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class TableItemException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -872733425317309830L;

    public TableItemException(String string) {
        super(string);
    }

    public TableItemException(Exception exc) {
        super(exc);
    }
}
