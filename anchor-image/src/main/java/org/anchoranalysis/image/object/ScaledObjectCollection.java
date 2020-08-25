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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.scale.ScaleFactor;

/**
 * When scaling an object-collection, so as to keep boundaries between objects exact, the objects
 * need to be scaled collectively using a particular interpolation procedure rather than
 * collectively.
 *
 * <p>If any objects overlap with other objects, they are scaled separately independently of the
 * others.
 *
 * @author Owen Feehan
 */
public class ScaledObjectCollection {

    private final Map<ObjectMask, ObjectMask> objectsScaled;

    /**
     * Objects that have been scaled
     *
     * @param objects objects to be scaled (after possibly a preoperation)
     * @param scaleFactor how to scale the objects by
     * @param preOperation operation applied to each-object before it is scaled (e.g. flattening)
     * @param postOperation operation applied to each-object after it is scaled (e.g. flattening)
     * @throws CreateException
     */
    public ScaledObjectCollection(
            ObjectCollection objects,
            ScaleFactor scaleFactor,
            Optional<UnaryOperator<ObjectMask>> preOperation,
            Optional<UnaryOperator<ObjectMask>> postOperation)
            throws CreateException {

        if (objects.isEmpty()) {
            // Early exit if the collection is empty
            objectsScaled = new HashMap<>();
            return;
        }

        ObjectsWithBoundingBox objectsWithBox = objectsWithBox(objects);

        ScaledLabelledObjects scaledObjects =
                new ScaledLabelledObjects(objectsWithBox, preOperation, scaleFactor);

        objectsScaled = scaledObjects.buildMapOfAllScaledObjects(postOperation);
    }

    /**
     * Gets the scaled version of an object given its unscaled input (which must be an identical
     * object to that passed into the constructor)
     *
     * @param object the unscaled-object
     * @return the corresponding scaled object
     * @throws GetOperationFailedException if the unscaled object was never passed into the
     *     constructor
     */
    public ObjectMask scaledObjectFor(Object object) throws GetOperationFailedException {
        ObjectMask scaled = objectsScaled.get(object);
        if (scaled == null) {
            throw new GetOperationFailedException(
                    object.toString(),
                    "No scaled-object exists in the collection corresponding to the input object");
        }
        return scaled;
    }

    /**
     * Returns the scaled-objects as an object-collection but without preserving the order of the
     * unscaled objects in the collection.
     *
     * <p>The total number of objects is identical to the unscaled objects, just the order may
     * differ.
     *
     * @return a collection with the scaled-objects
     */
    public ObjectCollection asCollectionOrderNotPreserved() {
        return ObjectCollectionFactory.of(objectsScaled.values());
    }

    public int size() {
        return objectsScaled.size();
    }

    private static ObjectsWithBoundingBox objectsWithBox(ObjectCollection objects) {
        try {
            // Create a raster that contains a unique ID for each object in the collection
            return new ObjectsWithBoundingBox(objects);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
