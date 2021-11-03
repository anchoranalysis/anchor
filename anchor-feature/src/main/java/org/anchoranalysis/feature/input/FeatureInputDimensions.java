package org.anchoranalysis.feature.input;

import java.util.Optional;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import lombok.EqualsAndHashCode;

/**
 * Any {@link FeatureInput} that exposes the {@link Dimensions} of an image or scene.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode
public abstract class FeatureInputDimensions implements FeatureInputWithResolution { // NOSONAR

    /**
     * The dimensions of the associated image.
     *
     * @return the dimensions.
     * @throws FeatureCalculationException if the dimensions cannot be calculated.
     */
    public abstract Dimensions dimensions() throws FeatureCalculationException;

    @Override
    public Optional<Resolution> getResolutionOptional() {
        try {
            return dimensions().resolution();
        } catch (FeatureCalculationException e) {
            // If any error occurs, we ignore the resolution information.
            return Optional.empty();
        }
    }
}
