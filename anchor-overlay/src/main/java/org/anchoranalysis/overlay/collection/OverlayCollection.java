/*-
 * #%L
 * anchor-overlay
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

package org.anchoranalysis.overlay.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.overlay.Overlay;

/**
 * A collection of {@link Overlay} objects.
 *
 * <p>It is built on top of an internal list representation.
 *
 * @author Owen Feehan
 */
public class OverlayCollection implements Iterable<Overlay> {

    private List<Overlay> delegate;

    /** Creates an empty collection. */
    public OverlayCollection() {
        delegate = new ArrayList<>();
    }

    /**
     * Creates the collection from a stream of {@link Overlay}s.
     *
     * <p>Each element is reused internally, without any copying.
     *
     * @param stream the stream.
     */
    public OverlayCollection(Stream<Overlay> stream) {
        delegate = stream.collect(Collectors.toList());
    }

    @Override
    public Iterator<Overlay> iterator() {
        return delegate.iterator();
    }

    /**
     * Access a particular element in the collection by index.
     *
     * @param index the index (starting at 0).
     * @return the respective element at index {@code index}.
     * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index
     *     &gt;= size()</tt>)
     */
    public Overlay get(int index) {
        return delegate.get(index);
    }

    /**
     * The total number of elements in the list.
     *
     * @return the total number of elements.
     */
    public int size() {
        return delegate.size();
    }

    /**
     * Append an overlay to the end of the list.
     *
     * @param overlay the overlay to append.
     */
    public void add(Overlay overlay) {
        delegate.add(overlay);
    }
}
