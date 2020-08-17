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

package org.anchoranalysis.image.object.ops;

import java.util.stream.Stream;
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
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

/** Merges one or more {@link ObjectMask}s into a single object */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMaskMerger {

    /**
     * Merges two objects together
     *
     * <p>This is an IMMUTABLE operation.
     *
     * <p>The merged box has a minimal bounding-box to fit both objects.
     *
     * <p>Even if the two existing objects do not intersect or touch, a single merged object is
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

        BoundingBox box = first.boundingBox().union().with(second.boundingBox());

        ObjectMask out =
                new ObjectMask(box, VoxelsFactory.getByte().createInitialized(box.extent()));
        copyPixelsCheckMask(first, out, box);
        copyPixelsCheckMask(second, out, box);
        return out;
    }

    /**
     * Merges all the bounding boxes of a collection of objects.
     *
     * @param objects a stream of objects whose bounding-boxes are to be merged
     * @return a bounding-box just large enough to include all the bounding-boxes of the objects
     * @throws OperationFailedException if the object-collection is empty
     */
    public static BoundingBox mergeBoundingBoxes(ObjectCollection objects)
            throws OperationFailedException {

        if (objects.isEmpty()) {
            throw new OperationFailedException(
                    "The object-collection is empty, at least one object is required for this operation");
        }

        return mergeBoundingBoxes(objects.streamStandardJava());
    }

    /**
     * Merges all the bounding boxes of a stream of objects.
     *
     * @param objects a stream of objects whose bounding-boxes are to be merged
     * @return a bounding-box just large enough to include all the bounding-boxes of the objects
     */
    public static BoundingBox mergeBoundingBoxes(Stream<ObjectMask> objects) {
        return objects // NOSONAR
                .map(ObjectMask::boundingBox)
                .reduce( // NOSONAR
                        (boundingBox, other) -> boundingBox.union().with(other))
                .get();
    }

    /**
     * Merges all the objects together that are found in a collection
     *
     * @param objects objects to be merged
     * @return a newly created merged version of all the objects, with a bounding-box just big
     *     enough to include all the existing objects' bounding-boxes
     * @throws OperationFailedException if any two objects with different binary-values are merged.
     */
    public static ObjectMask merge(ObjectCollection objects) throws OperationFailedException {

        if (objects.size() == 0) {
            throw new OperationFailedException("There must be at least one object");
        }

        if (objects.size() == 1) {
            return objects.get(0).duplicate(); // So we are always guaranteed to have a new object
        }

        BoundingBox box = mergeBoundingBoxes(objects.streamStandardJava());

        ObjectMask objectOut =
                new ObjectMask(box, VoxelsFactory.getByte().createInitialized(box.extent()));

        BinaryValues bv = null;
        for (ObjectMask objectMask : objects) {

            if (bv != null) {
                if (!objectMask.binaryValues().equals(bv)) {
                    throw new OperationFailedException(
                            "Cannot merge. Incompatible binary values among object-collection");
                }
            } else {
                bv = objectMask.binaryValues();
            }

            copyPixelsCheckMask(objectMask, objectOut, box);
        }

        return objectOut;
    }

    private static void copyPixelsCheckMask(
            ObjectMask source, ObjectMask destination, BoundingBox box) {

        Point3i pointDest = source.boundingBox().relativePositionTo(box);
        Extent extent = source.boundingBox().extent();

        source.extracter()
                .objectCopyTo(source, destination.voxels(), new BoundingBox(pointDest, extent));
    }

    /**
     * Inverts the binary-values of the second object-mask if necessary to match the first
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
        if (!second.binaryValues().equals(first.binaryValues())) {

            if (second.binaryValues().createInverted().equals(first.binaryValues())) {
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
