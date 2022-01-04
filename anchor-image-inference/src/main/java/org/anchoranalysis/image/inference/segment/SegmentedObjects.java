/*-
 * #%L
 * anchor-plugin-image
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
package org.anchoranalysis.image.inference.segment;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.bean.segment.reduce.ReduceElements;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.scale.RelativeScaleCalculator;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Objects that are a result of an instance-segmentation.
 *
 * <p>Each object has an associated confidence score, and an associated class-label.
 *
 * <p>Internally, objects are partitioned by class-label.
 *
 * @author Owen Feehan
 */
public class SegmentedObjects {

    /**
     * Tree-keys and list-values are selected in the multi-map to preserve ordering after
     * transformations.
     */
    private final List<LabelledWithConfidence<MultiScaleObject>> list;

    /** The segmented-objects, at two different scales. */
    @Getter private final DualScale<SegmentedObjectsAtScale> objects;

    /** The background image to use for segmentation, when visualizing segmentations. */
    private final DualScale<Stack> background;

    /**
     * Create for a collection of objects with an <b>identical label</b>.
     *
     * @param classLabel the label that applies to each object in {@code objects}.
     * @param objects the objects with label {@code classLabel}.
     * @param background background-images used for visualizing the segmentation, at two respective
     *     scales.
     * @param executionTimeRecorder records the execution-time of particular operations.
     */
    public SegmentedObjects(
            String classLabel,
            Collection<WithConfidence<MultiScaleObject>> objects,
            DualScale<Stack> background,
            ExecutionTimeRecorder executionTimeRecorder) {
        this(addLabel(classLabel, objects), background, executionTimeRecorder);
    }

    /**
     * Create for a collection of objects with <b>potentially differing labels</b>.
     *
     * @param objects the objects that are the result of the segmentation, with associated
     *     confidence and labels.
     * @param background background-images used for visualizing the segmentation, at two respective
     *     scales.
     * @param executionTimeRecorder records the execution-time of particular operations.
     */
    public SegmentedObjects(
            List<LabelledWithConfidence<MultiScaleObject>> objects,
            DualScale<Stack> background,
            ExecutionTimeRecorder executionTimeRecorder) {
        this.list = objects;
        this.background = background;
        this.objects =
                new DualScale<>(
                        new SegmentedObjectsAtScale(
                                list,
                                MultiScaleObject::getInputScale,
                                background.atInputScale(),
                                executionTimeRecorder,
                                " (input-scale)"),
                        new SegmentedObjectsAtScale(
                                list,
                                MultiScaleObject::getModelScale,
                                background.atModelScale(),
                                executionTimeRecorder,
                                " (model-scale)"));
    }

    /**
     * Reduces the segmented-objects, applying a reduction algorithm separately to each
     * object-class.
     *
     * @param reduce the algorithm used to reduce each object-class.
     * @param separateEachLabel if true, each label is reduced separately. if false, all labels are
     *     reduced together.
     * @param executionTimeRecorder records the execution-time of particular operations.
     * @return a new {@link SegmentedObjects} with the {@code reduce} algorithm applied to each
     *     object-class, reusing the existing objects.
     * @throws OperationFailedException if the reduction fails on any object-class.
     */
    public SegmentedObjects reduce(
            ReduceElements<ObjectMask> reduce,
            boolean separateEachLabel,
            ExecutionTimeRecorder executionTimeRecorder)
            throws OperationFailedException {

        DualScale<Extent> extent = objects.map(SegmentedObjectsAtScale::extent);

        // What's needed to scale a model-scale object to input-scale.
        ScaleFactor scaleFactor =
                extent.apply(
                        (input, model) ->
                                RelativeScaleCalculator.relativeScale(model, input, false));

        SegmentedObjectsReducer reducer =
                new SegmentedObjectsReducer(
                        list,
                        objects.atModelScale().listWithLabels(),
                        objects.atModelScale().extent(),
                        reduce,
                        background,
                        executionTimeRecorder,
                        element ->
                                new MultiScaleObject(
                                        () ->
                                                element.scale(
                                                        scaleFactor,
                                                        Optional.of(extent.atInputScale())),
                                        () -> element));
        return reducer.reduce(separateEachLabel);
    }

    /***
     * The object-mask with the highest confidence.
     *
     * @return the highest-confidence object-mask or {@link Optional#empty} if no objects exist.
     */
    public Optional<WithConfidence<MultiScaleObject>> highestConfidence() {
        return list.stream()
                .map(LabelledWithConfidence::getWithConfidence)
                .max((a, b) -> Double.compare(a.getConfidence(), b.getConfidence()));
    }

    /**
     * The total number of segmented objects.
     *
     * @return the total number.
     */
    public int size() {
        return list.size();
    }

    /**
     * Whether no segmented objects exist.
     *
     * @return true if no segmented objects exist, false otherwise.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /** Converts a collection from {@link WithConfidence} to {@link LabelledWithConfidence}. */
    private static List<LabelledWithConfidence<MultiScaleObject>> addLabel(
            String label, Collection<WithConfidence<MultiScaleObject>> objects) {
        return FunctionalList.mapToList(
                objects.stream(),
                withConfidence -> new LabelledWithConfidence<>(label, withConfidence));
    }
}
