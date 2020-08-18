package org.anchoranalysis.image.object;

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
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorImgLib2NearestNeighbor;
import org.anchoranalysis.image.scale.ScaleFactor;

/**
 * Objects labelled and scaled.
 *
 * <p>Objects are variously scaled collectively in a raster if non-overlapping (to produce the
 * tightest borders between neighbouring objects), or else independently if overlapping.
 *
 * @author Owen Feehan
 */
class ScaledLabelledObjects {

    private static final Interpolator INTERPOLATOR = createInterpolatorForLabels();

    private Map<Integer, ObjectMask> labelMapping = new HashMap<>();

    private List<OverlappingObject> objectsOverlap = new ArrayList<>();

    private final Channel labelsScaled;
    private final ScaleFactor scaleFactor;
    private final int numberLabels;
    private final Point3i cornerScaled;

    public ScaledLabelledObjects(
            ObjectsWithBoundingBox objectsWithBox,
            Optional<UnaryOperator<ObjectMask>> preOperation,
            ScaleFactor scaleFactor)
            throws CreateException {
        this.scaleFactor = scaleFactor;

        Channel labels = createLabels(objectsWithBox, preOperation);

        // Scale the raster with nearest neighbour interpolation
        labelsScaled = labels.scaleXY(scaleFactor, INTERPOLATOR);

        // Inferring the number of labels that have been produced
        numberLabels = objectsWithBox.size() - objectsOverlap.size();

        cornerScaled = scaleCorner(objectsWithBox.boundingBox().cornerMin(), scaleFactor);
    }

    /**
     * Constructs a map from unscaled to scale for all objects (whether overlapping or not)
     *
     * @throws CreateException
     */
    public Map<ObjectMask, ObjectMask> buildMapOfAllScaledObjects(
            Optional<UnaryOperator<ObjectMask>> postOperation) throws CreateException {

        UnaryOperator<ObjectMask> postOperationWithShift =
                object -> shiftByAndMaybePost(object, cornerScaled, postOperation);

        // Read the objects from the raster Ids, and create a corresponding scaled object
        Map<ObjectMask, ObjectMask> map =
                new CreateObjectsFromLabels(labelsScaled.voxels().any(), 1, numberLabels)
                        .create(labelMapping, postOperationWithShift);

        // Scaling the overlapping objects
        objectsOverlap.stream()
                .forEach(
                        overlappingObject ->
                                scaleObjectIndependently(
                                        overlappingObject,
                                        labelsScaled.extent(),
                                        scaleFactor,
                                        map,
                                        postOperationWithShift));

        return map;
    }

    /**
     * Creates a channel with labels for the unscaled (non-overlapping objects) and places
     * overlapping objects in {@code objectsOverlap}
     */
    private Channel createLabels(
            ObjectsWithBoundingBox objectsWithBox, Optional<UnaryOperator<ObjectMask>> preOperation)
            throws CreateException {
        LabelObjects labeller = new LabelObjects(preOperation, Optional.of(objectsOverlap::add));
        return labeller.createLabelledChannelForObjects(objectsWithBox, Optional.of(labelMapping));
    }

    /**
     * Shifts an object-backwards and maybe applies a post-operation
     *
     * @param object the object to shift back (and maybe apply a post-operation after shifting)
     * @param shift how much to shift the object by
     * @return the object shifted and with the post-operation applied (if it's defined)
     */
    private ObjectMask shiftByAndMaybePost(
            ObjectMask object,
            ReadableTuple3i shift,
            Optional<UnaryOperator<ObjectMask>> postOperation) {
        ObjectMask shifted = object.shiftBy(shift);
        if (postOperation.isPresent()) {
            return postOperation.get().apply(shifted);
        } else {
            return shifted;
        }
    }

    /** Scales an object independently of the others, and adds to the map */
    private static void scaleObjectIndependently(
            OverlappingObject unscaled,
            Extent extent,
            ScaleFactor scaleFactor,
            Map<ObjectMask, ObjectMask> map,
            UnaryOperator<ObjectMask> postOp) {
        ObjectMask scaled = unscaled.getAfterPreoperation().scale(scaleFactor, Optional.of(extent));
        map.put(unscaled.getOriginal(), postOp.apply(scaled));
    }

    private static Point3i scaleCorner(ReadableTuple3i cornerUnscaled, ScaleFactor scaleFactor) {
        Point3i corner = new Point3i(cornerUnscaled);
        corner.scaleX(scaleFactor.x());
        corner.scaleY(scaleFactor.y());
        return corner;
    }

    /**
     * We use a nearest neighbour interpolator as we want only distinct labels as an output,
     * therefore no combining of intensity values
     *
     * @return a nearest-neighbour interpolator with boundaries extended as 0
     */
    private static Interpolator createInterpolatorForLabels() {
        InterpolatorImgLib2NearestNeighbor interpolator = new InterpolatorImgLib2NearestNeighbor();
        interpolator.extendWith(0);
        return interpolator;
    }
}
