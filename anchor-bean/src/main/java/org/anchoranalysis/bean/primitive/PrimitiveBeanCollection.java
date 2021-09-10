/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.primitive;

/**
 * A bean that provides a collection of primitive-types or {@link String}s.
 *
 * @param <T> primitive-type stored in the collection, if necessary, in boxed form e.g. Integer,
 *     Double etc.
 * @author Owen Feehan
 */
public interface PrimitiveBeanCollection<T> extends Iterable<T> {

    /**
     * Add an element to the collection.
     *
     * @param element the element to add.
     */
    void add(T element);

    /**
     * Whether the collection contains a particular element?
     *
     * @param element the element to search for
     * @return true iff the collection contains the element.
     */
    boolean contains(T element);

    /**
     * Whether the collection is empty or not.
     *
     * @return true iff the collection contains zero elements.
     */
    boolean isEmpty();
}
