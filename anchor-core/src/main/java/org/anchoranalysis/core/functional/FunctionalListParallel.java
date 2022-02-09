/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
