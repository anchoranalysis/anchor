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

package org.anchoranalysis.mpp.sgmn.optscheme;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.anchor.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.anchor.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNodeNull;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.optscheme.termination.TriggerTerminationCondition;
import org.anchoranalysis.mpp.sgmn.kernel.MarkFactoryContext;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalculationContext;

@AllArgsConstructor
public class OptSchemeContext {

    @Getter private final String experimentDescription;

    @Getter private final EnergySchemeWithSharedFeatures energyScheme;

    @Getter private final DualStack dualStack;

    @Getter private final TriggerTerminationCondition triggerTerminationCondition;

    private final BoundIOContext context;

    private final RandomNumberGenerator randomNumberGenerator;

    @Getter private final MarkWithIdentifierFactory markFactory;

    public MarkFactoryContext markFactoryContext() {
        return new MarkFactoryContext(markFactory);
    }

    public KernelCalculationContext calculateContext(MarkFactoryContext markFactoryContext) {
        return new KernelCalculationContext(
                markFactoryContext,
                dualStack.getEnergyStack(),
                energyScheme,
                randomNumberGenerator,
                ErrorNodeNull.instance());
    }

    public Logger getLogger() {
        return context.getLogger();
    }

    public BoundOutputManagerRouteErrors getOutputManager() {
        return context.getOutputManager();
    }

    public boolean isDebugEnabled() {
        return context.isDebugEnabled();
    }
}
