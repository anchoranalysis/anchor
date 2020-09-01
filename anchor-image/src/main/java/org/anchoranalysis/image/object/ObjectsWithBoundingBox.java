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
package org.anchoranalysis.image.object;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.object.combine.ObjectMaskMerger;
import org.anchoranalysis.image.object.factory.ObjectCollectionFactory;

/**
 * One or more objects with the a bounding-box that contains them all fully
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class ObjectsWithBoundingBox {

    /**
     * The objects in the collection. This collection should not be altered after the constructor
     * (treated immutably).
     */
    @Getter private final ObjectCollection objects;

    /** A bounding-box that must contain all objects in the collection */
    @Getter private final BoundingBox boundingBox;

    /**
     * Creates for a single object, using its current bounding-box.
     *
     * @param object the object
     */
    public ObjectsWithBoundingBox(ObjectMask object) {
        this.objects = ObjectCollectionFactory.of(object);
        this.boundingBox = object.boundingBox();
    }

    /**
     * Creates for a collection of objects, minimally fitting a bounding-box around all
     * objects
     *
     * @param objects objects
     * @throws OperationFailedException if the object-collection is empty
     */
    public ObjectsWithBoundingBox(ObjectCollection objects) throws OperationFailedException {
        Preconditions.checkArgument(!objects.isEmpty());
        this.objects = objects;
        this.boundingBox = ObjectMaskMerger.mergeBoundingBoxes(objects);
    }

    /**
     * Maps the containing bounding-box to a larger one (that must contain the existing box)
     *
     * @param boundingBoxToAssign the new bounding-box to assign
     * @return newly-created with the same object-container but a different bounding-box
     * @throws OperationFailedException if the new bounding-box does not contain the existing one
     */
    public ObjectsWithBoundingBox mapBoundingBoxToBigger(BoundingBox boundingBoxToAssign)
            throws OperationFailedException {
        return new ObjectsWithBoundingBox(objects, boundingBoxToAssign);
    }

    /**
     * Maps the containing-box to the entire image-dimensions, and changes each object-mask to
     * belong to the entiore dimensions
     *
     * @param dimensions the scene dimensions
     * @return newly-created with new objects and a new bounding-box
     * @throws OperationFailedException if the image-dimensions don't contain the existing
     *     bounding-boc
     */
    public ObjectsWithBoundingBox mapObjectsToUseEntireImage(Dimensions dimensions)
            throws OperationFailedException {
        if (!dimensions.contains(boundingBox)) {
            throw new OperationFailedException(
                    String.format(
                            "The dimensions-box to be assigned (%s) must contain the existing bounding-box (%s), but it does not",
                            dimensions, boundingBox));
        }
        BoundingBox boxToAssign = new BoundingBox(dimensions);
        return new ObjectsWithBoundingBox(
                objects.stream().mapBoundingBoxChangeExtent(boxToAssign), boxToAssign);
    }

    /**
     * Adds objects without changing the bounding-box
     *
     * <p>The operation is <i>immutable</i>.
     *
     * @param objectsToAdd objects to add (unchanged)
     * @return a newly created {@link ObjectsWithBoundingBox} with the combined objects and the same
     *     bounding-box
     */
    public ObjectsWithBoundingBox addObjectsNoBoundingBoxChange(ObjectCollection objectsToAdd) {
        ObjectCollection combined = ObjectCollectionFactory.of(objects, objectsToAdd);
        for (ObjectMask toAdd : objectsToAdd) {
            assert (boundingBox.intersection().existsWith(toAdd.boundingBox()));
        }
        return new ObjectsWithBoundingBox(combined, boundingBox);
    }

    /** The number of objects */
    public int size() {
        return objects.size();
    }

    /** Gets a particular object */
    public ObjectMask get(int index) {
        return objects.get(index);
    }
}
