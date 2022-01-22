package org.anchoranalysis.core.functional.unchecked;

/**
 * Tests two arguments of type {@code int} as a predicate.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface BiIntPredicate {

    /**
     * Tests the predicate.
     *
     * @param first first argument.
     * @param second second argument.
     * @return result of the predicate.
     */
    boolean test(int first, int second);
}
