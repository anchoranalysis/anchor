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

/**
 * Combines an {@link EnergyScheme} with {@link SharedFeatures} for energy calculations.
 */
public class EnergySchemeWithSharedFeatures {

    /** The energy scheme used for calculations. */
    @Getter private EnergyScheme energyScheme;

    /** Shared features used across calculations. */
    @Getter private SharedFeatures sharedFeatures;

    /** Operation for calculating individual total energy. */
    private CalculateIndividualTotalOperation calculateTotalIndividual;

    /** Logger for reporting messages. */
    private Logger logger;

    /**
     * Caches energy value by index.
     */
    private class CalculateIndividualTotalOperation
            implements CheckedFunction<Integer, EnergyTotal, NamedFeatureCalculateException> {

        /** The mark for which energy is calculated. */
        private VoxelizedMarkMemo mark;

        /** The energy stack without parameters. */
        private EnergyStackWithoutParameters raster;

        /** Dictionary of parameters for energy calculations. */
        private Dictionary dictionary;

        /**
         * Updates the operation with new mark and raster.
         *
         * @param mark the new mark
         * @param raster the new raster
         * @throws OperationFailedException if the update operation fails
         */
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

        /**
         * Calculates the energy total.
         *
         * @return the calculated energy total
         * @throws NamedFeatureCalculateException if the calculation fails
         */
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

    /**
     * Creates a new {@link EnergySchemeWithSharedFeatures}.
     *
     * @param energyScheme the energy scheme to use
     * @param sharedFeatures the shared features to use
     * @param logger the logger for reporting messages
     */
    public EnergySchemeWithSharedFeatures(
            EnergyScheme energyScheme, SharedFeatures sharedFeatures, Logger logger) {
        super();
        this.energyScheme = energyScheme;
        this.sharedFeatures = sharedFeatures;
        this.logger = logger;

        calculateTotalIndividual = new CalculateIndividualTotalOperation();
    }

    /**
     * Calculates the total energy for all marks in the list.
     *
     * @param pxlMarkMemoList the list of energy memos
     * @param raster the energy stack without parameters
     * @return the total energy
     * @throws NamedFeatureCalculateException if the calculation fails
     */
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

    /**
     * Calculates the total energy for an individual mark.
     *
     * @param pmm the voxelized mark memo
     * @param raster the energy stack without parameters
     * @return the total energy
     * @throws NamedFeatureCalculateException if the calculation fails
     */
    public EnergyTotal totalIndividual(VoxelizedMarkMemo pmm, EnergyStackWithoutParameters raster)
            throws NamedFeatureCalculateException {
        try {
            calculateTotalIndividual.update(pmm, raster);
            return calculateTotalIndividual.calc();
        } catch (OperationFailedException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    /**
     * Creates an {@link AddCriteriaEnergyPair} based on the energy scheme.
     *
     * @return the created AddCriteriaEnergyPair
     * @throws CreateException if creation fails
     */
    public AddCriteriaEnergyPair createAddCriteria() throws CreateException {
        try {
            return new AddCriteriaEnergyPair(
                    getEnergyScheme().getElemPairAsFeatureList(),
                    (AddCriteriaPair) getEnergyScheme().getPairAddCriteria().duplicateBean());
        } catch (InitializeException | BeanDuplicateException e) {
            throw new CreateException(e);
        }
    }

    /**
     * Gets the region map from the energy scheme.
     *
     * @return the region map
     */
    public RegionMap getRegionMap() {
        return energyScheme.getRegionMap();
    }

    /**
     * Creates an energy stack with parameters.
     *
     * @param raster the energy stack without parameters
     * @return the created energy stack
     * @throws FeatureCalculationException if creation fails
     */
    private EnergyStack createEnergyStack(EnergyStackWithoutParameters raster)
            throws FeatureCalculationException {

        try {
            return new EnergyStack(raster, energyScheme.createDictionary());
        } catch (CreateException e) {
            throw new FeatureCalculationException(e);
        }
    }
}