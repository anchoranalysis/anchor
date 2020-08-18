package org.anchoranalysis.image.object;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.ImageDimensions;

/**
 * Writes a unique ID (successive integer IDs) for each object's voxels into a channel, and 0 for
 * background.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@NoArgsConstructor
public class LabelObjects {

    /**
     * An operation that is applied <i>after</i> any objects have been added to {@code mapLabels}
     * but <i>before</i> the voxels are written with the labelled intensity values
     */
    private Optional<UnaryOperator<ObjectMask>> operationBeforeLabelling = Optional.empty();

    /**
     * if set, this is called once with any overlapping object (after {@code operationAfterMap is
     * applied}). Otherwise, an exception is thrown if an overlapping object is encountered.
     */
    private Optional<Consumer<OverlappingObject>> overlappingObjectConsumer = Optional.empty();

    /**
     * Creates a channel that is exactly the same-size as the bounding-box around a collection of
     * objects, and labels the background with 0, and each object in the collection with an
     * incrementing unique integer identifier
     *
     * @param objects the objects to write IDs for
     * @param mapLabels if set, an entry is added for every labelled object from object (unscaled)
     *     to the label it is assigned
     * @return a channel of the same-size as the bounding-box associated with {@code objects} and
     *     with unique IDs for each object, and 0 for voxels not on any object
     * @throws CreateException
     */
    public Channel createLabelledChannelForObjects(
            ObjectsWithBoundingBox objects, Optional<Map<Integer, ObjectMask>> mapLabels)
            throws CreateException {

        // A channel that is exactly the size of the bounding box for the objects
        Channel channel =
                ChannelFactory.instance()
                        .createEmptyInitialisedToSupportMaxValue(
                                new ImageDimensions(objects.boundingBox().extent()),
                                (long) (objects.size() + 1));

        ReadableTuple3i shiftBack = objects.boundingBox().cornerMin();
        performLabelling(
                channel,
                objects.objects(),
                mapLabels,
                object -> shiftObjectBack(maybeApplyBefore(object), shiftBack));

        return channel;
    }

    /**
     * Replaces the contents of the channel so that all objects voxels are labelled with successive
     * unique integer identifiers 1,2,3 etc. and voxels in no object are 0.
     *
     * @param channel the channel whose voxels will be replaced
     * @param objects the objects to write IDs for
     * @param mapLabels if set, an entry is added for every labelled object from object (unscaled)
     *     to the label it is assigned
     * @throws CreateException
     */
    public void labelObjectsInChannel(
            Channel channel, ObjectCollection objects, Optional<Map<Integer, ObjectMask>> mapLabels)
            throws CreateException {
        performLabelling(channel, objects, mapLabels, this::maybeApplyBefore);
    }

    /** Shifts an object's bounding-box backwards */
    private ObjectMask shiftObjectBack(ObjectMask object, ReadableTuple3i shiftBack) {
        return object.shiftBackBy(shiftBack);
    }

    /** Applies the operation to an object, if it's defined */
    private ObjectMask maybeApplyBefore(ObjectMask object) {
        if (operationBeforeLabelling.isPresent()) {
            return operationBeforeLabelling.get().apply(object);
        } else {
            return object;
        }
    }

    /**
     * Writes an incrementing sequence labels for each object to voxels (and 0 for background)
     *
     * @param channel the channel containing voxels
     * @param objects the objects to write labels for
     * @param mapLabelsToBefore if set, an entry is added for each label to the before version of an
     *     object-mask (before scaling any any other operation)
     * @param operationAfterMap an operation applied to each-object mask after they are maybe added
     *     to {@code mapLabelsToBefore} but before their labels are written to voxels
     * @param overlappingObjectConsumer if set, this is called once with any overlapping object
     *     (after {@code operationAfterMap is applied}). Otherwise, an exception is thrown if an
     *     overlapping object is encountered.
     * @throws CreateException if there are more than 255 objects, or if two objects overlap (and
     *     {@code overlappingObjectConsumer} is not set)
     */
    private void performLabelling(
            Channel channel,
            ObjectCollection objects,
            Optional<Map<Integer, ObjectMask>> mapLabelsToBefore,
            UnaryOperator<ObjectMask> operationAfterMap)
            throws CreateException {

        channel.assignValue(0).toAll();

        int index = 1;
        for (ObjectMask object : objects) {

            ObjectMask objectAfterOp = operationAfterMap.apply(object);

            boolean voxelsAssigned =
                    channel.assignValue(index)
                            .toObject(objectAfterOp, voxelValue -> voxelValue == 0);
            if (voxelsAssigned) {
                // Add mapping from label to input-object
                if (mapLabelsToBefore.isPresent()) {
                    mapLabelsToBefore.get().put(index, object);
                }

                index++;

                if (index == channel.getVoxelDataType().maxValue()) {
                    throw new CreateException(
                            String.format(
                                    "A maximum of %d (non-overlapping) objects are allowed, and this threshold has been reached. There are %d objects in total (overlapping or not).",
                                    channel.getVoxelDataType().maxValue(), objects.size()));
                }
            } else {
                processOverlappingObject(new OverlappingObject(object, objectAfterOp));
            }
        }
    }

    private void processOverlappingObject(OverlappingObject overlappingObject)
            throws CreateException {
        if (overlappingObjectConsumer.isPresent()) {
            overlappingObjectConsumer.get().accept(overlappingObject);
        } else {
            // A failure occurred, so it is not possible to write this object
            throw new CreateException(
                    "An object-mask overlaps with another, which is not currently supported for scaling.");
        }
    }
}
