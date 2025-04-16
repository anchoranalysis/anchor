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
import lombok.Value;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * Feature input that contains a pair of memoized voxelized marks and an energy stack.
 *
 * <p>This class extends {@link FeatureInputEnergy} to include two {@link VoxelizedMarkMemo} objects,
 * representing a pair of memoized voxelized marks for feature calculations.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class FeatureInputPairMemo extends FeatureInputEnergy {

    /** The first memoized voxelized mark. */
    private VoxelizedMarkMemo object1;

    /** The second memoized voxelized mark. */
    private VoxelizedMarkMemo object2;

    /**
     * Creates a new instance with two memoized voxelized marks and an energy stack.
     *
     * @param object1 the first memoized voxelized mark
     * @param object2 the second memoized voxelized mark
     * @param energyStack the energy stack associated with the marks
     */
    public FeatureInputPairMemo(
            VoxelizedMarkMemo object1, VoxelizedMarkMemo object2, EnergyStack energyStack) {
        super(Optional.of(energyStack));
        this.object1 = object1;
        this.object2 = object2;
    }
}