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

package org.anchoranalysis.mpp.feature.input;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * Feature input that contains a single memoized voxelized mark and an optional energy stack.
 *
 * <p>This class extends {@link FeatureInputEnergy} to include a {@link VoxelizedMarkMemo} object,
 * representing a single memoized voxelized mark for feature calculations.</p>
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputSingleMemo extends FeatureInputEnergy {

    /** The memoized voxelized mark. */
    @Getter @Setter private VoxelizedMarkMemo memo;

    /**
     * Creates a new instance with a memoized voxelized mark and a non-optional energy stack.
     *
     * @param pxlPartMemo the memoized voxelized mark
     * @param energyStack the energy stack associated with the mark
     */
    public FeatureInputSingleMemo(VoxelizedMarkMemo pxlPartMemo, EnergyStack energyStack) {
        this(pxlPartMemo, Optional.of(energyStack));
    }

    /**
     * Creates a new instance with a memoized voxelized mark and an optional energy stack.
     *
     * @param pxlPartMemo the memoized voxelized mark
     * @param energyStack an optional energy stack associated with the mark
     */
    public FeatureInputSingleMemo(
            VoxelizedMarkMemo pxlPartMemo, Optional<EnergyStack> energyStack) {
        super(energyStack);
        this.memo = pxlPartMemo;
    }
}