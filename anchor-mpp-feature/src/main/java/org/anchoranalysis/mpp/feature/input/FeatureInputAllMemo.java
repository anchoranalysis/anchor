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
import org.anchoranalysis.mpp.feature.mark.EnergyMemoList;

/**
 * Feature input that contains a list of memoized energy calculations for all marks.
 *
 * <p>This class extends {@link FeatureInputEnergy} to include an {@link EnergyMemoList}, which
 * represents memoized energy calculations for all marks in the model.
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputAllMemo extends FeatureInputEnergy {

    /** The list of memoized energy calculations for all marks. */
    @Getter @Setter private EnergyMemoList calculations;

    /**
     * Creates a new instance with a list of memoized energy calculations and an energy stack.
     *
     * @param pxlMarkMemoList the list of memoized energy calculations for all marks
     * @param raster the energy stack associated with the calculations
     */
    public FeatureInputAllMemo(EnergyMemoList pxlMarkMemoList, EnergyStack raster) {
        super(Optional.of(raster));
        this.calculations = pxlMarkMemoList;
    }
}
