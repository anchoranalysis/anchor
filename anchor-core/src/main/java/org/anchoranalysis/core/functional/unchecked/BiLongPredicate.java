package org.anchoranalysis.core.functional.unchecked;

/**
 * Tests two arguments of type {@code long} as a predicate.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface BiLongPredicate {

    /**
     * Tests the predicate.
     *
     * @param first first argument.
     * @param second second argument.
     * @return result of the predicate.
     */
    boolean test(long first, long second);
}
