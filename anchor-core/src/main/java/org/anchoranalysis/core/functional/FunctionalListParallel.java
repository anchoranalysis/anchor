package org.anchoranalysis.core.functional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedFunction;

/**
 * Like {@link FunctionalList} but executes any operations in parallel where possible.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalListParallel {

    /**
     * Maps a collection to a list with each element in the original collection maybe producing an
     * element in the output.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> an exception that may be thrown by an {@code mapFunction}
     * @param collection the collection to be mapped.
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @param mapFunction function to do the mapping to an Optional (the item is included in the
     *     output if the optional is defined).
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     * @throws E if it is thrown by any call to {@code mapFunction}
     */
    public static <S, T, E extends Exception> List<T> mapToListOptional(
            Collection<S> collection,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, Optional<T>, E> mapFunction)
            throws E {
        Stream<S> stream = collection.stream().parallel();
        return FunctionalList.mapToListOptional(stream, throwableClass, mapFunction);
    }
}
