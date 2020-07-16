/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel;

import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class KernelCalcContext {

    private NRGSchemeWithSharedFeatures nrgScheme;

    private ProposerContext proposerContext;
    private CfgGenContext cfgGenContext;

    public KernelCalcContext(
            CfgGenContext cfgGenContext,
            NRGStackWithParams nrgStack,
            NRGSchemeWithSharedFeatures nrgScheme,
            RandomNumberGenerator randomNumberGenerator,
            ErrorNode errorNode) {
        this.nrgScheme = nrgScheme;
        this.cfgGenContext = cfgGenContext;

        this.proposerContext =
                new ProposerContext(
                        randomNumberGenerator, nrgStack, nrgScheme.getRegionMap(), errorNode);
    }

    public ProposerContext proposer() {
        return proposerContext;
    }

    public NRGSchemeWithSharedFeatures getNrgScheme() {
        return nrgScheme;
    }

    public KernelCalcContext replaceError(ErrorNode errorNode) {
        return new KernelCalcContext(
                cfgGenContext,
                proposerContext.getNrgStack(),
                nrgScheme,
                proposerContext.getRandomNumberGenerator(),
                errorNode);
    }

    public CfgGenContext cfgGen() {
        return cfgGenContext;
    }
}
