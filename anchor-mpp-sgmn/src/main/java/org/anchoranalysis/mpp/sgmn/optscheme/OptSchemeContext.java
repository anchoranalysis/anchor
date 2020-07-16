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
