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
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.spatial.extent.box.BoundingBox;
import org.anchoranalysis.spatial.extent.rtree.RTree;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * An R-Tree of object-masks (indexed via a derived bounding-box).
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
    @Getter private ObjectCollection objects;

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

    public ObjectCollection contains(Point3i point) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(object -> object.contains(point), tree.contains(point));
    }

    /**
     * All objects that intersect with another object.
     *
     * @param objectToIntersectWith the object with which other object should intersect
     * @return a newly created collection of all objects that intersect with {@code
     *     objectToIntersectWith}.
     */
    public ObjectCollection intersectsWith(ObjectMask objectToIntersectWith) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(
                        object -> object.hasIntersectingVoxels(objectToIntersectWith),
                        tree.intersectsWith(objectToIntersectWith.boundingBox()));
    }

    public ObjectCollection intersectsWith(BoundingBox box) {
        return objects.createSubset(tree.intersectsWith(box));
    }

    public List<Integer> intersectsWithAsIndices(BoundingBox box) {
        return tree.intersectsWith(box);
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
        Set<Integer> unprocessed =
                IntStream.range(0, objects.size()).boxed().collect(Collectors.toSet());
        List<ObjectCollection> out = new ArrayList<>();

        while (!unprocessed.isEmpty()) {

            Integer identifier = unprocessed.iterator().next();

            ObjectCollection spatiallyConnected = new ObjectCollection();
            addSpatiallyConnected(spatiallyConnected.asList(), identifier, unprocessed);
            out.add(spatiallyConnected);
        }
        assert (unprocessed.isEmpty());
        return out;
    }

    private void addSpatiallyConnected(
            List<ObjectMask> spatiallyConnected, Integer identifier, Set<Integer> unprocessed) {

        unprocessed.remove(identifier);

        ObjectMask source = objects.get(identifier);
        spatiallyConnected.add(source);
        List<Integer> queue = tree.intersectsWith(source.boundingBox());

        while (!queue.isEmpty()) {
            Integer current = queue.remove(0);

            if (unprocessed.contains(current)) {
                unprocessed.remove(current);

                ObjectMask currentObject = objects.get(current);
                spatiallyConnected.add(currentObject);
                queue.addAll(tree.intersectsWith(currentObject.boundingBox()));
            }
        }
    }
}
