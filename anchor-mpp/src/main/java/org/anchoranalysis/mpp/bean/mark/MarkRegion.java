/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean.mark;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.GenerateUniqueParameterization;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * An abstract base class for defining regions associated with marks.
 *
 * <p>This class extends AnchorBean and implements GenerateUniqueParameterization, providing a
 * foundation for creating mark regions in the MPP (Marked Point Process) framework.
 */
public abstract class MarkRegion extends AnchorBean<MarkRegion>
        implements GenerateUniqueParameterization {

    /**
     * Creates voxel statistics for the mark region based on the given memo and dimensions.
     *
     * @param memo a memoized representation of a voxelized mark
     * @param dimensions the dimensions of the space in which the mark exists
     * @return VoxelStatistics object containing statistical information about the mark region
     * @throws CreateException if there's an error creating the statistics
     */
    public abstract VoxelStatistics createStatisticsFor(
            VoxelizedMarkMemo memo, Dimensions dimensions) throws CreateException;
}
