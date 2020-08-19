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

package org.anchoranalysis.image.object;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.points.PointRange;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

@AllArgsConstructor
public class CreateObjectsFromLabels {

    private static final int BACKGROUND_COLOR = 0;

    /**
     * Voxels each labelled with an integer (sequentially increasing from 1) to represent an object
     */
    private Voxels<?> voxels;

    /** Minimum label-value inclusive */
    private int minLabelInclusive;

    /** Maximum label-value inclusive */
    private int maxLabelInclusive;

    /**
     * Creates a map of objects to other objects that are labelled with unique integers
     * (sequentially increasing) in voxels
     *
     * @param labelMap a map from a label to what becomes the key in the output map (each label
     *     should map to a unique key)
     * @param operationAfterScaling an operation to apply after labelling, but before the object is
     *     placed in the map
     * @return a map constructed key from {@code labelMap} and value as the object-derived from the
     *     label - for each label
     * @throws CreateException
     */
    public Map<ObjectMask, ObjectMask> create(
            Map<Integer, ObjectMask> labelMap, UnaryOperator<ObjectMask> operationAfterScaling)
            throws CreateException {

        /** an object for every label (as there is no minimum volume) */
        return new MapForLabelsCreator(
                        labelMap, create(0), operationAfterScaling, minLabelInclusive)
                .createMapForLabels();
    }

    /**
     * Creates an object-collection from voxels that are labelled with unique integers (sequentially
     * increasing)
     *
     * @param minBoundingBoxVolume minimum volume of bounding-box otherwise a label is ignored
     * @return a list of respective object-masks, one for each label
     * @throws CreateException if bounding-boxes cannot be derived
     */
    public ObjectCollection create(int smallVolumeThreshold) throws CreateException {
        try {
            return ObjectCollectionFactory.filterAndMapWithIndexFrom(
                    deriveBoundingBoxes(),
                    box -> box.extent().volumeXY() >= smallVolumeThreshold,
                    (box, index) ->
                            voxels.extract()
                                    .voxelsEqualTo(index + 1)
                                    .deriveObject(box) // The labels begin at 1
                    );
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
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

        for (int z = 0; z < voxels.slices().extent().z(); z++) {

            VoxelBuffer<?> buffer = voxels.slice(z);

            int offset = 0;

            for (int y = 0; y < voxels.slices().extent().y(); y++) {
                for (int x = 0; x < voxels.slices().extent().x(); x++) {

                    int col = buffer.getInt(offset++);

                    if (col != BACKGROUND_COLOR) {
                        try {
                            list.get(col - 1).add(x, y, z);
                        } catch (IndexOutOfBoundsException e) {
                            throw new OperationFailedException(
                                    "An invalid index occurred, which is probably a result of setting incorrect min and max labels as parameters",
                                    e);
                        }
                    }
                }
            }
        }

        /** Convert to bounding-boxes after filtering any empty point-ranges */
        return list.stream()
                .filter(pointRange -> !pointRange.isEmpty())
                .map(PointRange::deriveBoundingBoxNoCheck)
                .collect(Collectors.toList());
    }
}
