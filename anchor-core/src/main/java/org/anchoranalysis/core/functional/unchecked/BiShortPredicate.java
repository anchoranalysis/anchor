package org.anchoranalysis.core.functional.unchecked;

/**
 * Tests two arguments of type {@code short} as a predicate.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface BiShortPredicate {

    /**
     * Tests the predicate.
     *
     * @param first first argument.
     * @param second second argument.
     * @return result of the predicate.
     */
    boolean test(short first, short second);
}
