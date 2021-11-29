/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;
import org.anchoranalysis.core.functional.checked.CheckedBooleanSupplier;
import org.anchoranalysis.core.functional.checked.CheckedConsumer;
import org.anchoranalysis.core.functional.checked.CheckedIntConsumer;
import org.anchoranalysis.core.functional.checked.CheckedRunnable;

/**
 * Utilities for repeating operations a certain number of times.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalIterate {

    /**
     * Repeats an operation a number of times.
     *
     * @param <E> an exception that may be thrown by {@code operation}.
     * @param numberTimes how many times to repeat the operation
     * @param operation the operation
     * @throws E if {@code operation} throws it.
     */
    public static <E extends Exception> void repeat(int numberTimes, CheckedRunnable<E> operation)
            throws E {
        for (int i = 0; i < numberTimes; i++) {
            operation.run();
        }
    }

    /**
     * Repeats an operation a number of times, counting the number of times the operation returns
     * true.
     *
     * @param <E> an exception that may be thrown by {@code operation}.
     * @param numberTimes how many times to repeat the operation
     * @param operation the operation, which returns true for success, or false for failure.
     * @return the number of successful operations (maximally {@code numberTimes}).
     * @throws E if {@code operation} throws it.
     */
    public static <E extends Exception> int repeatCountSuccessful(
            int numberTimes, CheckedBooleanSupplier<E> operation) throws E {
        int countSuccessful = 0;
        for (int i = 0; i < numberTimes; i++) {
            if (operation.getAsBoolean()) {
                countSuccessful++;
            }
        }
        return countSuccessful;
    }

    /**
     * Repeats an operation a number of times, passing an increment index to {@code operation} each
     * time.
     *
     * <p>The index begins at 0 and will increment to {@code numberTimes -1 } (inclusive).
     *
     * @param <E> an exception that may be thrown by {@code operation}.
     * @param numberTimes how many times to repeat the operation
     * @param operation the operation to execute given an index
     * @throws E if {@code operation} throws it.
     */
    public static <E extends Exception> void repeatWithIndex(
            int numberTimes, CheckedIntConsumer<E> operation) throws E {
        for (int i = 0; i < numberTimes; i++) {
            operation.accept(i);
        }
    }

    /**
     * Repeats an operation until a true value is returned.
     *
     * @param maxNumberTimes the maximum number of times to repeat if no true value is returned.
     * @param supplier the operation to repeat that returns true or false
     * @param <E> an exception that may be thrown by the supplier
     * @return true if at least one operation returned true, or false if no operation did.
     * @throws E if {@code supplier} throws an exception.
     */
    public static <E extends Exception> boolean repeatUntil(
            int maxNumberTimes, CheckedBooleanSupplier<E> supplier) throws E {
        for (int i = 0; i < maxNumberTimes; i++) {
            if (supplier.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterate through all entries in a {@link Map}.
     *
     * @param <K> map key type, see corresponding parameter in @{link Map}.
     * @param <V> map value type, see corresponding parameter in @{link Map}.
     * @param <E> an exception that may be thrown by {@code consumer}.
     * @param map the map whose entries will be iterated.
     * @param consumer called for each entry.
     * @throws E if throw by {@code consumer}.
     */
    public static <K, V, E extends Exception> void iterateMap(
            Map<K, V> map, CheckedBiConsumer<K, V, E> consumer) throws E {
        for (Entry<K, V> entry : map.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Iterates through every element in an array in reverse order
     *
     * @param <T> array element type
     * @param <E> an exception that may be thrown by {@code consumer}.
     * @param array the array
     * @param consumer called for each element in the array.
     * @throws E if throw by {@code consumer}.
     */
    public static <T, E extends Exception> void reverseIterateArray(
            T[] array, CheckedConsumer<T, E> consumer) throws E {
        for (int i = (array.length - 1); i >= 0; i--) {
            consumer.accept(array[i]);
        }
    }
}
