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

import java.util.Comparator;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates a {@link Comparator} that can work with {@link Optional}s.
 *
 * <p>An {@link Optional#empty} value can be eithered first or last.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalComparator {

    /**
     * Creates a {@link Comparator} for elements of type {@code T} where empty-values are
     * <b>first</b>.
     *
     * <p>Inspired by this <a href="https://stackoverflow.com/a/29570443/14572996">Stack Overflow
     * post</a> by <i>Anderson Vieira</i>.
     *
     * @param <T> element-type (without {@link Optional}.
     * @return the comparator.
     */
    public static <T extends Comparable<T>> Comparator<Optional<T>> emptyFirstComparator() {
        return new EmptyFirst<>();
    }

    /**
     * Creates a {@link Comparator} for elements of type {@code T} where empty-values are
     * <b>last</b>.
     *
     * @param <T> element-type (without {@link Optional}.
     * @return the comparator.
     */
    public static <T extends Comparable<T>> Comparator<Optional<T>> emptyLastComparator() {
        return new EmptyLast<>();
    }

    /** The implementation when empty values are <b>first</b>. */
    private static class EmptyFirst<T extends Comparable<T>> implements Comparator<Optional<T>> {
        @Override
        public int compare(Optional<T> obj1, Optional<T> obj2) {
            if (obj1.isPresent() && obj2.isPresent()) {
                return obj1.get().compareTo(obj2.get());
            } else if (obj1.isPresent()) {
                return -1;
            } else if (obj2.isPresent()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /** The implementation when empty values are <b>last</b>. */
    private static class EmptyLast<T extends Comparable<T>> implements Comparator<Optional<T>> {
        @Override
        public int compare(Optional<T> obj1, Optional<T> obj2) {
            if (obj1.isPresent() && obj2.isPresent()) {
                return obj1.get().compareTo(obj2.get());
            } else if (obj1.isPresent()) {
                return 1;
            } else if (obj2.isPresent()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
