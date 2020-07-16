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
/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNodeNull;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.optscheme.termination.TriggerTerminationCondition;
import org.anchoranalysis.mpp.sgmn.kernel.CfgGenContext;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;

@AllArgsConstructor
public class OptSchemeContext {

    @Getter private final String experimentDescription;

    @Getter private final NRGSchemeWithSharedFeatures nrgScheme;

    @Getter private final DualStack dualStack;

    @Getter private final TriggerTerminationCondition triggerTerminationCondition;

    private final BoundIOContext context;

    private final RandomNumberGenerator randomNumberGenerator;

    @Getter private final CfgGen cfgGen;

    public CfgGenContext cfgGenContext() {
        return new CfgGenContext(cfgGen);
    }

    public KernelCalcContext calcContext(CfgGenContext cfgGenContext) {
        return new KernelCalcContext(
                cfgGenContext,
                dualStack.getNrgStack(),
                nrgScheme,
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
