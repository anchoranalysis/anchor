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

import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An R-Tree that contains items, each with an associated one-dimensional interval.
 *
 * <p>This is similar to {@link BoundingBoxRTree} but uses a single dimension rather than
 * three-dimensions to index the data.
 *
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @param <T> object-type stored in structure (the payload).
 * @author Owen Feehan
 */
public class IntervalRTree<T> extends RTree<T> {

    /**
     * The number of spatial dimensions that the bounding boxes are expected to support.
     *
     * <p>The underlying library does not support a single-dimension, so we wrap our single
     * dimension query in 2 dimensions as a workaround.
     */
    private static final int NUMBER_DIMENSIONS = 2;

    /** Creates an empty R-Tree. */
    public IntervalRTree() {
        super(NUMBER_DIMENSIONS);
    }

    /**
     * Creates an empty R-Tree with a specified number of children.
     *
     * @param maxNumberEntries maximum number of entries in the r-tree
     */
    public IntervalRTree(int maxNumberEntries) {
        super(NUMBER_DIMENSIONS, maxNumberEntries);
    }

    /**
     * Adds a bounding-box with a corresponding index.
     *
     * <p>Note that the payload must not be unique, and multiple identical elements can exist with
     * the same bounding-box and payload.
     *
     * @param min the minimum of the interval (inclusive).
     * @param max the maximum of the interval (inclusive).
     * @param payload the payload associated with the interval.
     */
    public void add(double min, double max, T payload) {
        super.add(asRectangle(min, max), payload);
    }

    /**
     * Which objects contain a particular point?
     *
     * @param point the point
     * @return payloads for all objects that contain {@code point}.
     */
    public Set<T> contains(double point) {
        return containsStream(point).collect(Collectors.toSet());
    }

    /**
     * Like {@link #contains} but returns a {@link Stream} instead of a {@link Set}.
     *
     * @param point the point
     * @return payloads for all objects that contain {@code point}.
     */
    public Stream<T> containsStream(double point) {
        Point pointToSearch = Point.create(point, point);
        return super.containsStream(pointToSearch);
    }

    /**
     * Which bounding-boxes intersect with another specific bounding box?
     *
     * @param min the minimum of the interval (inclusive).
     * @param max the maximum of the interval (inclusive).
     * @return payloads for all bounding-boxes that intersect with {@code toIntersectWith}.
     */
    public Set<T> intersectsWith(double min, double max) {
        return intersectsWithStream(min, max).collect(Collectors.toSet());
    }

    /**
     * Like {@link #intersectsWith(double, double)} but returns a {@link Stream} instead of a {@link
     * Set}.
     *
     * @param min the minimum of the interval (inclusive).
     * @param max the maximum of the interval (inclusive).
     * @return payloads for all bounding-boxes that intersect with {@code toIntersectWith}.
     */
    public Stream<T> intersectsWithStream(double min, double max) {
        return super.intersectsWithStream(asRectangle(min, max));
    }

    /**
     * Removes a particular item from the r-tree, identified by its interval and payload.
     *
     * <p>If no entry can be found matching exactly the {@code box} and {@code payload}, no change
     * happens to the r-tree. No error is reported.
     *
     * <p>If multiple entries exist that match exactly the {@code box} and {@code payload}, then all
     * entries are removed.
     *
     * <p>If {@code box} exists but with a different {@code payload}, behaviour is undefined. Either
     * {@code box} is removed, or no change occurs.
     *
     * @param min the minimum of the interval (inclusive).
     * @param max the maximum of the interval (inclusive).
     * @param payload the payload
     */
    public void remove(double min, double max, T payload) {
        super.remove(asRectangle(min, max), payload);
    }

    /** Converts am interval to a {@link Rectangle}. */
    private static Rectangle asRectangle(double min, double max) {
        return Rectangle.create(min, min, max, max);
    }
}
