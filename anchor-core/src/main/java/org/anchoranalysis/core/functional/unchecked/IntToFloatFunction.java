package org.anchoranalysis.core.functional.unchecked;

import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

/**
 * Represents a function that accepts an int-valued argument and produces a float-valued result.
 *
 * <p>This is a {@code int}-to-{@code float} primitive specialization for {@link Function}.
 *
 * <p>It is modelled after {@link IntToDoubleFunction}.
 */
@FunctionalInterface
public interface IntToFloatFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument.
     * @return the function result.
     */
    float applyAsFloat(int value);
}
