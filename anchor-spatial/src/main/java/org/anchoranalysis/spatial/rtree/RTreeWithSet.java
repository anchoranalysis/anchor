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
package org.anchoranalysis.spatial.rtree;

import java.util.Set;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;

@AllArgsConstructor
/**
 * An R-Tree coupled with a set, both of whose elements are kept synchronized across add/remove
 * operations.
 */
class RTreeWithSet<T> {

    /** The r-tree for elements */
    private final RTree<T> tree;

    /** A set containing identical elements to the r-tree. */
    private final Set<T> set;

    public boolean contains(T element) {
        return set.contains(element);
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public T arbitraryElement() {
        return set.iterator().next();
    }

    /**
     * Removes a particular item from the r-tree, identified by its bounding-box and payload.
     *
     * @param element the element to remove
     * @param box the bounding-box
     * @throws OperationFailedException if no item matching {@code box} and {@code payload} cannot
     *     be found to be removed.
     */
    public void remove(T element, BoundingBox box) throws OperationFailedException {
        set.remove(element);
        tree.remove(box, element);
    }

    public Set<T> intersectsWith(BoundingBox box) {
        return tree.intersectsWith(box);
    }
}
