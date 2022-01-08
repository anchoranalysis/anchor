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

import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * An R-Tree that contains items, each with an associated bounding-box in three dimensions.
 *
 * <p>This is similar to {@link IntervalRTree} but uses three-dimensions rather than a single
 * dimension to index the data.
 *
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @param <T> object-type stored in structure (the payload).
 * @author Owen Feehan
 */
public class BoundingBoxRTree<T> extends RTree<T> {

    /** The number of spatial dimensions that the bounding boxes are expected to support. */
    private static final int NUMBER_DIMENSIONS = 3;

    /** Creates an empty R-Tree. */
    public BoundingBoxRTree() {
        super(NUMBER_DIMENSIONS);
    }

    /**
     * Creates an empty R-Tree with a specified number of children.
     *
     * @param maxNumberEntries maximum number of entries in the r-tree
     */
    public BoundingBoxRTree(int maxNumberEntries) {
        super(NUMBER_DIMENSIONS, maxNumberEntries);
    }

    /**
     * Which objects contain a particular point?
     *
     * @param point the point
     * @return payloads for all objects that contain {@code point}.
     */
    public Set<T> contains(ReadableTuple3i point) {
        return containsStream(point).collect(Collectors.toSet());
    }

    /**
     * Like {@link #contains} but returns a {@link Stream} instead of a {@link Set}.
     *
     * @param point the point
     * @return payloads for all objects that contain {@code point}.
     */
    public Stream<T> containsStream(ReadableTuple3i point) {
        Point pointToSearch = Point.create(point.x(), point.y(), point.z());
        return super.containsStream(pointToSearch);
    }

    /**
     * Adds a bounding-box with a corresponding payload.
     *
     * <p>Note that the payload must not be unique, and multiple identical elements can exist with
     * the same bounding-box and payload.
     *
     * @param box the box to add
     * @param payload the payload associated with the bounding-box
     */
    public void add(BoundingBox box, T payload) {
        super.add(asRectangle(box), payload);
    }

    /**
     * Which bounding-boxes intersect with another specific bounding box?
     *
     * @param toIntersectWith the box that must be intersected with
     * @return payloads for all bounding-boxes that intersect with {@code toIntersectWith}.
     */
    public Set<T> intersectsWith(BoundingBox toIntersectWith) {
        return intersectsWithStream(toIntersectWith).collect(Collectors.toSet());
    }

    /**
     * Like {@link #intersectsWith(BoundingBox)} but returns a {@link Stream} instead of a {@link
     * Set}.
     *
     * @param toIntersectWith the box that must be intersected with
     * @return payloads for all bounding-boxes that intersect with {@code toIntersectWith}.
     */
    public Stream<T> intersectsWithStream(BoundingBox toIntersectWith) {
        return super.intersectsWithStream(asRectangle(toIntersectWith));
    }

    /**
     * Removes a particular item from the r-tree, identified by its bounding-box and payload.
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
     * @param box the bounding-box
     * @param payload the payload
     */
    public void remove(BoundingBox box, T payload) {
        super.remove(asRectangle(box), payload);
    }

    /** Converts a {@link BoundingBox} to a {@link Rectangle}. */
    private static Rectangle asRectangle(BoundingBox box) {
        return Rectangle.create(minPoint(box), maxPoint(box));
    }

    /**
     * The corner (minimum) point of a bounding-box in three dimensions, expressed as an array of
     * floats.
     */
    private static double[] minPoint(BoundingBox box) {
        return new double[] {box.cornerMin().x(), box.cornerMin().y(), box.cornerMin().z()};
    }

    /**
     * The corner (maximum) point of a bounding-box in three dimensions, expressed as an array of
     * floats.
     */
    private static double[] maxPoint(BoundingBox box) {
        ReadableTuple3i max = box.calculateCornerMaxInclusive();
        return new double[] {max.x(), max.y(), max.z()};
    }
}
