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
import org.anchoranalysis.core.functional.function.CheckedBiConsumer;
import org.anchoranalysis.core.functional.function.CheckedBooleanSupplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilities for repeating operations a certain number of times.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalIterate {

    /**
     * Repeats an operation a number of times.
     * 
     * @param numberTimes how many times to repeat the operation
     * @param operation the operation
     */
    public static void repeat(int numberTimes, Runnable operation) {
        for( int i=0; i<numberTimes; i++) {
            operation.run();
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
    public static <E extends Exception> boolean repeatUntil(int maxNumberTimes, CheckedBooleanSupplier<E> supplier) throws E {
        for( int i=0; i<maxNumberTimes; i++) {
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
    public static <K, V, E extends Exception> void iterateMap( Map<K,V> map, CheckedBiConsumer<K, V, E> consumer ) throws E {
        for( Entry<K,V> entry : map.entrySet()) {
            consumer.accept( entry.getKey(), entry.getValue() );
        }
    }
}
