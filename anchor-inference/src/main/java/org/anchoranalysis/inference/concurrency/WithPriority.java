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
package org.anchoranalysis.inference.concurrency;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Wraps an element of type {@code T} to ensure priority is given when the flag {@code gpu==true}.
 *
 * <p>The priority exists via an ordering of elements, where elements with flag {@code gpu==true}
 * always precede those with {@code gpu==false}.
 *
 * @author Owen Feehan
 * @param <T> the element-type
 */
@EqualsAndHashCode
@AllArgsConstructor
public class WithPriority<T> implements Comparable<WithPriority<T>> {

    /** The element to assign priority to. */
    private T element;

    /** Whether the element is associated with a GPU, in which case, it is given priority. */
    private boolean gpu;

    /**
     * Gets the underlying element stored in the structure.
     *
     * @return the underlying element, ignoring any priority.
     */
    public T get() {
        return element;
    }

    /**
     * Is the element returned by {@link #get} associated with a GPU?
     *
     * @return true if it the element is associated with the GPU.
     */
    public boolean isGPU() {
        return gpu;
    }

    /** Orders so that {@code gpu==true} has higher priority in queue to {@code gpu==false}. */
    @Override
    public int compareTo(WithPriority<T> other) {
        return Boolean.compare(other.gpu, gpu);
    }
}
