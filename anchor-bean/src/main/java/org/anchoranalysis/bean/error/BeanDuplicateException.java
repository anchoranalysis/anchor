/* (C)2020 */
package org.anchoranalysis.bean.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

/**
 * An exception occurs when the duplication of a bean fails
 *
 * <p>We keep this unchecked, as if a bean is properly configured it should not be thrown.
 *
 * <p>As we already do checks to see if a bean is properly configured it should never (or almost
 * never) occur.
 *
 * <p>We don't want to make needlessly dirty code, as bean duplication occurs, so we keep it as a
 * runtime exception.
 *
 * @author Owen Feehan
 */
public class BeanDuplicateException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1842384434578361294L;

    public BeanDuplicateException(String string) {
        super(string);
    }

    public BeanDuplicateException(Exception exc) {
        super(exc);
    }

    public BeanDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
