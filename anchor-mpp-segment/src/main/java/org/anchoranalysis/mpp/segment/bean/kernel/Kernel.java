/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.bean.kernel;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.MPPBean;
import org.anchoranalysis.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.mpp.segment.kernel.KernelCalculateEnergyException;
import org.anchoranalysis.mpp.segment.kernel.KernelCalculationContext;

/**
 * Modifies an entity by applying a kernel.
 *
 * @author Owen Feehan
 * @param <T> the type of entity that is modified
 * @param <S> updatable-state
 */
public abstract class Kernel<T,S> extends MPPBean<Kernel<T,S>> implements CompatibleWithMark {

    // START BEAN PROPERTIES
    @BeanField @AllowEmpty @Getter @Setter
    // This is only decorative. Currently it has no use.
    private String name = "";
    // END BEAN PROPERTIES

    // Call ONCE before calculating anything
    public abstract void initBeforeCalc(KernelCalculationContext context) throws InitException;

    /**
     * Calculates the Energy for a proposal
     *
     * @param existing the existing Energy
     * @param context
     * @return a proposal, or empty() if there is no proposal to make
     * @throws KernelCalculateEnergyException
     */
    public abstract Optional<T> makeProposal(Optional<T> existing, KernelCalculationContext context)
            throws KernelCalculateEnergyException;

    public abstract double calculateAcceptanceProbability(
            int existingSize,
            int proposalSize,
            double poissonIntens,
            Dimensions dimensions,
            double densityRatio);

    public abstract String describeLast();

    /**
     * If the kernel is accepted, makes the necessary changes to a ListUpdatableMarkSetCollection
     *
     * @param updatableState where to make the changes
     * @param energyExisting existing energy
     * @param energyNew accepted energy
     * @throws UpdateMarkSetException
     */
    public abstract void updateAfterAcceptance(
            S updatableState,
            T energyExisting,
            T energyNew)
            throws UpdateMarkSetException;

    /**
     * The mark ids that were changed in the last energy calculation for the kernel
     *
     * <p>Guaranteed only to be called, if energy calculation did not return null
     *
     * @return an array of mark IDs
     */
    public abstract int[] changedMarkIDArray();

    /**
     * Called every time a proposal is accepted, so a kernel can potentially keep track of the state
     * of the current image
     *
     * @param state current-state (after being accepted)
     */
    public abstract void informLatestState(T state);
}
