/* (C)2020 */
package org.anchoranalysis.feature.input;

import java.util.Optional;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.extent.ImageResolution;

public interface FeatureInputWithRes extends FeatureInput {

    Optional<ImageResolution> getResOptional();

    default ImageResolution getResRequired() throws FeatureCalcException {
        return getResOptional()
                .orElseThrow(
                        () ->
                                new FeatureCalcException(
                                        "An image-resolution is required to be associated with the input for this feature"));
    }
}
