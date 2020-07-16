/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import java.util.Optional;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

/**
 * Creates KeyValueParams for a particular NRGStack that is associated with a NRGScheme
 *
 * @author Owen Feehan
 */
public class KeyValueParamsForImageCreator {

    private NRGScheme nrgScheme;
    private SharedFeatureMulti sharedFeatures;
    private Logger logger;

    public KeyValueParamsForImageCreator(
            NRGScheme nrgScheme, SharedFeatureMulti sharedFeatures, Logger logger) {
        super();
        this.nrgScheme = nrgScheme;
        this.sharedFeatures = sharedFeatures;
        this.logger = logger;
    }

    public KeyValueParams createParamsForImage(NRGStack nrgStack) throws FeatureCalcException {
        try {
            KeyValueParams params = nrgScheme.createKeyValueParams();
            addParamsForImage(nrgStack, params);
            return params;

        } catch (CreateException | OperationFailedException e) {
            throw new FeatureCalcException(e);
        }
    }

    private void addParamsForImage(NRGStack nrgStack, KeyValueParams kvp)
            throws OperationFailedException {

        FeatureInputStack params = new FeatureInputStack(nrgStack);

        FeatureInitParams paramsInit =
                new FeatureInitParams(Optional.of(kvp), Optional.of(nrgStack), Optional.empty());

        for (NamedBean<Feature<FeatureInputStack>> ni : nrgScheme.getListImageFeatures()) {

            kvp.putIfEmpty(ni.getName(), calcImageFeature(ni.getItem(), paramsInit, params));
        }
    }

    private double calcImageFeature(
            Feature<FeatureInputStack> feature,
            FeatureInitParams paramsInit,
            FeatureInputStack params)
            throws OperationFailedException {

        try {
            FeatureCalculatorSingle<FeatureInputStack> session =
                    FeatureSession.with(feature, paramsInit, sharedFeatures, logger);

            return session.calc(params);

        } catch (FeatureCalcException e) {
            throw new OperationFailedException(e);
        }
    }
}
