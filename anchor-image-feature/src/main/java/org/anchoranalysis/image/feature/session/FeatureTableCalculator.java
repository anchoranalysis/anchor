/* (C)2020 */
package org.anchoranalysis.image.feature.session;

import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;

/**
 * A feature-calculator with additional functions for encoding the output in in a tabular-format
 * with column-names
 *
 * @author Owen Feehan
 * @param <T>
 */
public interface FeatureTableCalculator<T extends FeatureInput> extends FeatureCalculatorMulti<T> {

    /**
     * Initializes a feature store that has the same structure as that previously created by
     * createFeatures() from the same object
     *
     * @param initParams
     * @param nrgStack
     * @param logger
     * @param features
     */
    void start(ImageInitParams initParams, Optional<NRGStackWithParams> nrgStack, Logger logger)
            throws InitException;

    /**
     * Makes a copy of the feature-store for a new thread. Deep-copies the features. Shallow-copies
     * everything else.
     *
     * @return
     */
    FeatureTableCalculator<T> duplicateForNewThread();

    /** A list of names for each feature (columns ofthe table) */
    FeatureNameList createFeatureNames();
}
