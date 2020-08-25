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

package org.anchoranalysis.mpp.segment.kernel;

import org.anchoranalysis.anchor.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.mpp.proposer.error.ErrorNode;

public class KernelCalculationContext {

    private EnergySchemeWithSharedFeatures energyScheme;

    private ProposerContext proposerContext;
    private MarkFactoryContext markFactoryContext;

    public KernelCalculationContext(
            MarkFactoryContext markFactoryContext,
            EnergyStack energyStack,
            EnergySchemeWithSharedFeatures energyScheme,
            RandomNumberGenerator randomNumberGenerator,
            ErrorNode errorNode) {
        this.energyScheme = energyScheme;
        this.markFactoryContext = markFactoryContext;

        this.proposerContext =
                new ProposerContext(
                        randomNumberGenerator, energyStack, energyScheme.getRegionMap(), errorNode);
    }

    public ProposerContext proposer() {
        return proposerContext;
    }

    public EnergySchemeWithSharedFeatures getEnergyScheme() {
        return energyScheme;
    }

    public KernelCalculationContext replaceError(ErrorNode errorNode) {
        return new KernelCalculationContext(
                markFactoryContext,
                proposerContext.getEnergyStack(),
                energyScheme,
                proposerContext.getRandomNumberGenerator(),
                errorNode);
    }

    public MarkWithIdentifierFactory getMarkFactory() {
        return markFactoryContext.getMarkFactory();
    }
}
