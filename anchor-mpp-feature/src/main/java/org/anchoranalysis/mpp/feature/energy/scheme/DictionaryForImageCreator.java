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

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorSingle;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.shared.SharedFeatures;
import org.anchoranalysis.image.feature.input.FeatureInputStack;

/**
 * Creates a {@link Dictionary} for a particular {@link EnergyStack} that is associated with an
 * {@link EnergyScheme}.
 *
 * <p>This class calculates image features defined in the energy scheme and stores them in a
 * dictionary.
 */
@AllArgsConstructor
public class DictionaryForImageCreator {

    /** The energy scheme containing the image features to be calculated. */
    private EnergyScheme energyScheme;

    /** Shared features used in the feature calculation process. */
    private SharedFeatures sharedFeatures;

    /** Logger for reporting any issues during the creation process. */
    private Logger logger;

    /**
     * Creates a dictionary containing calculated image features for a given energy stack.
     *
     * @param energyStack the energy stack without parameters
     * @return a dictionary containing the calculated image features
     * @throws CreateException if there's an error during the dictionary creation process
     */
    public Dictionary create(EnergyStackWithoutParameters energyStack) throws CreateException {
        try {
            Dictionary dictionary = energyScheme.createDictionary();
            addParametersForImage(energyStack, dictionary);
            return dictionary;
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    /**
     * Adds calculated image feature parameters to the dictionary for a given energy stack.
     *
     * @param energyStack the energy stack without parameters
     * @param dictionary the dictionary to add the calculated features to
     * @throws OperationFailedException if there's an error during the feature calculation process
     */
    private void addParametersForImage(
            EnergyStackWithoutParameters energyStack, Dictionary dictionary)
            throws OperationFailedException {

        FeatureInputStack parameters = new FeatureInputStack(energyStack);

        FeatureInitialization initialization =
                new FeatureInitialization(
                        Optional.of(dictionary), Optional.of(energyStack), Optional.empty());

        for (NamedBean<Feature<FeatureInputStack>> feature : energyScheme.getListImageFeatures()) {
            dictionary.putCheck(
                    feature.getName(),
                    calculateImageFeature(feature.getItem(), initialization, parameters));
        }
    }

    /**
     * Calculates a single image feature.
     *
     * @param feature the feature to calculate
     * @param initialization the feature initialization parameters
     * @param parameters the input parameters for the feature calculation
     * @return the calculated feature value
     * @throws OperationFailedException if there's an error during the feature calculation process
     */
    private double calculateImageFeature(
            Feature<FeatureInputStack> feature,
            FeatureInitialization initialization,
            FeatureInputStack parameters)
            throws OperationFailedException {

        try {
            FeatureCalculatorSingle<FeatureInputStack> session =
                    FeatureSession.with(feature, initialization, sharedFeatures, logger);

            return session.calculate(parameters);

        } catch (FeatureCalculationException | InitializeException e) {
            throw new OperationFailedException(e);
        }
    }
}
