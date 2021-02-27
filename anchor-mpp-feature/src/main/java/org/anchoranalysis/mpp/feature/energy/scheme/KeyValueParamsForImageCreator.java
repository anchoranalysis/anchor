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
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.single.FeatureCalculatorSingle;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.image.feature.input.FeatureInputStack;

/**
 * Creates KeyValueParams for a particular EnergyStack that is associated with a EnergyScheme
 *
 * @author Owen Feehan
 */
public class KeyValueParamsForImageCreator {

    private EnergyScheme energyScheme;
    private SharedFeatureMulti sharedFeatures;
    private Logger logger;

    public KeyValueParamsForImageCreator(
            EnergyScheme energyScheme, SharedFeatureMulti sharedFeatures, Logger logger) {
        super();
        this.energyScheme = energyScheme;
        this.sharedFeatures = sharedFeatures;
        this.logger = logger;
    }

    public Dictionary createParamsForImage(EnergyStackWithoutParams energyStack)
            throws CreateException {
        try {
            Dictionary params = energyScheme.createKeyValueParams();
            addParamsForImage(energyStack, params);
            return params;

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    private void addParamsForImage(EnergyStackWithoutParams energyStack, Dictionary kvp)
            throws OperationFailedException {

        FeatureInputStack params = new FeatureInputStack(energyStack);

        FeatureInitParams paramsInit =
                new FeatureInitParams(Optional.of(kvp), Optional.of(energyStack), Optional.empty());

        for (NamedBean<Feature<FeatureInputStack>> ni : energyScheme.getListImageFeatures()) {

            kvp.putCheck(ni.getName(), calculateImageFeature(ni.getItem(), paramsInit, params));
        }
    }

    private double calculateImageFeature(
            Feature<FeatureInputStack> feature,
            FeatureInitParams paramsInit,
            FeatureInputStack params)
            throws OperationFailedException {

        try {
            FeatureCalculatorSingle<FeatureInputStack> session =
                    FeatureSession.with(feature, paramsInit, sharedFeatures, logger);

            return session.calculate(params);

        } catch (FeatureCalculationException | InitException e) {
            throw new OperationFailedException(e);
        }
    }
}
