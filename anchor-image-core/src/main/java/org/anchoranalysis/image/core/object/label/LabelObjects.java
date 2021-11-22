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
package org.anchoranalysis.image.core.object.label;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundedList;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Writes a unique ID (successive integer IDs) for each elements's voxels into a channel, and 0 for
 * background.
 *
 * <p>An element can be an object-mask or any other class from which an object-mask can be derived.
 *
 * @author Owen Feehan
 * @param <T> element-type, which must be an object-mask or an element containing an object-mask.
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class LabelObjects<T> {

    /**
     * An operation that is applied <i>after</i> any elements have been added to {@code mapLabels}
     * but <i>before</i> the voxels are written with the labelled intensity values
     */
    private Optional<UnaryOperator<T>> operationBeforeLabelling = Optional.empty();

    /**
     * if set, this is called once with any overlapping object (after {@code operationAfterMap is
     * applied}). Otherwise, an exception is thrown if an overlapping object is encountered.
     */
    private Optional<Consumer<OverlappingObject<T>>> overlappingObjectConsumer = Optional.empty();

    /** Extracts an object-mask representation from an element */
    private final Function<T, ObjectMask> extractObject;

    /** Shifts the element in the positive direction. */
    private final BiFunction<T, ReadableTuple3i, T> shiftElement;

    /**
     * Creates a channel that contains a unique integer (label) for each element's
     * object-representation.
     *
     * <p>Specifically, a channel is created that is exactly the same-size as the bounding-box
     * around a collection of element, and labels the background with 0, and each element in the
     * list with an incrementing unique integer identifier.
     *
     * @param elements the elements to write IDs for and a bounding-box that contains them all.
     * @param mapLabels if set, an entry is added for every labelled element (unscaled) to the label
     *     it is assigned.
     * @return a channel of the same-size as the containing bounding-box in {@code elements} and
     *     with unique IDs for each element, and 0 for voxels not belonging to any element.
     * @throws CreateException if there are more than 255 objects, or if two objects overlap when
     * this cannot handled gracefully.
     */
    public Channel createLabelledChannel(
            BoundedList<T> elements, Optional<Map<Integer, T>> mapLabels) throws CreateException {

        // A channel that is exactly the size of the bounding box for the elements
        Channel channel =
                ChannelFactory.instance()
                        .createEmptyInitialisedToSupportMaxValue(
                                new Dimensions(elements.boundingBox().extent()),
                                elements.size() + 1l);

        ReadableTuple3i shiftBack = elements.boundingBox().cornerMin();
        Point3i shiftBackMult = Point3i.immutableScale(shiftBack, -1);
        performLabelling(
                channel,
                elements.list(),
                mapLabels,
                element -> shiftElement.apply(maybeApplyBefore(element), shiftBackMult));

        return channel;
    }

    /**
     * Replaces the contents of the channel so that all element's voxels are labelled with
     * successive unique integer identifiers 1,2,3 etc. and voxels in no element are 0.
     *
     * @param channel the channel whose voxels will be replaced
     * @param elements the element to write IDs for
     * @param mapLabels if set, an entry is added for every labelled element (unscaled) to the label
     *     it is assigned
     * @throws CreateException if there are more than 255 objects, or if two objects overlap (and
     *     {@code overlappingObjectConsumer} is not set).
     */
    public void labelElements(
            Channel channel, List<T> elements, Optional<Map<Integer, T>> mapLabels)
            throws CreateException {
        performLabelling(channel, elements, mapLabels, this::maybeApplyBefore);
    }

    /** Applies the operation to an element, if such an operation is defined. */
    private T maybeApplyBefore(T element) {
        if (operationBeforeLabelling.isPresent()) {
            return operationBeforeLabelling.get().apply(element);
        } else {
            return element;
        }
    }

    /**
     * Writes an incrementing sequence labels for each element to voxels (and 0 for background)
     *
     * @param channel the channel containing voxels
     * @param elements the elements to write labels for
     * @param mapLabelsToBefore if set, an entry is added for each label to the before version of an
     *     element (before scaling any any other operation)
     * @param operationAfterMap an operation applied to each element after they are maybe added to
     *     {@code mapLabelsToBefore} but before their labels are written to voxels
     * @throws CreateException if there are more than 255 objects, or if two objects overlap (and
     *     {@code overlappingObjectConsumer} is not set).
     */
    private void performLabelling(
            Channel channel,
            List<T> elements,
            Optional<Map<Integer, T>> mapLabelsToBefore,
            UnaryOperator<T> operationAfterMap)
            throws CreateException {

        int index = 1;
        for (T element : elements) {

            ObjectMask objectAfterOp = extractObject.apply(operationAfterMap.apply(element));

            boolean voxelsAssigned =
                    channel.assignValue(index)
                            .toObjectWhile(objectAfterOp, voxelValue -> voxelValue == 0);
            if (voxelsAssigned) {
                // Add mapping from label to input-element
                if (mapLabelsToBefore.isPresent()) {
                    mapLabelsToBefore.get().put(index, element);
                }

                index++;

                if (index == channel.getVoxelDataType().maxValue()) {
                    throw new CreateException(
                            String.format(
                                    "A maximum of %d (non-overlapping) elements are allowed, and this threshold has been reached. There are %d elements in total (overlapping or not).",
                                    channel.getVoxelDataType().maxValue(), elements.size()));
                }
            } else {
                processOverlappingObject(new OverlappingObject<>(element, objectAfterOp));
            }
        }
    }

    private void processOverlappingObject(OverlappingObject<T> overlappingObject)
            throws CreateException {
        if (overlappingObjectConsumer.isPresent()) {
            overlappingObjectConsumer.get().accept(overlappingObject);
        } else {
            // A failure occurred, so it is not possible to write this object
            throw new CreateException(
                    "The object-mask of an element overlaps with another, which is not currently supported for scaling.");
        }
    }
}
