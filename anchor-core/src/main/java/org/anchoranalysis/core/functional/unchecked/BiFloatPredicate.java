package org.anchoranalysis.core.functional.unchecked;

/**
 * Tests two arguments of type {@code float} as a predicate.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface BiFloatPredicate {

    /**
     * Tests the predicate.
     *
     * @param first first argument.
     * @param second second argument.
     * @return result of the predicate.
     */
    boolean test(float first, float second);
}
