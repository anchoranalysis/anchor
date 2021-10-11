/*-
 * #%L
 * anchor-spatial
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Bases class for implementations of R-Trees that store objects with associated geometry.
 *
 * @author Owen Feehan
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @param <T> object-type stored in structure (the payload).
 * @author Owen Feehan
 */
public abstract class RTree<T> {

    /** The underlying r-tree data structure. */
    private com.github.davidmoten.rtreemulti.RTree<T, Geometry> tree;

    /**
     * Creates an empty R-Tree.
     *
     * @param numberDimensions the number of spatial dimensions that the bounding boxes are expected
     *     to support.
     */
    protected RTree(int numberDimensions) {
        tree = com.github.davidmoten.rtreemulti.RTree.dimensions(numberDimensions).create();
    }

    /**
     * Creates an empty R-Tree with a specified number of children.
     *
     * @param numberDimensions the number of spatial dimensions that the bounding boxes are expected
     *     to support.
     * @param maxNumberEntries maximum number of entries in the r-tree.
     */
    protected RTree(int numberDimensions, int maxNumberEntries) {
        // Three is minimum number for this parameter
        int maxChildren = Math.max(maxNumberEntries, 3);
        tree =
                com.github.davidmoten.rtreemulti.RTree.maxChildren(maxChildren)
                        .dimensions(numberDimensions)
                        .create();
    }

    /**
     * The total number of items stored in the tree.
     *
     * @return the total number of items.
     */
    public int size() {
        return tree.size();
    }

    /**
     * <i>All</i> elements contained within the tree, as a {@link Set}.
     *
     * @return a newly created {@link Set} of all the elements, reusing the existing element
     *     objects.
     */
    public Set<T> asSet() {
        return toSet(tree.entries());
    }

    /**
     * Adds a {@link Rectangle} with a corresponding payload.
     *
     * <p>Note that the payload must not be unique, and multiple identical elements can exist with
     * the same bounding-box and payload.
     *
     * @param rectangle the rectangle associated with the payload.
     * @param payload the payload associated with the rectangle.
     */
    protected void add(Rectangle rectangle, T payload) {
        // Adding is an immutable operation, so we need to resassign the member variable
        this.tree = tree.add(payload, rectangle);
    }

    /**
     * Which objects contain a particular point?
     *
     * @param point the point.
     * @return payloads for all objects that contain {@code point}.
     */
    protected Stream<T> containsStream(Point point) {
        return toStream(tree.search(point));
    }

    /**
     * Which elements that a rectangle intersects with as a {@link Stream}.
     *
     * @param rectangle the rectangle associated with the payload.
     */
    protected Stream<T> intersectsWithStream(Rectangle rectangle) {
        return toStream(tree.search(rectangle));
    }

    /**
     * Removes a particular item from the r-tree, identified by its {@link Rectangle} and payload.
     *
     * <p>If no entry can be found matching exactly the {@code rectangle} and {@code entry}, no
     * change happens to the r-tree. No error is reported.
     *
     * <p>If multiple entries exist that match exactly the {@code rectangle} and {@code entry}, then
     * all entries are removed.
     *
     * @param rectangle the rectangle.
     * @param payload the payload.
     */
    protected void remove(Rectangle rectangle, T payload) {
        this.tree = tree.delete(payload, rectangle, true);
    }

    /** Creates a {@link Set} from entries found in the R-Tree. */
    private Set<T> toSet(Iterable<Entry<T, Geometry>> entries) {
        return toStream(entries).collect(Collectors.toSet());
    }

    /** Creates a {@link Stream} from entries found in the R-Tree. */
    private Stream<T> toStream(Iterable<Entry<T, Geometry>> entries) {
        return StreamSupport.stream(entries.spliterator(), false).map(Entry::value);
    }
}
