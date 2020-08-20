package org.anchoranalysis.core.functional;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.CheckedBiFunction;
import org.anchoranalysis.core.functional.function.CheckedConsumer;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.functional.function.CheckedSupplier;

/**
 * Additional utility functions for {@link Optional} and exceptions.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalUtilities {

    /**
     * Like {@link Optional::map} but tolerates an exception in the mapping function, which is
     * immediately thrown.
     *
     * @param <S> incoming optional-type for map
     * @param <T> outgoing optional-type for map
     * @param <E> exception that may be thrown during mapping
     * @param optional incoming optional
     * @param mapFunc the function that does the mapping from incoming to outgoing
     * @return the outgoing "mapped" optional
     * @throws E an exception if the mapping function throws it
     */
    public static <S, E extends Exception> void ifPresent(
            Optional<S> optional, CheckedConsumer<S, E> consumerFunction) throws E {
        if (optional.isPresent()) {
            consumerFunction.accept(optional.get());
        }
    }

    /**
     * Like {@link Optional::map} but tolerates an exception in the mapping function, which is
     * immediately thrown.
     *
     * @param <S> incoming optional-type for map
     * @param <T> outgoing optional-type for map
     * @param <E> exception that may be thrown during mapping
     * @param optional incoming optional
     * @param mapFunction the function that does the mapping from incoming to outgoing
     * @return the outgoing "mapped" optional
     * @throws E an exception if the mapping function throws it
     */
    public static <S, T, E extends Exception> Optional<T> map(
            Optional<S> optional, CheckedFunction<S, T, E> mapFunction) throws E {
        if (optional.isPresent()) {
            T target = mapFunction.apply(optional.get());
            return Optional.of(target);
        } else {
            return Optional.empty();
        }
    }
    
    
    /**
     * Like {@link Optional::orElseGet} but tolerates an exception in the supplier function, which is
     * immediately thrown.
     *
     * @param <T> optional-type
     * @param <E> exception that may be thrown during mapping
     * @param optional incoming optional
     * @param supplier supplies a value if the optional is empty
     * @return the outgoing optional
     * @throws E an exception if the supplier throws it
     */
    public static <T, E extends Exception> T orElseGet(
            Optional<T> optional, CheckedSupplier<T,E> supplier) throws E {
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return supplier.get();
        }
    }

    /**
     * Like {@link Optional::flatMap} but tolerates an exception in the mapping function, which is
     * immediately thrown.
     *
     * @param <S> incoming optional-type for map
     * @param <T> outgoing optional-type for map
     * @param <E> exception that may be thrown during mapping
     * @param optional incoming optional
     * @param mapFunction the function that does the mapping from incoming to outgoing
     * @return the outgoing "mapped" optional
     * @throws E an exception if the mapping function throws it
     */
    public static <S, T, E extends Exception> Optional<T> flatMap(
            Optional<S> optional, CheckedFunction<S, Optional<T>, E> mapFunction) throws E {
        if (optional.isPresent()) {
            return mapFunction.apply(optional.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Mapping only occurs if both Optionals are non-empty (equivalent to a logical AND on the
     * optionals)
     *
     * @param <T> outgoing optional-type for map
     * @param <U> first incoming optional-type for map
     * @param <V> second incoming optional-type for map
     * @param optional1 first incoming optional
     * @param optional2 second incoming optional
     * @param mapFunction the function that does the mapping from both incoming objects to outgoing
     * @return the outgoing "mapped" optional (empty() if either incoming optional is empty)
     */
    public static <T, U, V, E extends Exception> Optional<T> mapBoth(
            Optional<U> optional1, Optional<V> optional2, CheckedBiFunction<U, V, T, E> mapFunction)
            throws E {
        if (optional1.isPresent() && optional2.isPresent()) {
            return Optional.of(mapFunction.apply(optional1.get(), optional2.get()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * The first optional if it's present, or the second, or the third etc. using an array
     *
     * @param <T> type of optionals
     * @return a new optional that is optionals[0] OR optionals[1] OR optionals[2] etc.
     */
    @SafeVarargs
    public static <T> Optional<T> orFlat(Optional<T>... optionals) {
        for (Optional<T> item : optionals) {
            if (item.isPresent()) {
                return item;
            }
        }
        return Optional.empty();
    }

    /**
     * Creates an Optional from a string that might be empty or null
     *
     * @param possiblyEmptyString a string that might be empty or null
     * @return the string, or empty() if the string is empty or null
     */
    public static Optional<String> create(String possiblyEmptyString) {
        if (possiblyEmptyString == null || possiblyEmptyString.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(possiblyEmptyString);
        }
    }

    /**
     * Creates an {@link Optional} from a boolean flag
     *
     * @param <T> type in Optional
     * @param flag iff TRUE an populated optional is returned, otherwise empty().
     * @param valueIfFlagTrue a positive value to use if fla gis true
     * @return a filled or empty optional depending on flag
     */
    public static <T> Optional<T> createFromFlag(boolean flag, T valueIfFlagTrue) {
        if (flag) {
            return Optional.of(valueIfFlagTrue);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates an {@link Optional} from a boolean flag
     *
     * @param <T> type in Optional
     * @param flag iff TRUE an populated optional is returned, otherwise empty().
     * @param valueIfFlagTrue used to generate a positive value
     * @return a filled or empty optional depending on flag
     */
    public static <T> Optional<T> createFromFlag(boolean flag, Supplier<T> valueIfFlagTrue) {
        if (flag) {
            return Optional.of(valueIfFlagTrue.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates an {@link Optional} from a boolean flag - where the supplier can thrown an exception
     *
     * @param <T> type in Optional
     * @param flag iff TRUE an populated optional is returned, otherwise empty().
     * @param valueIfFlagTrue used to generate a positive value
     * @return a filled or empty optional depending on flag
     * @throws E if the supplioer throws an exception
     */
    public static <T, E extends Exception> Optional<T> createFromFlagChecked(
            boolean flag, CheckedSupplier<T, E> valueIfFlagTrue) throws E {
        if (flag) {
            return Optional.of(valueIfFlagTrue.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates an {@link Optional} from a boolean flag and a supplier that returns an {@link
     * Optional}
     *
     * @param <T> type in Optional
     * @param flag iff TRUE an populated optional is returned, otherwise empty().
     * @param valueIfFlagTrue used to generate a positive value
     * @return a filled or empty optional depending on flag
     */
    public static <T> Optional<T> createFromFlagOptional(
            boolean flag, Supplier<Optional<T>> valueIfFlagTrue) {
        if (flag) {
            return valueIfFlagTrue.get();
        } else {
            return Optional.empty();
        }
    }
}
