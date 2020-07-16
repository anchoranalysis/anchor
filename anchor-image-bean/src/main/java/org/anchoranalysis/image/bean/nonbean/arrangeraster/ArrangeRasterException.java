/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.arrangeraster;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class ArrangeRasterException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -6247544738100419019L;

    public ArrangeRasterException(String string) {
        super(string);
    }

    public ArrangeRasterException(Exception exc) {
        super(exc);
    }
}
