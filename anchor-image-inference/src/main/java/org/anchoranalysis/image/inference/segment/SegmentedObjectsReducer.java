/*-
 * #%L
 * anchor-image-inference
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.bean.reduce.ObjectForReduction;
import org.anchoranalysis.image.inference.bean.reduce.ObjectForReductionFactory;
import org.anchoranalysis.image.inference.bean.segment.reduce.ReduceElements;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Reduces the number of objects in a {@link SegmentedObjects}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class SegmentedObjectsReducer {

    /**
     * Tree-keys and list-values are selected in the multi-map to preserve ordering after
     * transformations.
     */
    private final List<LabelledWithConfidence<MultiScaleObject>> list;

    /** The segmented-objects used for the reduction operation. */
    private final List<LabelledWithConfidence<ObjectMask>> objects;

    /** A containing-space in which all {@code objects} should reside. */
    private final Extent extent;

    /** The algorithm used to reduce a list of {@link ObjectMask}s. */
    private final ReduceElements<ObjectMask> reduce;

    /** The background image to use for segmentation, when visualizing segmentations. */
    private final DualScale<Stack> background;

    /** Records the execution-time of particular operations. */
    private final ExecutionTimeRecorder executionTimeRecorder;

    /** Converts a newly-created {@link ObjectMask} to a {@link MultiScaleObject}. */
    private final Function<ObjectMask, MultiScaleObject> convertToScale;

    /**
     * Reduces the segmented-objects, applying a reduction algorithm separately to each
     * object-class.
     *
     * @param separateEachLabel if true, each label is reduced separately. if false, all labels are
     *     reduced together.
     * @return a new {@link SegmentedObjects} with the {@code reduce} algorithm applied to each
     *     object-class, reusing the existing objects.
     * @throws OperationFailedException if the reduction fails on any object-class.
     */
    public SegmentedObjects reduce(boolean separateEachLabel) throws OperationFailedException {
        if (separateEachLabel) {
            // Apply the reduction algorithm on each label's objects separately.
            return new SegmentedObjects(
                    reducedObjectsSeparate(), background, executionTimeRecorder);
        } else {
            // Apply the reduction algorithm on all object, irrespective of label.
            return new SegmentedObjects(
                    reduceList(objects, extent, index -> index), background, executionTimeRecorder);
        }
    }

    /** Apply the reduction algorithm on each label's objects separately. */
    private List<LabelledWithConfidence<MultiScaleObject>> reducedObjectsSeparate()
            throws OperationFailedException {
        List<LabelledWithConfidence<MultiScaleObject>> out = new ArrayList<>();

        Multimap<String, ObjectForReduction> map = createObjectMap();

        for (String label : map.keySet()) {
            List<ObjectForReduction> forReduction = new ArrayList<>(map.get(label));
            List<LabelledWithConfidence<ObjectMask>> listForReduction =
                    FunctionalList.mapToList(
                            forReduction.stream(), ObjectForReduction::getLabelled);
            out.addAll(
                    reduceList(
                            listForReduction, extent, index -> forReduction.get(index).getIndex()));
        }
        return out;
    }

    /** Creates a {@link MultiMap} of all elements in {@code objects}, indexed by their label. */
    private Multimap<String, ObjectForReduction> createObjectMap() {
        Multimap<String, ObjectForReduction> map =
                MultimapBuilder.treeKeys().arrayListValues().build();
        ObjectForReductionFactory.populateFromList(objects)
                .forEach(object -> map.put(object.getLabel(), object));
        return map;
    }

    /** Reduces a particular {@link List} of elements, mapping the indices of the retain items. */
    private List<LabelledWithConfidence<MultiScaleObject>> reduceList(
            List<LabelledWithConfidence<ObjectMask>> elements,
            Extent extent,
            UnaryOperator<Integer> mapIndex)
            throws OperationFailedException {
        ReductionOutcome<LabelledWithConfidence<ObjectMask>> outcome =
                reduce.reduce(elements, extent, executionTimeRecorder);
        ReductionOutcome<LabelledWithConfidence<MultiScaleObject>> outcomeMapped =
                outcome.map(mapIndex, this::convertToMultiScale);
        return outcomeMapped.listAfter(list);
    }

    /**
     * Converts a {@link LabelledWithConfidence} from a {@link ObjectMask} to a {@link
     * MultiScaleObject}.
     */
    private LabelledWithConfidence<MultiScaleObject> convertToMultiScale(
            LabelledWithConfidence<ObjectMask> labelled) {
        return new LabelledWithConfidence<>(
                convertToScale.apply(labelled.getElement()),
                labelled.getConfidence(),
                labelled.getLabel());
    }
}
