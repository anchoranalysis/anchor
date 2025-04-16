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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.mpp.feature.input.FeatureInputPairMemo;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * Criteria for adding edges between marks in a graph-based model.
 *
 * <p>This interface extends {@link OrderedFeatureList} to provide an ordered list of features for
 * pair-wise mark comparisons.
 *
 * @param <T> the type of the generated edge
 */
public interface AddCriteria<T> extends OrderedFeatureList<FeatureInputPairMemo> {

    /**
     * Generates an edge between two marks based on the defined criteria.
     *
     * @param mark1 the first {@link VoxelizedMarkMemo}
     * @param mark2 the second {@link VoxelizedMarkMemo}
     * @param energyStack the {@link EnergyStack} containing image data and parameters
     * @param session an optional {@link FeatureCalculatorMulti} for feature calculations
     * @param do3D whether to perform 3D calculations
     * @return an {@link Optional} containing the generated edge of type T if criteria are met,
     *     otherwise empty
     * @throws CreateException if there's an error during edge generation
     */
    Optional<T> generateEdge(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            EnergyStack energyStack,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws CreateException;
}
