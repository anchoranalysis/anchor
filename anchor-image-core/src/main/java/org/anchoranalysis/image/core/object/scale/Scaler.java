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
package org.anchoranalysis.image.core.object.scale;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Scales object-masks (or other more generic elements) collectively to avoid undesired overlap.
 *
 * <p>If each object-mask is scaled independently, touching but non-overlapping objects can become
 * overlapping when scaled. This provides a <i>collective</i> scaling procedure that avoids this
 * using nearest-neighbor interpolation.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Scaler {

    private static final AccessObjectMask<ObjectMask> ACCESS_OBJECTS = new AccessSimple();

    /**
     * Scales every object-mask in a collection
     *
     * <p>It is desirable scale objects together, as interpolation can be done so that adjacent
     * boundaries pre-scaling remain adjacent after scaling (only if there's no overlap among them).
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param objects objects to scale
     * @param factor scaling-factor
     * @return a new collection with scaled object-masks (existing object-masks are unaltered)
     * @throws OperationFailedException
     */
    public static ScaledElements<ObjectMask> scaleObjects(
            ObjectCollection objects, ScaleFactor factor) throws OperationFailedException {
        return scaleObjects(objects, factor, Optional.empty(), Optional.empty());
    }

    /**
     * Scales every object-mask in a collection, ensuring the results remain inside a particular
     * region.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * <p>Like {@link #scaleObjects(ObjectCollection,ScaleFactor)} but ensured the scaled-results
     * will always be inside a particular extent (clipping if necessary)
     *
     * @param objects objects to scale
     * @param factor scaling-factor
     * @param clipTo clips any objects after scaling to make sure they fit inside this extent
     * @return a new collection with scaled object-masks
     * @throws OperationFailedException
     */
    public static ScaledElements<ObjectMask> scaleObjects(
            ObjectCollection objects, ScaleFactor factor, Extent clipTo)
            throws OperationFailedException {
        try {
            return new ScaledElements<>(
                    objects.asList(),
                    factor,
                    Optional.empty(),
                    Optional.of(object -> object.clipTo(clipTo)),
                    ACCESS_OBJECTS);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Scales every object-mask in a collection, allowing for additional manipulation before and
     * after scaling.
     *
     * @param objects objects to scale
     * @param factor scaling-factor
     * @param preOperation applied to each-object before it is scaled (e.g. flattening)
     * @param postOperation applied to each-object after it is scaled (e.g. clipping to an extent)
     * @return a new collection with scaled object-masks (existing object-masks are unaltered)
     * @throws OperationFailedException
     */
    public static ScaledElements<ObjectMask> scaleObjects(
            ObjectCollection objects,
            ScaleFactor factor,
            Optional<UnaryOperator<ObjectMask>> preOperation,
            Optional<UnaryOperator<ObjectMask>> postOperation)
            throws OperationFailedException {
        try {
            return new ScaledElements<>(
                    objects.asList(), factor, preOperation, postOperation, ACCESS_OBJECTS);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Scales every element in a list collectively, ensuring the results remain inside a particular
     * region.
     *
     * <p>This is similar to {@link #scaleObjects(ObjectCollection, ScaleFactor, Extent)} but
     * accepts a parameterized type, rather than {@link ObjectMask}.
     *
     * @param <T> element-type.
     * @param elements objects to scale.
     * @param factor scaling-factor.
     * @param clipTo clips any objects after scaling to make sure they fit inside this extent.
     * @param access retrieves a corresponding bounding-box and {@link ObjectMask} from an element.
     * @return a new collection with scaled elements.
     * @throws OperationFailedException if the scaled-elements cannot be created successfully.
     */
    public static <T> ScaledElements<T> scaleElements(
            List<T> elements, ScaleFactor factor, Extent clipTo, AccessObjectMask<T> access)
            throws OperationFailedException {
        try {
            return new ScaledElements<>(
                    elements,
                    factor,
                    Optional.empty(),
                    Optional.of(object -> access.clipTo(object, clipTo)),
                    access);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
