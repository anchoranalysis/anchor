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

package org.anchoranalysis.mpp.feature.energy.scheme;

import lombok.Getter;
import org.anchoranalysis.bean.exception.BeanDuplicateException;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.shared.SharedFeatures;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.feature.addcriteria.AddCriteriaEnergyPair;
import org.anchoranalysis.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.mpp.feature.energy.EnergyTotal;
import org.anchoranalysis.mpp.feature.input.FeatureInputAllMemo;
import org.anchoranalysis.mpp.feature.input.FeatureInputSingleMemo;
import org.anchoranalysis.mpp.feature.mark.EnergyMemoList;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

public class EnergySchemeWithSharedFeatures {

    @Getter private EnergyScheme energyScheme;
    @Getter private SharedFeatures sharedFeatures;

    private CalculateIndividualTotalOperation calculateTotalIndividual;
    private Logger logger;

    // Caches energy value by index
    private class CalculateIndividualTotalOperation
            implements CheckedFunction<Integer, EnergyTotal, NamedFeatureCalculateException> {

        private VoxelizedMarkMemo mark;
        private EnergyStackWithoutParameters raster;
        private Dictionary dictionary;

        public void update(VoxelizedMarkMemo mark, EnergyStackWithoutParameters raster)
                throws OperationFailedException {
            this.mark = mark;
            this.raster = raster;

            DictionaryForImageCreator creator =
                    new DictionaryForImageCreator(energyScheme, sharedFeatures, logger);
            try {
                this.dictionary = creator.create(raster);
            } catch (CreateException e) {
                throw new OperationFailedException(e);
            }
        }

        @Override
        public EnergyTotal apply(Integer index) throws NamedFeatureCalculateException {
            return calc();
        }

        public EnergyTotal calc() throws NamedFeatureCalculateException {
            try {
                FeatureCalculatorMulti<FeatureInputSingleMemo> session =
                        FeatureSession.with(
                                energyScheme.getElemIndAsFeatureList(),
                                new FeatureInitialization(dictionary),
                                sharedFeatures,
                                logger);

                FeatureInputSingleMemo input =
                        new FeatureInputSingleMemo(mark, new EnergyStack(raster, dictionary));

                return new EnergyTotal(session.calculate(input).total());
            } catch (InitializeException e) {
                throw new NamedFeatureCalculateException(e);
            }
        }
    }

    public EnergySchemeWithSharedFeatures(
            EnergyScheme energyScheme, SharedFeatures sharedFeatures, Logger logger) {
        super();
        this.energyScheme = energyScheme;
        this.sharedFeatures = sharedFeatures;
        this.logger = logger;

        calculateTotalIndividual = new CalculateIndividualTotalOperation();
    }

    public EnergyTotal totalAll(EnergyMemoList pxlMarkMemoList, EnergyStackWithoutParameters raster)
            throws NamedFeatureCalculateException {

        try {
            EnergyStack energyStack = createEnergyStack(raster);

            FeatureCalculatorMulti<FeatureInputAllMemo> session =
                    FeatureSession.with(
                            energyScheme.getElemAllAsFeatureList(),
                            new FeatureInitialization(energyStack.getParameters()),
                            sharedFeatures,
                            logger);

            FeatureInputAllMemo input = new FeatureInputAllMemo(pxlMarkMemoList, energyStack);

            return new EnergyTotal(session.calculate(input).total());

        } catch (InitializeException | FeatureCalculationException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    public EnergyTotal totalIndividual(VoxelizedMarkMemo pmm, EnergyStackWithoutParameters raster)
            throws NamedFeatureCalculateException {
        try {
            calculateTotalIndividual.update(pmm, raster);
            return calculateTotalIndividual.calc();
        } catch (OperationFailedException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    public AddCriteriaEnergyPair createAddCriteria() throws CreateException {
        try {
            return new AddCriteriaEnergyPair(
                    getEnergyScheme().getElemPairAsFeatureList(),
                    (AddCriteriaPair) getEnergyScheme().getPairAddCriteria().duplicateBean());
        } catch (InitializeException | BeanDuplicateException e) {
            throw new CreateException(e);
        }
    }

    public RegionMap getRegionMap() {
        return energyScheme.getRegionMap();
    }

    private EnergyStack createEnergyStack(EnergyStackWithoutParameters raster)
            throws FeatureCalculationException {

        try {
            return new EnergyStack(raster, energyScheme.createDictionary());
        } catch (CreateException e) {
            throw new FeatureCalculationException(e);
        }
    }
}
