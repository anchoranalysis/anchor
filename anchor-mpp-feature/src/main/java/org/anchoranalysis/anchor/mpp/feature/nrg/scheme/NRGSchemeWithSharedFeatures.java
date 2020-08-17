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

package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaNRGElemPair;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.nrg.NRGTotal;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

public class NRGSchemeWithSharedFeatures {

    private NRGScheme nrgScheme;
    private SharedFeatureMulti sharedFeatures;

    private CalculateIndividualTotalOperation calculateTotalIndividual;
    private Logger logger;

    // Caches NRG value by index
    private class CalculateIndividualTotalOperation
            implements CheckedFunction<Integer, NRGTotal, NamedFeatureCalculateException> {

        private VoxelizedMarkMemo pmm;
        private NRGStack raster;
        private KeyValueParams kvp;

        public void update(VoxelizedMarkMemo pmm, NRGStack raster) throws OperationFailedException {
            this.pmm = pmm;
            this.raster = raster;

            KeyValueParamsForImageCreator creator =
                    new KeyValueParamsForImageCreator(nrgScheme, sharedFeatures, logger);
            try {
                this.kvp = creator.createParamsForImage(raster);
            } catch (CreateException e) {
                throw new OperationFailedException(e);
            }
        }

        @Override
        public NRGTotal apply(Integer index) throws NamedFeatureCalculateException {
            return calc();
        }

        public NRGTotal calc() throws NamedFeatureCalculateException {
            try {
                FeatureCalculatorMulti<FeatureInputSingleMemo> session =
                        FeatureSession.with(
                                nrgScheme.getElemIndAsFeatureList(),
                                new FeatureInitParams(kvp),
                                sharedFeatures,
                                logger);

                FeatureInputSingleMemo params =
                        new FeatureInputSingleMemo(pmm, new NRGStackWithParams(raster, kvp));

                return new NRGTotal(session.calculate(params).total());
            } catch (InitException e) {
                throw new NamedFeatureCalculateException(e);
            }
        }
    }

    public NRGSchemeWithSharedFeatures(
            NRGScheme nrgScheme, SharedFeatureMulti sharedFeatures, Logger logger) {
        super();
        this.nrgScheme = nrgScheme;
        this.sharedFeatures = sharedFeatures;
        this.logger = logger;

        calculateTotalIndividual = new CalculateIndividualTotalOperation();
    }

    public NRGTotal totalAll(MemoCollection pxlMarkMemoList, NRGStack raster)
            throws NamedFeatureCalculateException {

        try {
            NRGStackWithParams nrgStack = createNRGStack(raster);

            FeatureCalculatorMulti<FeatureInputAllMemo> session =
                    FeatureSession.with(
                            nrgScheme.getElemAllAsFeatureList(),
                            new FeatureInitParams(nrgStack.getParams()),
                            sharedFeatures,
                            logger);

            FeatureInputAllMemo params = new FeatureInputAllMemo(pxlMarkMemoList, nrgStack);

            return new NRGTotal(session.calculate(params).total());

        } catch (InitException | FeatureCalculationException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    public NRGTotal totalIndividual(VoxelizedMarkMemo pmm, NRGStack raster)
            throws NamedFeatureCalculateException {
        try {
            calculateTotalIndividual.update(pmm, raster);
            return calculateTotalIndividual.calc();
        } catch (OperationFailedException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    private NRGStackWithParams createNRGStack(NRGStack raster) throws FeatureCalculationException {

        KeyValueParams kvp;
        try {
            kvp = nrgScheme.createKeyValueParams();
        } catch (CreateException e) {
            throw new FeatureCalculationException(e);
        }

        return new NRGStackWithParams(raster, kvp);
    }

    public NRGScheme getNrgScheme() {
        return nrgScheme;
    }

    public AddCriteriaNRGElemPair createAddCriteria() throws CreateException {
        try {
            return new AddCriteriaNRGElemPair(
                    getNrgScheme().getElemPairAsFeatureList(),
                    (AddCriteriaPair) getNrgScheme().getPairAddCriteria().duplicateBean());
        } catch (InitException | BeanDuplicateException e) {
            throw new CreateException(e);
        }
    }

    public SharedFeatureMulti getSharedFeatures() {
        return sharedFeatures;
    }

    public RegionMap getRegionMap() {
        return nrgScheme.getRegionMap();
    }
}
