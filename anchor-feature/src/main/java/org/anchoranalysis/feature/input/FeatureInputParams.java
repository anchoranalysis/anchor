/* (C)2020 */
package org.anchoranalysis.feature.input;

import java.util.Optional;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;

/**
 * A feature-input that is associated with particular key-value parameters
 *
 * @author Owen Feehan
 */
public interface FeatureInputParams extends FeatureInputWithRes {

    Optional<KeyValueParams> getParamsOptional();

    default KeyValueParams getParamsRequired() throws FeatureCalcException {
        return getParamsOptional()
                .orElseThrow(() -> new FeatureCalcException("Params are required for this input"));
    }
}
