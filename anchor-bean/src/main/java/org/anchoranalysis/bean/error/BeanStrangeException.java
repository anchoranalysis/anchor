/* (C)2020 */
package org.anchoranalysis.bean.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

/**
 * When something quite strange happens that we don't usually want to catch.
 *
 * <p>This type of "black swan" exception is for errors where we aren't going to invest much effort
 * in handling, unless it starts becoming thrown regularly.
 *
 * @author Owen Feehan
 */
public class BeanStrangeException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public BeanStrangeException(String message) {
        super(message);
    }

    public BeanStrangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
