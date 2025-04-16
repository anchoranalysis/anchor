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

package org.anchoranalysis.mpp.mark.voxelized.memo;

import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.mark.Mark;

/**
 * Factory class for creating {@link VoxelizedMarkMemo} instances.
 * <p>
 * This class explicitly manages the creation of VoxelizedMarkMemo objects as they can consume
 * a large amount of memory. It provides a static method for creating these objects, ensuring
 * controlled instantiation.
 * </p>
 */
public class VoxelizedMarkMemoFactory {

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is designed to be used via its static method only.
     * </p>
     */
    private VoxelizedMarkMemoFactory() {
        // ONLY ALLOWED AS STATIC
    }

    /**
     * Creates a new {@link VoxelizedMarkMemo} instance.
     *
     * @param mark the mark to be voxelized
     * @param stack the energy stack without parameters
     * @param regionMap the region map
     * @return a new {@link VoxelizedMarkMemo} instance
     */
    public static VoxelizedMarkMemo create(
            Mark mark, EnergyStackWithoutParameters stack, RegionMap regionMap) {
        return new VoxelizedMarkMemo(mark, stack, regionMap);
    }
}