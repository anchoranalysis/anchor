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
package org.anchoranalysis.image.object.scale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundedList;
import org.anchoranalysis.image.extent.scale.ScaleFactor;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorImgLib2NearestNeighbor;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.label.DecodeLabels;
import org.anchoranalysis.image.object.label.LabelObjects;
import org.anchoranalysis.image.object.label.OverlappingObject;

/**
 * Elements labelled and scaled in a raster.
 *
 * <p>Elements (with object-representations) are variously scaled collectively in a raster if
 * non-overlapping (to produce the tightest borders between neighboring objects), or else
 * independently if overlapping.
 *
 * @author Owen Feehan
 */
class ScaledLabels<T> {

    private static final Interpolator INTERPOLATOR = createInterpolatorForLabels();

    private Map<Integer, T> labelMapping = new HashMap<>();

    private List<OverlappingObject<T>> objectsOverlap = new ArrayList<>();

    private final Channel labelsScaled;
    private final ScaleFactor scaleFactor;
    private final int numberLabels;
    private final Point3i cornerScaled;

    /** Access the object-mask-representation of elements of type {@code T}. */
    private final AccessObjectMask<T> access;

    public ScaledLabels(
            BoundedList<T> boundedElements,
            Optional<UnaryOperator<T>> preOperation,
            ScaleFactor scaleFactor,
            AccessObjectMask<T> access)
            throws CreateException {
        this.scaleFactor = scaleFactor;
        this.access = access;

        Channel labels = createLabels(boundedElements, preOperation);

        // Scale the raster with nearest neighbor interpolation
        this.labelsScaled = labels.scaleXY(scaleFactor, INTERPOLATOR);

        // Inferring the number of labels that have been produced
        this.numberLabels = boundedElements.size() - objectsOverlap.size();

        this.cornerScaled = scaleCorner(boundedElements.boundingBox().cornerMin(), scaleFactor);
    }

    /**
     * Constructs a map from unscaled to scaled for all objects (whether overlapping or not)
     *
     * @throws CreateException
     */
    public Map<T, T> buildMapOfAllScaledObjects(Optional<UnaryOperator<T>> postOperation)
            throws CreateException {

        UnaryOperator<T> postOperationWithShift =
                object -> shiftByAndMaybePost(object, cornerScaled, postOperation);

        // Read the objects from the raster Ids, and create a corresponding scaled object
        Map<T, T> map =
                new DecodeLabels<>(labelsScaled.voxels().any(), 1, numberLabels, access::createFrom)
                        .create(labelMapping, postOperationWithShift);

        // Scaling the overlapping objects
        for (int i = 0; i < objectsOverlap.size(); i++) {
            scaleObjectIndependently(
                    objectsOverlap.get(i),
                    i,
                    labelsScaled.extent(),
                    scaleFactor,
                    map,
                    postOperationWithShift);
        }

        return map;
    }

    /**
     * Creates a channel with labels for the unscaled (non-overlapping objects) and places
     * overlapping objects in {@code objectsOverlap}
     */
    private Channel createLabels(
            BoundedList<T> elementsWithBox, Optional<UnaryOperator<T>> preOperation)
            throws CreateException {
        LabelObjects<T> labeller =
                new LabelObjects<>(
                        preOperation,
                        Optional.of(objectsOverlap::add),
                        access::objectFor,
                        access::shiftBy);
        return labeller.createLabelledChannel(elementsWithBox, Optional.of(labelMapping));
    }

    /**
     * Shifts an object-backwards and maybe applies a post-operation
     *
     * @param object the object to shift back (and maybe apply a post-operation after shifting)
     * @param shift how much to shift the object by
     * @return the object shifted and with the post-operation applied (if it's defined)
     */
    private T shiftByAndMaybePost(
            T object, ReadableTuple3i shift, Optional<UnaryOperator<T>> postOperation) {
        T shifted = access.shiftBy(object, shift);
        if (postOperation.isPresent()) {
            return postOperation.get().apply(shifted);
        } else {
            return shifted;
        }
    }

    /** Scales an object independently of the others, and adds to the map */
    private void scaleObjectIndependently(
            OverlappingObject<T> unscaled,
            int index,
            Extent extent,
            ScaleFactor scaleFactor,
            Map<T, T> map,
            UnaryOperator<T> postOp) {
        ObjectMask scaled = unscaled.getAfterPreoperation().scale(scaleFactor, Optional.of(extent));
        map.put(unscaled.getOriginal(), postOp.apply(access.createFrom(index, scaled)));
    }

    private static Point3i scaleCorner(ReadableTuple3i cornerUnscaled, ScaleFactor scaleFactor) {
        Point3i corner = new Point3i(cornerUnscaled);
        corner.scaleX(scaleFactor.x());
        corner.scaleY(scaleFactor.y());
        return corner;
    }

    /**
     * We use a nearest neighbor interpolator as we want only distinct labels as an output,
     * therefore no combining of intensity values
     *
     * @return a nearest-neighbor interpolator with boundaries extended as 0
     */
    private static Interpolator createInterpolatorForLabels() {
        InterpolatorImgLib2NearestNeighbor interpolator = new InterpolatorImgLib2NearestNeighbor();
        interpolator.extendWith(0);
        return interpolator;
    }
}
