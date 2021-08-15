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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.experimental.Accessors;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.rtree.RTree;

/**
 * An R-Tree of object-masks (indexed via a derived bounding-box).
 *
 * <p>Note that when an object is removed, it remains in the {@code objects} associated with the
 * r-tree, but is removed from the index.
 *
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @see RTree for a related structure operating only on bounding-boxes
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class ObjectCollectionRTree {

    /** An r-tree that stores indices of the objects for each bounding-box */
    private RTree<Integer> tree;

    /** All objects stored in the r-tree (whose order corresponds to indices in {@code delegate} */
    private ObjectCollection objects;

    /**
     * Creates an r-tree for particular objects.
     *
     * @param objects the objects
     */
    public ObjectCollectionRTree(ObjectCollection objects) {
        this.objects = objects;
        tree = new RTree<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            tree.add(objects.get(i).boundingBox(), i);
        }
    }

    /**
     * All objects in the collection that contain a particular point.
     *
     * <p>Note that the point must exist as an <i>on</i> pixel on the actual {@link ObjectMask}, not
     * just within the bounding box of the object.
     *
     * @param point the particular point that must exist in all objects that are searched for.
     * @return a newly created collection of all objects that contain {@code point}, being empty if
     *     no objects do.
     */
    public ObjectCollection contains(Point3i point) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(object -> object.contains(point), tree.contains(point));
    }

    /**
     * All objects that intersect with another particular object.
     *
     * @param object the object with which objects should intersect
     * @return a newly created collection of all objects that intersect with {@code
     *     objectToIntersectWith}, being empty if no objects intersect.
     */
    public ObjectCollection intersectsWith(ObjectMask object) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(
                        objectToIterate -> objectToIterate.hasIntersectingVoxels(object),
                        tree.intersectsWith(object.boundingBox()));
    }

    /**
     * All objects that intersect with a particular bounding box.
     *
     * @param box the bounding-box with which objects should intersect.
     * @return a newly created collection of all objects that intersect with {@code box}, being
     *     empty if no objects intersect.
     */
    public ObjectCollection intersectsWith(BoundingBox box) {
        return objects.createSubset(tree.intersectsWith(box));
    }

    /**
     * The indices of all objects that intersect with a particular bounding box.
     *
     * <p>The indices are unique identifiers corresponding to the position of the object in the
     * {@link ObjectCollection} as passed to the constructor.
     *
     * @param box the bounding-box with which objects should intersect.
     * @return a newly created list of all indices that intersect with {@code box}, being empty if
     *     no objects intersect.
     */
    public Set<Integer> intersectsWithAsIndices(BoundingBox box) {
        return tree.intersectsWith(box);
    }

    /**
     * Removes an object-mask from the r-tree index.
     *
     * <p>Note the associated {@link ObjectCollection} remains unchanged, and the indices of the
     * other elements remain unchanged.
     *
     * <p>If no entry can be found matching exactly the object's bounding-box and {@code index}, no
     * change happens to the r-tree. No error is reported.
     *
     * <p>If multiple entries exist that match exactly the object's bounding-box and {@code index},
     * then all entries are removed.
     *
     * @param object the object to remove
     * @param index the associated index of the object
     */
    public void remove(ObjectMask object, int index) {
        tree.remove(object.boundingBox(), index);
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
    public List<ObjectCollection> spatiallySeparate() {
        Set<Integer> unprocessed = createIntegerSequence(objects.size());
        List<ObjectCollection> out = new ArrayList<>();

        while (!unprocessed.isEmpty()) {

            Integer identifier = unprocessed.iterator().next();

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
     * @param identifier the identifier of the <i>source</i> object for which we seek spatially
     *     connected objects
     * @param unprocessed the indices of all objects that have not yet been processed (i.e. added to
     *     a list).
     */
    private void addSpatiallyConnected(
            List<ObjectMask> addTo, Integer identifier, Set<Integer> unprocessed) {

        unprocessed.remove(identifier);

        ObjectMask source = objects.get(identifier);
        addTo.add(source);
        List<Integer> queue =
                tree.intersectsWith(source.boundingBox()).stream().collect(Collectors.toList());

        while (!queue.isEmpty()) {
            Integer current = queue.remove(0);

            if (unprocessed.contains(current)) {
                unprocessed.remove(current);

                ObjectMask currentObject = objects.get(current);
                addTo.add(currentObject);
                queue.addAll(tree.intersectsWith(currentObject.boundingBox()));
            }
        }
    }

    /** Create a sequence of integers beginning at 0 and with {@code numberElements}. */
    private static Set<Integer> createIntegerSequence(int numberElements) {
        return IntStream.range(0, numberElements).boxed().collect(Collectors.toSet());
    }
}
