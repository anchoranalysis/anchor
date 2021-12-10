package org.anchoranalysis.core.functional;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;

/**
 * Utility functions to create {@link Optional} from flags.
 *
 * <p>See {@link OptionalUtilities} for utility functions for already-instantiated {@link
 * Optional}s.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalFactory {

    /**
     * Creates only if a boolean flag is true, otherwise returns {@code Optional.empty()}.
     *
     * @param <T> type of optional
     * @param flag boolean flag.
     * @param value a value used only if {@code flag} is true.
     * @return an optional that is defined or empty depending on the flag.
     */
    public static <T> Optional<T> create(boolean flag, T value) {
        if (flag) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates only if a boolean flag is true, otherwise returns {@code Optional.empty()}.
     *
     * @param <T> type of optional
     * @param flag boolean flag.
     * @param supplier a function to create a value T only called if {@code flag} is true.
     * @return an optional that is defined or empty depending on the flag.
     */
    public static <T> Optional<T> create(boolean flag, Supplier<T> supplier) {
        if (flag) {
            return Optional.of(supplier.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Like {@link #create} but accepts a supplier that throws a checked/exception.
     *
     * @param <T> type of optional
     * @param <E> the checked exception
     * @param flag boolean flag
     * @param supplier a function to create a value T only called if {@code flag} is true
     * @return an optional that is defined or empty depending on the flag
     * @throws E if <code>supplier</code> throws it
     */
    public static <T, E extends Exception> Optional<T> createChecked(
            boolean flag, CheckedSupplier<T, E> supplier) throws E {
        if (flag) {
            return Optional.of(supplier.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates {@code Optional.empty()} for an empty string, or otherwise {@code Optional.of()}.
     *
     * @param string the string (possibly empty or null).
     * @return the optional.
     */
    public static Optional<String> create(String string) {
        if (string != null && !string.isEmpty()) {
            return Optional.of(string);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates an {@link Optional} from a boolean flag and a supplier that returns an {@link
     * Optional}.
     *
     * @param <T> type in optional.
     * @param flag iff true an populated optional is returned, otherwise {@link Optional#empty()}.
     * @param valueIfFlagTrue used to generate a positive value.
     * @return a filled or empty optional depending on flag.
     */
    public static <T> Optional<T> createFlat(boolean flag, Supplier<Optional<T>> valueIfFlagTrue) {
        if (flag) {
            return valueIfFlagTrue.get();
        } else {
            return Optional.empty();
        }
    }
}
