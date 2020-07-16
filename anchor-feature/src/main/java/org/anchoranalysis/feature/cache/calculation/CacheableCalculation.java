/* (C)2020 */
package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Caches the result of a calculation, until reset() is called
 *
 * <p>Implements an equivalence-relation via {@code equals()} that checks if two
 * cacheable-calculations are identical. This allows the user to search through a collection of
 * {@link CacheableCalculation} to find one with identical parameters and re-use it.
 *
 * <p>IMPORTANT NOTE: It is therefore important to make sure every class has a well-defined {@code
 * equals()} and {@code hashCode()}.
 *
 * @author Owen Feehan
 * @param <S> result-type of the calculation
 * @param <T> feature input-type
 * @param <E> exception thrown if something goes wrong in the calculation
 */
public abstract class CacheableCalculation<S, T extends FeatureInput, E extends Exception>
        implements ResettableCalculation {

    private T input;

    // We delegate the actualy execution of the cache
    private CachedOperation<S, E> delegate =
            new CachedOperation<S, E>() {

                @Override
                protected S execute() throws E {
                    return CacheableCalculation.this.execute(input);
                }
            };

    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from previously.
     *
     * @param input If there is no cached-value, and the calculation occurs, this input is used.
     *     Otherwise ignored.
     * @return the result of the calculation
     * @throws ExecuteException if the calculation cannot finish, for whatever reason
     */
    synchronized S getOrCalculate(T input) throws E {

        // Checks we have the same params, if we call the cached calculation a second-time. This
        // maybe catches errors.
        // We only do this when asserts are enabled, as its expensive.
        assert (checkParamsMatchesInput(input));

        initParams(input);
        return delegate.doOperation();
    }

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    public boolean hasCachedCalculation() {
        return delegate.isDone();
    }

    @Override
    public synchronized void invalidate() {
        delegate.reset();
        this.input = null; // Just to be clean, release memory, before the next getOrCalculate
    }

    /**
     * This performs the actual calculation when needed. It should only be called once, until
     * invalidate() is called.
     */
    protected abstract S execute(T input) throws E;

    private synchronized void initParams(T input) {
        this.input = input;
    }

    /** A check that if params are already set, any new inputs must be identical */
    private boolean checkParamsMatchesInput(T input) {
        if (hasCachedCalculation() && input != null && !input.equals(this.input)) {
            throw new AnchorFriendlyRuntimeException(
                    "This feature already has been used, its cache is already set to different params");
        }
        return true;
    }
}
