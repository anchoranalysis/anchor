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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Splits a collection of elements into spatially separate <i>clusters</i>.
 *
 * <p>Each element must provide a corresponding bounding-box.
 *
 * <p>Any objects whose bounding-boxes intersect belong to the same cluster, but otherwise not.
 *
 * <p>This is similar to a simplified <a href="https://en.wikipedia.org/wiki/DBSCAN">DBSCAN
 * algorithm</a>.
 *
 * @author Owen Feehan
 * @param <T> the element-type in the collection.
 */
@AllArgsConstructor
public class SpatiallySeparate<T> {

    /**
     * Extracts a bounding-box from a particular element.
     *
     * <p>This operation is assumed to have low computational cost.
     */
    private Function<T, BoundingBox> extractBoundingBox;

    /**
     * Splits a collection of elements into spatially separate <i>clusters</i>, <b>without consuming
     * all elements</b> in {@code elements}.
     *
     * @param elements the collection of elements to separate.
     * @return a list of object-collections, each object-collection is guaranteed to be spatially
     *     separate from the others.
     */
    public List<Set<T>> separate(Collection<T> elements) {
        List<Set<T>> out = new ArrayList<>();

        BoundingBoxRTree<T> tree = createTree(elements);

        while (!tree.isEmpty()) {
            Set<T> cluster = new HashSet<>();
            findArbitrarySpatiallyConnected(tree, cluster);
            out.add(cluster);
        }

        return out;
    }

    /** Creates a {@link BoundingBoxRTree} from all elements. */
    private BoundingBoxRTree<T> createTree(Collection<T> elements) {
        BoundingBoxRTree<T> tree = new BoundingBoxRTree<>(elements.size());
        for (T element : elements) {
            tree.add(extractBoundingBox.apply(element), element);
        }
        return tree;
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
    private void findArbitrarySpatiallyConnected(BoundingBoxRTree<T> tree, Set<T> out) {

        Deque<T> intersecting = new LinkedList<>();
        intersecting.add(tree.arbitraryElement());

        while (!intersecting.isEmpty()) {

            T current = intersecting.pop();

            out.add(current);

            BoundingBox boxCurrent = extractBoundingBox.apply(current);

            for (T neighbor : tree.intersectsWith(boxCurrent)) {
                if (!out.contains(neighbor)) {
                    intersecting.add(neighbor);
                }
            }

            tree.remove(boxCurrent, current);
        }
    }
}
