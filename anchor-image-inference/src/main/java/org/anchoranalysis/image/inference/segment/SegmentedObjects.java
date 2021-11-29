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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedUnaryOperator;
import org.anchoranalysis.image.core.object.scale.Scaler;
import org.anchoranalysis.image.inference.bean.segment.reduce.ReduceElements;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;
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
@NoArgsConstructor
public class SegmentedObjects {

    /**
     * Tree-keys and list-values are selected in the multi-map to preserve ordering after
     * transformations.
     */
    private final Multimap<String, WithConfidence<ObjectMask>> map =
            MultimapBuilder.treeKeys().arrayListValues().build();

    /**
     * Create for a collection of objects with an <b>identical label</b>.
     *
     * @param classLabel the label that applies to each object in {@code objects}.
     * @param objects the objects with label {@code classLabel}.
     */
    public SegmentedObjects(String classLabel, Collection<WithConfidence<ObjectMask>> objects) {
        add(classLabel, objects);
    }

    /**
     * Create for a collection of objects with <b>potentially differing labels</b>.
     *
     * @param objects
     */
    public SegmentedObjects(Stream<LabelledWithConfidence<ObjectMask>> objects) {
        objects.forEach(this::add);
    }

    /**
     * Adds one or more objects with an identical class-label.
     *
     * @param classLabel the label that applies to each object in {@code objects}.
     * @param objects the objects with label {@code classLabel}.
     */
    public void add(String classLabel, Collection<WithConfidence<ObjectMask>> objects) {
        this.map.putAll(classLabel, objects);
    }

    /**
     * Scales the segmented-objects.
     *
     * @param scaleFactor how much to scale by
     * @param extent an extent all objects are clipped to remain inside.
     * @return a segmented-objects with identical order, confidence-values etc. but with
     *     corresponding object-masks scaled.
     * @throws OperationFailedException
     */
    public SegmentedObjects scale(ScaleFactor scaleFactor, Extent extent)
            throws OperationFailedException {
        return mapValues(
                list ->
                        Scaler.scaleElements(
                                        list,
                                        scaleFactor,
                                        false,
                                        extent,
                                        new AccessSegmentedObjects(list))
                                .asListOrderPreserved(list));
    }

    /**
     * Reduces the segmented-objects, applying a reduction algorithm separately to each
     * object-class.
     *
     * @param reduce the algorithm used to reduce each object-class
     * @param separateEachLabel if true, each label is reduced separately. if false, all labels are
     *     reduced together.
     * @return a new {@link SegmentedObjects} with the {@code reduce} algorithm applied to each
     *     object-class, reusing the existing objects.
     * @throws OperationFailedException if the reduction fails on any object-class
     */
    public SegmentedObjects reduce(ReduceElements<ObjectMask> reduce, boolean separateEachLabel)
            throws OperationFailedException {
        if (separateEachLabel) {
            return mapValues(reduce::reduceUnlabelled);
        } else {
            return new SegmentedObjects(reduce.reduceLabelled(allLabelledObjects()).stream());
        }
    }

    /***
     * The object-mask with the highest confidence.
     *
     * @return the highest-confidence object-mask or {@link Optional#empty} if no objects exist.
     */
    public Optional<WithConfidence<ObjectMask>> highestConfidence() {
        return streamAllObjects()
                .max((a, b) -> Double.compare(a.getConfidence(), b.getConfidence()));
    }

    /**
     * Create a {@link ObjectCollection} of <i>all</i> contained objects, <i>excluding</i>
     * confidence.
     *
     * @return a newly created {@link ObjectCollection} that reuses the existing {@link ObjectMask}
     *     stored in the structure.
     */
    public ObjectCollection asObjects() {
        return new ObjectCollection(asList().stream().map(WithConfidence::getElement));
    }

    /**
     * Create a {@link List} of <i>all</i> contained objects, <i>including</i> confidence.
     *
     * @return a newly created {@link List} that reuses the existing {@link ObjectMask} stored in
     *     the structure.
     */
    public List<WithConfidence<ObjectMask>> asList() {
        return streamAllObjects().collect(Collectors.toList());
    }

    /**
     * Create a {@link Stream} of <i>all</i> contained objects, <i>including</i> confidence.
     *
     * @return a newly created {@link Stream} that reuses the existing {@link ObjectMask} stored in
     *     the structure.
     */
    public Stream<WithConfidence<ObjectMask>> streamAllObjects() {
        return map.entries().stream().map(Entry::getValue);
    }

    /**
     * Whether no segmented objects exist.
     *
     * @return true if no segmented objects exist, false otherwise.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Adds a single object.
     *
     * @param object the labelled-object to add
     */
    private void add(LabelledWithConfidence<ObjectMask> object) {
        this.map.put(object.getLabel(), object.getWithConfidence());
    }

    /** Creates a list of all labelled objects. */
    private List<LabelledWithConfidence<ObjectMask>> allLabelledObjects() {
        return map.entries().stream()
                .map(entry -> new LabelledWithConfidence<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Maps the objects associated with each object-class via a list.
     *
     * @param operator transforms the list of objects in a particular object-class to a new list
     * @return a new {@link SegmentedObjects} with a mapping applied to each object-class, reusing
     *     the existing objects.
     */
    private <E extends Exception> SegmentedObjects mapValues(
            CheckedUnaryOperator<List<WithConfidence<ObjectMask>>, E> operator) throws E {
        SegmentedObjects out = new SegmentedObjects();
        for (String key : this.map.keySet()) {
            List<WithConfidence<ObjectMask>> list =
                    this.map.get(key).stream().collect(Collectors.toList());
            out.map.putAll(key, operator.apply(list));
        }
        return out;
    }
}
