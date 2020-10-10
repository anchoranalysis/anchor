/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.sequencetype;

import java.io.Serializable;
import org.anchoranalysis.core.index.container.OrderProvider;

/**
 * A sequence of elements (of type {@code T}), which are mapped to a range of integer indices.
 * 
 * @author Owen Feehan
 * @param <T> index-type
 */
public abstract class SequenceType<T> implements Serializable {

    /** */
    private static final long serialVersionUID = -4972832404633715034L;

    public abstract String getName();

    public abstract void update(T element) throws SequenceTypeException;

    public abstract int getNumberElements();

    public abstract OrderProvider createOrderProvider();
    
    public abstract IncompleteElementRange elementRange();
    
    /**
     * If the sequence-type supports a different maximum-index to that derived from the elements {@link #update}, this assigns it.
     * 
     * <p>Many types will ignore this operation, relying on calls to {@link #update} for the maximum index.
     * 
     * @param index the maximum-index
     */
    public abstract void assignMaximumIndex(int index);
}
