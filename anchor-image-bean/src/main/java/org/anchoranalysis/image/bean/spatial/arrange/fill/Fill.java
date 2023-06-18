/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.initializable.CheckMisconfigured;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.BoundingBoxEnclosed;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Montages {@link Extent}s together to completely fill the available space, while preserving aspect
 * ratio.
 *
 * <p>Inspired by this <a href="https://gist.github.com/JesseCrocker/cfd05006335c2c828a2b">Python
 * implementation by Jesse Crocker</a>, which is in turn apparently inspired originally from {@code
 * https://www.crispymtn.com/stories/the-algorithm-for-a-perfectly-balanced-photo-gallery
 * collage.py}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class Fill extends StackArranger {

    // START BEAN PROPERTIES
    /**
     * The number of rows to use the montage, when sufficient images are available.
     *
     * <p>If fewer {@link Extent}s are passed, then a row is created for each {@link Extent}, so
     * much as possible.
     */
    @BeanField @Getter @Setter private int numberRows = 1;

    /**
     * When true the number of {@link Extent}s per row is allowed vary, to sensibly fill space.
     *
     * <p>When false, the total number of {@link Extent}s per row is kept uniform, apart from the
     * final occupied row (if there are insufficient images to fully populate it).
     *
     * <p>In both cases, the sizes of the images are allowed to vary, to maximally fill the
     * available space.
     */
    @BeanField @Getter @Setter private boolean varyNumberImagesPerRow = true;

    /**
     * The width of the combined image in pixels.
     *
     * <p>When 0, this is disabled.
     *
     * <p>At least one of both {@code width} and {@code widthRatio} must be enabled. If both are,
     * the minimum width is used.
     */
    @BeanField @Getter @Setter @NonNegative private int width = 0;

    /**
     * What fraction of the natural width of the elements should be used to determine the final
     * width.
     *
     * <p>The natural width of the elements, is the average-width of an entire row after
     * partitioning.
     *
     * <p>When 0.0, this is disabled.
     *
     * <p>At least one of both {@code width} and {@code widthRatio} must be enabled. If both are,
     * the minimum width is used.
     */
    @BeanField @Getter @Setter @NonNegative private double widthRatio = 0.1;
    // END BEAN PROPERTIES

    /**
     * Create with a particular number of rows.
     *
     * @param rows the number of rows to use the montage, when sufficient images are available.
     */
    public Fill(int rows) {
        this.numberRows = rows;
    }

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        CheckMisconfigured.atLeastOne("width", "widthRatio", width > 0, widthRatio > 0.0);
    }

    @Override
    public StackArrangement arrangeStacks(Iterator<Extent> extents, OperationContext context)
            throws ArrangeStackException {
        List<ExtentToArrange> elements = createElements(extents);

        // In case there are fewer elements than rows, we change the number of rows
        int numberRowsMin = Math.min(numberRows, elements.size());

        try {
            // Partition the images, so that each row has a roughly similar sum of aspect-ratios
            List<List<ExtentToArrange>> partitions =
                    context.getExecutionTimeRecorder()
                            .recordExecutionTime(
                                    "Partition extents",
                                    () ->
                                            Partitioner.partitionExtents(
                                                    elements,
                                                    numberRowsMin,
                                                    varyNumberImagesPerRow,
                                                    true));

            Extent combinedSize = scaleToTargetWidth(partitions);
            // Create a bounding box for each element, using the ID to determine the position in the
            // array.
            BoundingBox[] boxes = derivingBoundingBoxes(elements.size(), partitions, combinedSize);

            List<BoundingBoxEnclosed> boxesEnclosed =
                    FunctionalList.mapToList(Arrays.stream(boxes), BoundingBoxEnclosed::new);

            return new StackArrangement(combinedSize, boxesEnclosed);

        } catch (OperationFailedException e) {
            throw new ArrangeStackException("An error occurred partitioning the elements", e);
        }
    }

    /** Scales the partitioned elements, to match the desired target-width. */
    private Extent scaleToTargetWidth(List<List<ExtentToArrange>> partitions) {
        int combinedWidth = CombinedWidthCalculator.calculate(partitions, width, widthRatio);
        return FitCombinedScaler.scaleToWidth(partitions, !varyNumberImagesPerRow, combinedWidth);
    }

    /** Creates a {@link ExtentToArrange} for each respective {@link Extent}. */
    private static List<ExtentToArrange> createElements(Iterator<Extent> extents) {
        int index = 0;
        List<ExtentToArrange> elements = new LinkedList<>();
        while (extents.hasNext()) {
            elements.add(new ExtentToArrange(index++, extents.next()));
        }
        return elements;
    }

    /**
     * Derive a {@link BoundingBox} for each {@link Extent}, corresponding to its location and size
     * in the combined size.
     *
     * @throws OperationFailedException if any bounding box lies outside the image.
     */
    @SuppressWarnings("null")
    private static BoundingBox[] derivingBoundingBoxes(
            int numberElements, List<List<ExtentToArrange>> partitions, Extent combinedSize)
            throws OperationFailedException {
        BoundingBox[] boxes = new BoundingBox[numberElements];

        Point3i cornerMin = new Point3i();
        for (List<ExtentToArrange> row : partitions) {
            cornerMin.setX(0);
            Extent extent = null;
            assert (!row.isEmpty());

            for (ExtentToArrange element : row) {
                extent = element.getExtent();

                if (!combinedSize.contains(cornerMin)) {
                    throw new OperationFailedException(
                            String.format(
                                    "Element %d with corner %s is not located inside the montaged image of size %s",
                                    element.getIndex(), cornerMin, combinedSize));
                }

                BoundingBox box =
                        BoundingBox.createDuplicate(cornerMin, extent).clampTo(combinedSize);
                boxes[element.getIndex()] = box;

                cornerMin.incrementX(extent.x());
            }
            cornerMin.incrementY(extent.y()); // NOSONAR
        }

        return boxes;
    }
}
