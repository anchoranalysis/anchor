package org.anchoranalysis.core.functional.unchecked;

import java.util.function.UnaryOperator;

/**
 * Like a {@link UnaryOperator} but uses the primitive type {@code int}.
 *
 * @author owen Feehan
 */
@FunctionalInterface
public interface UnaryIntOperator {

    /**
     * Apply the operator.
     *
     * @param value the parameter.
     * @return the result of the operator.
     */
    int apply(int value);
}
