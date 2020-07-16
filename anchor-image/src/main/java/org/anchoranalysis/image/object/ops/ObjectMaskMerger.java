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
/* (C)2020 */
package org.anchoranalysis.image.object.ops;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.mask.MaskInverter;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

/** Merges one or more {@link ObjectMask}s into a single mask */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMaskMerger {

    /**
     * Merges two objects together
     *
     * <p>This is an IMMUTABLE operation.
     *
     * <p>The merged box has a minimal bounding-box to fit both objects.
     *
     * <p>Even if the two existing object-masks do not intersect or touch, a single merged mask is
     * nevertheless created.
     *
     * <p>It assumes that the binary-values of the merges are always 255 and 0, or 0 and 255.
     *
     * @param first first-object to merge
     * @param second second-object to merge
     * @return first and second merged together
     * @throws AnchorFriendlyRuntimeException if incompatible binary-values exist in the objects for
     *     merging
     */
    public static ObjectMask merge(ObjectMask first, ObjectMask second) {

        second = invertSecondIfNecessary(first, second);

        BoundingBox bbox = first.getBoundingBox().union().with(second.getBoundingBox());

        ObjectMask out = new ObjectMask(bbox, VoxelBoxFactory.getByte().create(bbox.extent()));
        copyPixelsCheckMask(first, out, bbox);
        copyPixelsCheckMask(second, out, bbox);
        return out;
    }

    /**
     * Merges all the bounding boxes of a collection of objects.
     *
     * @param objects objects whose bounding-boxes are merged
     * @return a bounding-box just large enough to include all the bounding-boxes of the objects
     * @throws OperationFailedException if the {@code objects} parameter is empty
     */
    public static BoundingBox mergeBoundingBoxes(ObjectCollection objects)
            throws OperationFailedException {

        if (objects.isEmpty()) {
            throw new OperationFailedException("At least one object must exist in the collection");
        }

        return objects.streamStandardJava()		// NOSONAR
                .map(ObjectMask::getBoundingBox)
                .reduce( // NOSONAR
                        (boundingBox, other) -> boundingBox.union().with(other))
                .get();
    }

    /**
     * Merges all the objects together that are found in a collection
     *
     * @param objects objects to be merged
     * @return a newly created merged version of all the objects, with a bounding-box just big
     *     enough to include all the existing mask bounding-boxes
     * @throws OperationFailedException if any two objects with different binary-values are merged.
     */
    public static ObjectMask merge(ObjectCollection objects) throws OperationFailedException {

        if (objects.size() == 0) {
            throw new OperationFailedException("There must be at least one object");
        }

        if (objects.size() == 1) {
            return objects.get(0).duplicate(); // So we are always guaranteed to have a new object
        }

        BoundingBox bbox = mergeBoundingBoxes(objects);

        ObjectMask objectOut =
                new ObjectMask(bbox, VoxelBoxFactory.getByte().create(bbox.extent()));

        BinaryValues bv = null;
        for (ObjectMask objectMask : objects) {

            if (bv != null) {
                if (!objectMask.getBinaryValues().equals(bv)) {
                    throw new OperationFailedException(
                            "Cannot merge. Incompatible binary values among object-collection");
                }
            } else {
                bv = objectMask.getBinaryValues();
            }

            copyPixelsCheckMask(objectMask, objectOut, bbox);
        }

        return objectOut;
    }

    private static void copyPixelsCheckMask(
            ObjectMask source, ObjectMask destination, BoundingBox bbox) {

        Point3i pointDest = source.getBoundingBox().relPosTo(bbox);
        Extent e = source.getBoundingBox().extent();

        source.getVoxelBox()
                .copyPixelsToCheckMask(
                        new BoundingBox(e),
                        destination.getVoxelBox(),
                        new BoundingBox(pointDest, e),
                        source.getVoxelBox(),
                        source.getBinaryValuesByte());
    }

    /**
     * Inverts the binary-values of the second mask if necessary to match the first
     *
     * @param first first-object to merge
     * @param second second-object to merge
     * @return the second object, possibly inverted (if this gives it identical binary-values to the
     *     first)
     * @throws AnchorFriendlyRuntimeException if the binary-values aren't identical, before or after
     *     inversion
     */
    private static ObjectMask invertSecondIfNecessary(ObjectMask first, ObjectMask second) {
        // If we don't have identical binary values, we invert the second one
        if (!second.getBinaryValues().equals(first.getBinaryValues())) {

            if (second.getBinaryValues().createInverted().equals(first.getBinaryValues())) {
                return MaskInverter.invertObjectDuplicate(second);
            } else {
                throw new AnchorFriendlyRuntimeException(
                        "The two objects to be merged have binary-values that are impossible to merge");
            }

        } else {
            return second;
        }
    }
}
