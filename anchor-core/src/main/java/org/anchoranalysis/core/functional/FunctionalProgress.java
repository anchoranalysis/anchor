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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.progress.ProgressReporter;

/** Utilities for updating a {@link ProgressReporter} in a functional way */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalProgress {

    /**
     * Maps a list to new list, updating a progress-reporter for every element
     *
     * @param <S> input-type to map
     * @param <T> output-type of map
     * @param <E> exception that can be thrown by {@link mapFunction}
     * @param list the list to map
     * @param progressReporter the progress-reporter to update
     * @param mapFunction the function to use for mapping
     * @return a newly-created list with the result of each mapped item
     * @throws E if the exception is thrown during mapping
     */
    public static <S, T, E extends Exception> List<T> mapList(
            List<S> list, ProgressReporter progressReporter, CheckedFunction<S, T, E> mapFunction)
            throws E {
        List<T> listOut = new ArrayList<>();

        progressReporter.setMin(0);
        progressReporter.setMax(list.size());
        progressReporter.open();

        try {
            for (int i = 0; i < list.size(); i++) {

                S item = list.get(i);

                listOut.add(mapFunction.apply(item));

                progressReporter.update(i + 1);
            }
            return listOut;

        } finally {
            progressReporter.close();
        }
    }

    /**
     * Maps a list to a new list, including only certain items, updating a progress-reporter for
     * every element
     *
     * <p>Items where the mapping returns {@link Optional.empty()} are not included in the outputted
     * list.
     *
     * @param <S> input-type to map
     * @param <T> output-type of map
     * @param <E> exception that can be thrown by {@link mapFunction}
     * @param list the list to map
     * @param progressReporter the progress-reporter to update
     * @param mapFunction the function to use for mapping
     * @return a newly-created list with the result of each mapped item
     * @throws E if the exception is thrown during mapping
     */
    public static <S, T, E extends Exception> List<T> mapListOptional(
            List<S> list,
            ProgressReporter progressReporter,
            CheckedFunction<S, Optional<T>, E> mapFunction)
            throws E {
        List<T> listOut = new ArrayList<>();

        progressReporter.setMin(0);
        progressReporter.setMax(list.size());
        progressReporter.open();

        try {
            for (int i = 0; i < list.size(); i++) {

                S item = list.get(i);
                mapFunction.apply(item).ifPresent(listOut::add);
                progressReporter.update(i + 1);
            }
            return listOut;

        } finally {
            progressReporter.close();
        }
    }
}
