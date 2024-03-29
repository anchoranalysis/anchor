/*-
 * #%L
 * anchor-plugin-opencv
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
package org.anchoranalysis.mpp.mark;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Converts a confidence-associated {@link Mark} to an equivalent {@link ObjectMask}.
 *
 * <p>A {@link ScaleFactor} is also applied to scale the {@link Mark} before conversion.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class MarkToObjectConverter {

    /** The region of a {@link Mark} to extract from. */
    private static final RegionMembershipWithFlags DEFAULT_REGION_MEMBERSHIP =
            RegionMapSingleton.instance()
                    .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    /**
     * An optional scaling factor to apply to the mark, before converting to an {@link ObjectMask}.
     */
    private final Optional<ScaleFactor> scaleFactor;

    /**
     * The dimensions of the final scaled-up scene, to ensure the {@link ObjectMask} is contained
     * within.
     */
    private final Dimensions dimensions;

    /** The region-membership to use when convering the mark. */
    private final RegionMembershipWithFlags regionMembership;

    /** The binary-values to use in the created {@link ObjectMask}'s buffers. */
    private final BinaryValuesByte binaryValuesOut;

    /**
     * Create for particular {@link Dimensions} without a scaling-factor.
     *
     * @param dimensions the dimensions of the final scaled-up scene, to ensure the {@link
     *     ObjectMask} is contained within.
     */
    public MarkToObjectConverter(Dimensions dimensions) {
        this(
                Optional.empty(),
                dimensions,
                DEFAULT_REGION_MEMBERSHIP,
                BinaryValuesByte.getDefault());
    }

    /**
     * Create for particular {@link Dimensions} with a scaling-factor.
     *
     * @param scaleFactor the scaling-factor.
     * @param dimensions the dimensions of the final scaled-up scene, to ensure the {@link
     *     ObjectMask} is contained within.
     */
    public MarkToObjectConverter(Optional<ScaleFactor> scaleFactor, Dimensions dimensions) {
        this(scaleFactor, dimensions, DEFAULT_REGION_MEMBERSHIP, BinaryValuesByte.getDefault());
    }

    /**
     * Converts a {@link Mark} to an equivalent {@link ObjectMask}.
     *
     * @param mark the {@link Mark} to convert (whose state will be internally modified if a
     *     scale-factor is applied).
     * @return an {@link ObjectMask} with scaling applied.
     */
    public ObjectMask convert(Mark mark) {
        // Scale the marks up
        try {
            if (scaleFactor.isPresent() && !scaleFactor.get().isNoScale()) {
                // We duplicate so avoid changing the state in the original mark.
                mark.scale(scaleFactor.get());
            }
        } catch (CheckedUnsupportedOperationException e) {
            throw new AnchorImpossibleSituationException();
        }
        // Then derive an {@link ObjectMask} representation.
        return mark.deriveObject(dimensions, regionMembership, binaryValuesOut);
    }
}
