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
package org.anchoranalysis.image.bean.spatial.arrange.align;

import java.util.function.ToIntFunction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.bean.nonbean.spatial.align.AlignmentOnDimension;
import org.anchoranalysis.image.bean.nonbean.spatial.align.PositionChoicesConstants;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Align the bounding-box to the {@code larger} without resizing.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class Align extends BoxAligner {

    // START BEAN PROPERTIES
    /**
     * Indicates how to align the image across the <b>X-axis</b> (i.e. horizontally): one of {@code
     * left, right, center}.
     */
    @BeanField @Getter @Setter private String alignX = PositionChoicesConstants.CENTER;

    /**
     * Indicates how to align the image across the <b>Y-axis</b> (i.e. vertically): one of {@code
     * top, bottom, center}.
     */
    @BeanField @Getter @Setter private String alignY = PositionChoicesConstants.CENTER;

    /**
     * Indicates how to align the image across the <b>Z-axis</b>: one of {@code top, bottom, center,
     * repeat}.
     *
     * <p>{@code repeat} is a special-case where a single z-slice overlay will be duplicated across
     * the z-dimension of the stack onto which it is overlayed.
     */
    @BeanField @Getter @Setter private String alignZ = PositionChoicesConstants.CENTER;
    // END BEAN PROPERTIES

    // START: enums derived from the text in the respective field, to indicate how to do alignment
    // for that dimension.
    private AlignmentOnDimension alignXEnum;
    private AlignmentOnDimension alignYEnum;
    private AlignmentOnDimension alignZEnum;
    // END: enums derived.

    /**
     * Creates with alignment text for each axis.
     *
     * @param alignX indicates how to align the image across the <b>X-axis</b>: one of {@code top,
     *     bottom, center}.
     * @param alignY indicates how to align the image across the <b>Y-axis</b> (i.e. vertically):
     *     one of {@code top, bottom, center}.
     * @param alignZ indicates how to align the image across the <b>Z-axis</b>: one of {@code top,
     *     bottom, center, repeat}. See {@code alignZ}.
     */
    public Align(String alignX, String alignY, String alignZ) {
        // We ignore the text values associated with the bean
        this.alignX = alignX;
        this.alignY = alignY;
        this.alignZ = alignZ;
    }

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        alignXEnum = PositionChoicesConstants.alignX(alignX);
        alignYEnum = PositionChoicesConstants.alignY(alignY);
        alignZEnum = PositionChoicesConstants.alignZ(alignZ);
    }

    @Override
    public BoundingBox alignAfterCheck(BoundingBox smaller, BoundingBox larger) {
        ReadableTuple3i cornerLarger = larger.cornerMin();
        Point3i cornerAligned = alignCorner(smaller, larger.extent());
        cornerAligned.add(cornerLarger);
        return BoundingBox.createReuse(cornerAligned, smaller.extent());
    }

    @Override
    public BoundingBox alignAfterCheck(Extent smaller, Extent larger)
            throws OperationFailedException {
        Point3i cornerAligned = alignCorner(smaller, larger);
        return BoundingBox.createReuse(cornerAligned, smaller);
    }

    @Override
    public BoundingBox alignAfterCheck(Extent smaller, BoundingBox larger)
            throws OperationFailedException {
        ReadableTuple3i cornerLarger = larger.cornerMin();
        Point3i cornerAligned = alignCorner(smaller, larger.extent());
        cornerAligned.add(cornerLarger);
        return BoundingBox.createReuse(cornerAligned, smaller);
    }

    /**
     * The minimum corner at which the entity should be locate after alignment, considering all of
     * {@code larger}.
     *
     * @param smaller the smaller size to align with {@code larger}.
     * @param larger the larger size to align against as a reference.
     * @return the minimum corner to use for the aligned box, newly-created.
     * @throws OperationFailedException if an unrecognised string is used for one of {@code alignX},
     *     {@code alignY} or {@code alignZ}.
     */
    private Point3i alignCorner(Extent smaller, Extent larger) throws OperationFailedException {
        return new Point3i(
                position(alignXEnum, Extent::x, smaller, 0, larger),
                position(alignYEnum, Extent::y, smaller, 0, larger),
                position(alignZEnum, Extent::z, smaller, 0, larger));
    }

    /**
     * The minimum corner at which the entity should be locate after alignment, ignoring the space
     * to the left.
     *
     * @param smaller the smaller size to align with {@code larger}.
     * @param larger the larger size to align against as a reference.
     * @return the minimum corner to use for the aligned box, newly-created.
     */
    public Point3i alignCorner(BoundingBox smaller, Extent larger) {
        return new Point3i(
                position(alignXEnum, Extent::x, smaller.extent(), smaller.cornerMin().x(), larger),
                position(alignYEnum, Extent::y, smaller.extent(), smaller.cornerMin().y(), larger),
                position(alignZEnum, Extent::z, smaller.extent(), smaller.cornerMin().z(), larger));
    }

    /**
     * Calculates the position on a particular axis.
     *
     * @param alignment how to do the alignment for this particular dimension.
     * @param extractValue extracts a value on the particular axis from a {@link Extent}.
     * @param larger the total enclosing size to align with, apart from what is being {@code
     *     disconsideredLeft}.
     * @param smaller the size of the <b>smaller</b> entity (what is being aligned).
     * @param disconsideredLeft the first of this amount in {@code larger} is not considered for
     *     alignment. Alignment only occurs with the remaining space.
     * @return the minimum corner on the particular axis to locate the overlay.
     */
    private static int position(
            AlignmentOnDimension alignment,
            ToIntFunction<Extent> extractValue,
            Extent smaller,
            int disconsideredLeft,
            Extent larger) {
        return alignment.align(
                extractValue.applyAsInt(larger),
                extractValue.applyAsInt(smaller),
                disconsideredLeft);
    }
}
