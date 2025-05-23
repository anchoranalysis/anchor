/*-
 * #%L
 * anchor-image-feature
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

package org.anchoranalysis.image.feature.bean;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Calculates a per-voxel score.
 *
 * <p>This abstract class provides a framework for calculating scores on a per-voxel basis.
 * Implementations should define the specific scoring algorithm in the {@link #calculate} method.
 */
public abstract class VoxelScore extends ImageBean<VoxelScore> {

    /**
     * Initializes the voxels-score.
     *
     * <p>Must be called before {@link #calculate}.
     *
     * @param histograms one or more histograms associated with this calculation
     * @param dictionary optional {@link Dictionary} associated with this calculation
     * @throws InitializeException if anything goes wrong
     */
    public void initialize(List<Histogram> histograms, Optional<Dictionary> dictionary)
            throws InitializeException {
        // TO be overridden if needed
    }

    /**
     * Calculates a score for a set of voxel intensities.
     *
     * @param voxelIntensities an array of voxel intensity values
     * @return the calculated score for the given voxel intensities
     * @throws FeatureCalculationException if the calculation fails
     */
    public abstract double calculate(int[] voxelIntensities) throws FeatureCalculationException;
}
