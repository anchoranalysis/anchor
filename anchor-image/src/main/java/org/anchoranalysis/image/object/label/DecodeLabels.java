/*-
 * #%L
 * anchor-plugin-ij
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

package org.anchoranalysis.image.object.label;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.points.PointRange;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;

/**
 * Decodes a labelled raster into derived objects or object-like elements.
 *
 * @author Owen Feehan
 * @param <T> element-type (an object-mask, or some class containing an object-mask).
 */
@AllArgsConstructor
public class DecodeLabels<T> {

    /** The label used for background voxels i.e. voxels that exist in no object. */
    private static final int BACKGROUND_LABEL = 0;

    /**
     * Creates an element from the scaled-object and its index
     *
     * @author Owen Feehan
     * @param <T> element-type (an object-mask, or some class containing an object-mask).
     */
    @FunctionalInterface
    public interface CreateElementFromScaledObject<T> {
        T createFrom(int index, ObjectMask scaledObject);
    }

    /**
     * Voxels each labelled with an integer (sequentially increasing from 1) to represent an object
     */
    private Voxels<?> voxels;

    /** Minimum label-value inclusive */
    private int minLabelInclusive;

    /** Maximum label-value inclusive */
    private int maxLabelInclusive;

    /** Create scaled element from a scaled-object and corresponding index. */
    private CreateElementFromScaledObject<T> createScaledElement;

    /**
     * Creates a map of elements to other elements that are labelled with unique integers
     * (sequentially increasing) in voxels.
     *
     * @param labelMap a map from a label to what becomes the key in the output map (each label
     *     should map to a unique key)
     * @param operationAfterScaling an operation to apply after labelling, but before the element is
     *     placed in the map
     * @return a map constructed key from {@code labelMap} and value as the element is derived from
     *     the label - for each label
     * @throws CreateException
     */
    public Map<T, T> create(Map<Integer, T> labelMap, UnaryOperator<T> operationAfterScaling)
            throws CreateException {

        /** an object for every label (as there is no minimum volume) */
        return new MapForLabelsCreator<>(
                        labelMap, create(0), operationAfterScaling, minLabelInclusive)
                .createMapForLabels();
    }

    /**
     * Creates a list of elements from voxels that are labelled with unique integers (sequentially
     * increasing)
     *
     * @param smallVolumeThreshold minimum volume of bounding-box otherwise a label is ignored
     * @return a list of respective elements, one for each label
     * @throws CreateException if bounding-boxes cannot be derived
     */
    public List<T> create(int smallVolumeThreshold) throws CreateException {
        try {
            return FunctionalList.filterAndMapWithIndexToList(
                    deriveBoundingBoxes(),
                    box -> box.extent().volumeXY() >= smallVolumeThreshold,
                    this::elementForIndex);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    private T elementForIndex(BoundingBox box, int index) {
        // The labels begin at 1
        return createScaledElement.createFrom(
                index, voxels.extract().voxelsEqualTo(index + 1).deriveObject(box));
    }

    /**
     * Derives bounding-boxes to minimally fit all voxels that match a label (for each label in a
     * sequence)
     *
     * @return a list of respective bounding-boxes, one for each label
     * @throws OperationFailedException if an {@link IndexOutOfBoundsException} occurs that suggests
     *     incorrect {@code minLabelInclusive} / {@code maxLabelInclusive} parameters were passed
     */
    private List<BoundingBox> deriveBoundingBoxes() throws OperationFailedException {

        List<PointRange> list =
                IntStream.rangeClosed(minLabelInclusive, maxLabelInclusive)
                        .mapToObj(index -> new PointRange())
                        .collect(Collectors.toList());

        IterateVoxelsAll.withVoxelBuffer(
                voxels,
                (point, buffer, offset) -> {
                    int label = buffer.getInt(offset);

                    if (label != BACKGROUND_LABEL) {
                        try {
                            list.get(label - 1).add(point);
                        } catch (IndexOutOfBoundsException e) {
                            throw new OperationFailedException(
                                    "An invalid index occurred, which is probably a result of setting incorrect min and max labels as parameters",
                                    e);
                        }
                    }
                });

        /** Convert to bounding-boxes after filtering any empty point-ranges */
        return FunctionalList.filterAndMapToList(
                list, pointRange -> !pointRange.isEmpty(), PointRange::deriveBoundingBoxNoCheck);
    }
}
