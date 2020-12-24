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

package org.anchoranalysis.mpp.segment.bean.kernel.proposer;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.segment.bean.kernel.Kernel;
import org.anchoranalysis.mpp.segment.kernel.KernelCalculationContext;
import org.anchoranalysis.mpp.segment.kernel.proposer.KernelWithIdentifier;
import org.anchoranalysis.mpp.segment.kernel.proposer.WeightedKernel;
import org.anchoranalysis.mpp.segment.kernel.proposer.WeightedKernelList;

public class KernelProposer<T> extends AnchorBean<KernelProposer<T>> {

    // START BEAN
    @BeanField @Getter @Setter private List<KernelProposerOption<T>> optionList = new ArrayList<>();

    @BeanField @Getter @Setter private Kernel<T> initialKernel;
    // END BEAN

    private double[] cumalativeProbability;
    private ArrayList<WeightedKernel<T>> kernels = null;

    public void init() throws InitException {
        calculateCumulativeProbability(optionList);

        // Ensure no two kernel factories have the same name
        EnsureUniqueNames.apply(kernels);
    }

    public void initWithProposerSharedObjects(MPPInitParams so, Logger logger)
            throws InitException {

        for (WeightedKernel<T> weightedKernel : kernels) {
            weightedKernel.getKernel().initRecursive(so, logger);
        }
    }

    public void initBeforeCalc(KernelCalculationContext context) throws InitException {
        for (WeightedKernel<T> weightedKernel : kernels) {
            weightedKernel.getKernel().initBeforeCalc(context);
        }
    }

    public KernelWithIdentifier<T> initialKernel() {
        return new KernelWithIdentifier<>(kernels.get(0).getKernel(), 0);
    }

    // Proposes a kernel
    public KernelWithIdentifier<T> proposeKernel(RandomNumberGenerator randomNumberGenerator) {
        return proposeKernel(randomNumberGenerator.sampleDoubleZeroAndOne());
    }

    public int getNumberKernels() {
        assert kernels != null;
        return kernels.size();
    }

    public WeightedKernelList<T> getAllKernelFactories() {

        WeightedKernelList<T> listOut = new WeightedKernelList<>();
        for (int i = 0; i < getNumberKernels(); i++) {
            WeightedKernel<T> wkf = kernels.get(i);
            listOut.add(wkf);
        }
        return listOut;
    }

    public String[] createKernelFactoryNames() {

        String[] namesOut = new String[getNumberKernels()];
        for (int i = 0; i < getNumberKernels(); i++) {
            namesOut[i] = kernels.get(i).getKernel().getBeanName();
        }
        return namesOut;
    }

    public WeightedKernel<T> getWeightedKernelFactory(int i) {
        return this.kernels.get(i);
    }

    // View of the kernel proposer
    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();

        sb.append("{size=");
        sb.append(getNumberKernels());
        sb.append(newLine);
        for (int i = 0; i < getNumberKernels(); i++) {
            sb.append(String.format("%d: %s%n", i, getWeightedKernelFactory(i).toString()));
        }
        sb.append("}");

        return sb.toString();
    }

    public void checkCompatibleWith(Mark testMark) {

        for (WeightedKernel<T> weightedKernel : kernels) {
            if (!weightedKernel.getKernel().isCompatibleWith(testMark)) {
                throw new UnsupportedOperationException(
                        String.format(
                                "Kernel %s is not compatible with templateMark",
                                weightedKernel.getName()));
            }
        }
    }

    private void calculateCumulativeProbability(List<KernelProposerOption<T>> options)
            throws InitException {

        if (options.isEmpty()) {
            throw new InitException("At least one option must be specified");
        }

        kernels = new ArrayList<>();

        // We add our initial kernel to the list, but weight it with 0, so it cannot
        // ordinarily be chosen
        // THIS MUST BE THE FIRST ITEM OF THE LIST, so we can pick from it later
        //   see proposeKernel
        kernels.add(new WeightedKernel<>(initialKernel, 0.0));

        // First we get a sum of all prob for normalization
        // and we population the lst kernel factories
        double total = 0;
        for (KernelProposerOption<T> opt : options) {
            total += opt.addWeightedKernelFactories(kernels);
        }

        if (total == 0) {
            throw new InitException("The total weights of the kernel-factories must be > 0");
        }

        // We a derived array with the cumulative probabilities
        cumalativeProbability = new double[kernels.size()];
        double running = 0;
        for (int i = 0; i < kernels.size(); i++) {
            running += (kernels.get(i).getWeight() / total);
            cumalativeProbability[i] = running;
        }
    }

    // Proposes a kernel
    private KernelWithIdentifier<T> proposeKernel(double randomValueBetweenZeroAndOne) {

        for (int i = 0; i < getNumberKernels(); i++) {

            WeightedKernel<T> wkf = getWeightedKernelFactory(i);

            if (randomValueBetweenZeroAndOne < cumalativeProbability[i]) {
                return new KernelWithIdentifier<>(wkf.getKernel(), i);
            }
        }

        throw new AnchorImpossibleSituationException();
    }
}
