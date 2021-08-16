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

package org.anchoranalysis.image.voxel.object;

import com.google.common.base.Functions;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.graph.GraphWithoutPayload;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.rtree.RTree;

/**
 * A data-structure to efficiently determine which object-masks intersect in a collection.
 *
 * <p>It can store elements of any type, so long each element maps deterministically to an {@link
 * ObjectMask}.
 *
 * <p>Internally, a r-tree data structure is used of object-masks (indexed via a derived
 * bounding-box) for efficient queries. However, search methods check not only bounding-box overlap,
 * but also that objects have at least one overlapping voxel.
 *
 * <p>All objects that are passed to the constructor are initially included. An existing object may
 * be removed, but no additional object may be added.
 *
 * <p>Note that when an object is removed, it remains in the {@code objects} associated with the
 * r-tree, but is removed from the index.
 *
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @see RTree for a related structure operating only on bounding-boxes
 * @param <T> the type of elements stored in the structure, each of which must map to a {@link
 *     ObjectMask}.
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class IntersectingObjects<T> {

    /** An r-tree that stores indices of the objects for each bounding-box */
    private RTree<T> tree;

    /** Extracts an {@link ObjectMask} from an element. */
    private final Function<T, ObjectMask> extractObject;

    /**
     * Creates from an {@link ObjectCollection}.
     *
     * @param objects the objects whose intersection will be checked
     * @return a newly created {@link IntersectingObjects} for {@code objects}.
     */
    public static IntersectingObjects<ObjectMask> create(ObjectCollection objects) {
        return new IntersectingObjects<>(objects.asList(), Functions.identity());
    }

    /**
     * Creates an r-tree for particular objects.
     *
     * @param objects the objects
     */
    public IntersectingObjects(Collection<T> objects, Function<T, ObjectMask> extractObject) {
        this.extractObject = extractObject;
        this.tree = new RTree<>(objects.size());
        objects.stream().forEach(element -> tree.add(boxFor(element), element));
    }

    /**
     * All elements that contain a particular point.
     *
     * <p>Note that the point must exist as an <i>on</i> pixel on the actual {@link ObjectMask}, not
     * just within the bounding box of the object.
     *
     * @param point the particular point that must exist in all objects that are searched for.
     * @return a newly created set of all elements that contain {@code point}, being empty if no
     *     objects do.
     */
    public Set<T> contains(Point3i point) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return tree.containsStream(point)
                .filter(object -> extractObject.apply(object).contains(point))
                .collect(Collectors.toSet());
    }

    /**
     * All elements that intersect with a particular object.
     *
     * @param object the object with which objects should intersect
     * @return a newly created set of all elements that intersect with {@code
     *     objectToIntersectWith}, being empty if no objects intersect.
     */
    public Set<T> intersectsWith(ObjectMask object) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return intersectsWithStream(object).collect(Collectors.toSet());
    }

    /**
     * Like {@link #intersectsWith(ObjectMask)} but returns the objects as a {@link Stream} rather
     * than a {@link Set}.
     *
     * @param object the object with which objects should intersect
     * @return a stream of all objects that intersect with {@code objectToIntersectWith}, being
     *     empty if no objects intersect.
     */
    public Stream<T> intersectsWithStream(ObjectMask object) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return tree.intersectsWithStream(object.boundingBox())
                .filter(element -> extractObject.apply(element).hasIntersectingVoxels(object));
    }

    /**
     * All elements that intersect with a particular bounding box.
     *
     * @param box the bounding-box with which objects should intersect.
     * @return a newly created set of all objects that intersect with {@code box}, being empty if no
     *     objects intersect.
     */
    public Set<T> intersectsWith(BoundingBox box) {
        return tree.intersectsWith(box);
    }

    /**
     * Removes an object-mask, so that it is no longer considered in queries.
     *
     * <p>Note the associated {@link ObjectCollection} remains unchanged.
     *
     * <p>If no entry can be found matching exactly the object, no change happens. No error is
     * reported.
     *
     * <p>If multiple entries exist that match exactly the object,then all entries are removed.
     *
     * @param element the element to remove
     */
    public void remove(T element) {
        tree.remove(boxFor(element), element);
    }

    /**
     * Splits the collection of objects into spatially separate <i>clusters</i>.
     *
     * <p>Any objects whose bounding-boxes intersect belong to the same cluster, but otherwise not.
     *
     * <p>This is similar to a simplified <a href="https://en.wikipedia.org/wiki/DBSCAN">DB Scan
     * algorithm</a>.
     *
     * @return a list of object-collections, each object-collection is guaranteed to be spatially
     *     separate from the others.
     */
    public Set<Set<T>> spatiallySeparate() {
        Set<T> unprocessed = tree.asSet();
        Set<Set<T>> out = new HashSet<>();

        while (!unprocessed.isEmpty()) {

            T current = unprocessed.iterator().next();

            Set<T> spatiallyConnected = new HashSet<>();
            addSpatiallyConnected(spatiallyConnected, current, unprocessed);
            out.add(spatiallyConnected);
        }
        return out;
    }

    /**
     * Constructs a graph where each vertex is an element and an edge exists between any elements
     * that intersect.
     *
     * @return a newly created graph, reusing the existing elements as vertices.
     */
    public GraphWithoutPayload<T> asGraph() {
        GraphWithoutPayload<T> graph = new GraphWithoutPayload<>(true);

        for (T element : tree.asSet()) {
            graph.addVertex(element);

            Iterator<T> intersecting =
                    intersectsWithStream(extractObject.apply(element)).iterator();
            while (intersecting.hasNext()) {
                // We avoid creating an edge if it already exists, or between an element and itself.
                T other = intersecting.next();
                if (!element.equals(other) && !graph.containsEdge(other, element)) {
                    graph.addEdge(element, other);
                }
            }
        }

        return graph;
    }

    /**
     * Number of items in the r-tree.
     *
     * @return the number of items
     */
    public int size() {
        return tree.size();
    }

    /**
     * Moves unprocessed elements that are spatially-connected (bounding-boxes intersect) into a
     * set.
     *
     * @param addTo the set to add the spatially-connected objects to.
     * @param source the <i>source</i> element for which we seek spatially-connected elements.
     * @param unprocessed all elements that have not yet been processed (i.e. added to a list).
     */
    private void addSpatiallyConnected(Set<T> addTo, T source, Set<T> unprocessed) {

        unprocessed.remove(source);

        addTo.add(source);
        List<T> queue = tree.intersectsWith(boxFor(source)).stream().collect(Collectors.toList());

        while (!queue.isEmpty()) {
            T current = queue.remove(0);

            if (unprocessed.contains(current)) {
                unprocessed.remove(current);

                addTo.add(current);
                queue.addAll(tree.intersectsWith(boxFor(current)));
            }
        }
    }

    /** Extracts a {@link BoundingBox} for an element. */
    private BoundingBox boxFor(T element) {
        return extractObject.apply(element).boundingBox();
    }
}
