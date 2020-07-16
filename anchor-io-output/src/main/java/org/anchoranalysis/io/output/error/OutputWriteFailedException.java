/* (C)2020 */
package org.anchoranalysis.io.output.error;

import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.core.error.friendly.IFriendlyException;

public class OutputWriteFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1036971819028250342L;

    public OutputWriteFailedException(String s) {
        super(s);
    }

    public OutputWriteFailedException(String s, IFriendlyException e) {
        super(s + ": " + e.friendlyMessageHierarchy(), null);
    }

    public OutputWriteFailedException(String s, AnchorCombinableException e) {
        super(s, e.summarize());
    }

    public OutputWriteFailedException(Throwable e) {
        super("", e);
    }
}
