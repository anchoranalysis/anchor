/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.addcriteria;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.feature.input.FeatureInputPairMemo;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.pair.MarkPair;

/**
 * Abstract base class for criteria used to determine if a pair of marks should be included.
 *
 * <p>This class implements {@link AddCriteria} for {@link MarkPair}s of {@link Mark}s.</p>
 */
public abstract class AddCriteriaPair extends AnchorBean<AddCriteriaPair>
        implements AddCriteria<MarkPair<Mark>> {

    @Override
    public Optional<MarkPair<Mark>> generateEdge(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            EnergyStack energyStack,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws CreateException {

        try {
            if (includeMarks(mark1, mark2, energyStack.dimensions(), session, do3D)) {
                return Optional.of(new MarkPair<Mark>(mark1.getMark(), mark2.getMark()));
            }
        } catch (IncludeMarksFailureException e) {
            throw new CreateException(e);
        }

        return Optional.empty();
    }

    /**
     * Determines whether to include a pair of marks based on specific criteria.
     *
     * @param mark1 the first {@link VoxelizedMarkMemo}
     * @param mark2 the second {@link VoxelizedMarkMemo}
     * @param dimensions the {@link Dimensions} of the image
     * @param session an optional {@link FeatureCalculatorMulti} for feature calculations
     * @param do3D whether to perform 3D calculations
     * @return true if the marks should be included, false otherwise
     * @throws IncludeMarksFailureException if there's an error during the inclusion decision process
     */
    public abstract boolean includeMarks(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            Dimensions dimensions,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws IncludeMarksFailureException;

    @Override
    public String describeBean() {
        return getBeanName();
    }
}