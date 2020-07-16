/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class MultipleFilesException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 445130185869796373L;

    public MultipleFilesException(String string) {
        super(string);
    }
}
