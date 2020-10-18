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
package org.anchoranalysis.spatial.extent.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.anchoranalysis.spatial.extent.box.BoundingBox;

@AllArgsConstructor
public class SpatiallySeparate<T> {

    /**
     * Extracts a bounding-box from a particular element.
     *
     * <p>This operation is assumed to have low computational cost.
     */
    private Function<T, BoundingBox> extractBoundingBox;

    /**
     * Splits the collection of objects into spatially separate <i>clusters</i>, <b>without
     * consuming all elements</b> in {@code elements}.
     *
     * <p>Upon completion of the algorithm, {@code elements} will be empty.
     *
     * <p>Any objects whose bounding-boxes intersect belong to the same cluster, but otherwise not.
     *
     * <p>This is similar to a simplified <a href="https://en.wikipedia.org/wiki/DBSCAN">DB Scan
     * algorithm</a>.
     *
     * @return a list of object-collections, each object-collection is guaranteed to be spatially
     *     separate from the others.
     */
    public Set<Set<T>> separate(Collection<T> elements) {
        return separateConsume(new HashSet<>(elements));
    }

    /**
     * Splits the collection of objects into spatially separate <i>clusters</i>, <b>consuming all
     * elements</b> in {@code elements}.
     *
     * <p>Upon completion of the algorithm, {@code elements} will be empty.
     *
     * <p>Any objects whose bounding-boxes intersect belong to the same cluster, but otherwise not.
     *
     * <p>This is similar to a simplified <a href="https://en.wikipedia.org/wiki/DBSCAN">DB Scan
     * algorithm</a>.
     *
     * @return a list of object-collections, each object-collection is guaranteed to be spatially
     *     separate from the others.
     */
    public Set<Set<T>> separateConsume(Set<T> elements) {

        Set<Set<T>> out = new HashSet<>();

        RTreeWithSet<T> tree = createTree(elements);

        while (!tree.isEmpty()) {
            out.add(findArbitrarySpatiallyConnected(tree));
        }

        return out;
    }

    private RTreeWithSet<T> createTree(Set<T> elements) {
        RTree<T> tree = new RTree<>(elements.size());
        for (T element : elements) {
            tree.add(extractBoundingBox.apply(element), element);
        }
        return new RTreeWithSet<>(tree, elements);
    }

    /**
     * Takes an arbitrary element from an r-tree and finds all spatially-connected objects, removing
     * them from the r-tree.
     *
     * <p>Two objects are deemed spatially-connected if their bounding-boxes intersect, and the set
     * of all connected-objects is a recursive relation over all such connected objects.
     *
     * @param tree the tree containing elements (which are removed if added to the outputted set)
     *     whose bounding-box intersects with an existing element) are recursively added.
     */
    private Set<T> findArbitrarySpatiallyConnected(RTreeWithSet<T> tree) {
        Set<T> out = new HashSet<>();

        List<T> intersecting = new ArrayList<>();
        intersecting.add(tree.arbitraryElement());

        while (!intersecting.isEmpty()) {
            T current = intersecting.remove(0);

            if (tree.contains(current)) {

                BoundingBox boxCurrent = extractBoundingBox.apply(current);

                tree.remove(current, boxCurrent);
                out.add(current);

                intersecting.addAll(tree.intersectsWith(boxCurrent));
            }
        }
        return out;
    }
}
