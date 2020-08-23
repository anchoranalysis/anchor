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

package org.anchoranalysis.image.index;

import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * An R-Tree of object-masks (indexed via a derived bounding-box).
 *
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @see BoundingBoxRTree for a related structure only on bounding-boxes
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class ObjectCollectionRTree {

    /** An r-tree that stores indices of the objects for each bounding-box */
    private BoundingBoxRTree tree;

    /** All objects stored in the r-tree (whose order corresponds to indices in {@code delegate} */
    @Getter private ObjectCollection objects;

    /**
     * Constructor - creates an r-tree for particular objects
     *
     * @param objects the objects
     */
    public ObjectCollectionRTree(ObjectCollection objects) {
        this.objects = objects;
        tree = new BoundingBoxRTree(objects.size());

        for (int i = 0; i < objects.size(); i++) {
            tree.add(i, objects.get(i).boundingBox());
        }
    }

    public ObjectCollection contains(Point3i point) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(object -> object.contains(point), tree.contains(point));
    }

    public ObjectCollection intersectsWith(ObjectMask object) {
        // We do an additional check to make sure the point is inside the object,
        //  as points can be inside the Bounding Box but not inside the object
        return objects.stream()
                .filterSubset(
                        omInd -> omInd.hasIntersectingVoxels(object),
                        tree.intersectsWith(object.boundingBox()));
    }

    public ObjectCollection intersectsWith(BoundingBox box) {
        return objects.createSubset(tree.intersectsWith(box));
    }

    public List<Integer> intersectsWithAsIndices(BoundingBox box) {
        return tree.intersectsWith(box);
    }
}
