/* (C)2020 */
package org.anchoranalysis.bean.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * If a Bean is misconfigured
 *
 * <p>e.g. missing a value that is required or with a value that violates the constraints of the
 * bean-field
 *
 * <p>There should always be a sensible message, even if we nest another exception.
 *
 * @author Owen Feehan
 */
public class BeanMisconfiguredException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = -6966810405755062033L;

    public BeanMisconfiguredException(String string) {
        super(string);
    }

    public BeanMisconfiguredException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
