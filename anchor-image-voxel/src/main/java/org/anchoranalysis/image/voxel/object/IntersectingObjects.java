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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.Accessors;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.rtree.RTree;

/**
 * A data-structure to efficiently determine which object-masks intersect in a collection.
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
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class IntersectingObjects {

    /** An r-tree that stores indices of the objects for each bounding-box */
    private RTree<ObjectMask> tree;

    /**
     * Creates an r-tree for particular objects.
     *
     * @param objects the objects
     */
    public IntersectingObjects(ObjectCollection objects) {
        tree = new RTree<>(objects.size());
        objects.forEach(objectToAdd -> tree.add(objectToAdd.boundingBox(), objectToAdd));
    }

    /**
     * All objects in the collection that contain a particular point.
     *
     * <p>Note that the point must exist as an <i>on</i> pixel on the actual {@link ObjectMask}, not
     * just within the bounding box of the object.
     *
     * @param point the particular point that must exist in all objects that are searched for.
     * @return a newly created set of all objects that contain {@code point}, being empty if no
     *     objects do.
     */
    public Set<ObjectMask> contains(Point3i point) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return tree.containsStream(point)
                .filter(object -> object.contains(point))
                .collect(Collectors.toSet());
    }

    /**
     * All objects that intersect with another particular object.
     *
     * @param object the object with which objects should intersect
     * @return a newly created set of all objects that intersect with {@code objectToIntersectWith},
     *     being empty if no objects intersect.
     */
    public Set<ObjectMask> intersectsWith(ObjectMask object) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return intersectsWithStream(object).collect(Collectors.toSet());
    }

    /**
     * Like {@link #intersectsWith(ObjectMask)} but returns the objects as a stream rather than a
     * {@link ObjectCollection}.
     *
     * @param object the object with which objects should intersect
     * @return a stream of all objects that intersect with {@code objectToIntersectWith}, being
     *     empty if no objects intersect.
     */
    public Stream<ObjectMask> intersectsWithStream(ObjectMask object) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return tree.intersectsWithStream(object.boundingBox())
                .filter(objectToIterate -> objectToIterate.hasIntersectingVoxels(object));
    }

    /**
     * All objects that intersect with a particular bounding box.
     *
     * @param box the bounding-box with which objects should intersect.
     * @return a newly created set of all objects that intersect with {@code box}, being empty if no
     *     objects intersect.
     */
    public Set<ObjectMask> intersectsWith(BoundingBox box) {
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
     * @param object the object to remove
     */
    public void remove(ObjectMask object) {
        tree.remove(object.boundingBox(), object);
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
    public Set<ObjectCollection> spatiallySeparate() {
        Set<ObjectMask> unprocessed = tree.payloads();
        Set<ObjectCollection> out = new HashSet<>();

        while (!unprocessed.isEmpty()) {

            ObjectMask identifier = unprocessed.iterator().next();

            ObjectCollection spatiallyConnected = new ObjectCollection();
            addSpatiallyConnected(spatiallyConnected.asList(), identifier, unprocessed);
            out.add(spatiallyConnected);
        }
        return out;
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
     * Moves objects that are spatially-connected (bounding-boxes intersect) from a set to a list.
     *
     * @param addTo the list to add the spatially-connected objects to
     * @param source the <i>source</i> object for which we seek spatially-connected objects
     * @param unprocessed all objects that have not yet been processed (i.e. added to a list).
     */
    private void addSpatiallyConnected(
            List<ObjectMask> addTo, ObjectMask source, Set<ObjectMask> unprocessed) {

        unprocessed.remove(source);

        addTo.add(source);
        List<ObjectMask> queue =
                tree.intersectsWith(source.boundingBox()).stream().collect(Collectors.toList());

        while (!queue.isEmpty()) {
            ObjectMask current = queue.remove(0);

            if (unprocessed.contains(current)) {
                unprocessed.remove(current);

                addTo.add(current);
                queue.addAll(tree.intersectsWith(current.boundingBox()));
            }
        }
    }
}
