/*-
 * #%L
 * anchor-image
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
package org.anchoranalysis.image.core.object.properties;

import java.util.function.Predicate;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectCollectionWithPropertiesFactory {

    /**
     * Creates a new collection by filtering an iterable and then mapping it to {@link
     * ObjectWithProperties}
     *
     * @param <T> type that will be mapped to {@link ObjectWithProperties}
     * @param <E> exception-type that may be thrown during mapping
     * @param iterable incoming collection to be mapped
     * @param mapFunc function for mapping
     * @return a newly created {@link ObjectCollectionWithProperties}
     * @throws E if thrown by <code>mapFunc</code>
     */
    public static <T, E extends Exception> ObjectCollectionWithProperties filterAndMapFrom(
            Iterable<T> iterable,
            Predicate<T> predicate,
            CheckedFunction<T, ObjectWithProperties, E> mapFunc)
            throws E {
        ObjectCollectionWithProperties out = new ObjectCollectionWithProperties();
        for (T item : iterable) {

            if (!predicate.test(item)) {
                continue;
            }

            out.add(mapFunc.apply(item));
        }
        return out;
    }
}
