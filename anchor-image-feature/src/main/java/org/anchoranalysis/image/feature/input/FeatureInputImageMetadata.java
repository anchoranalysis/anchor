package org.anchoranalysis.image.feature.input;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputDimensions;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.ImageMetadata;

/**
 * A {@link FeatureInput} that provides {@link ImageMetadata}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FeatureInputImageMetadata extends FeatureInputDimensions {

    /** The image metadata. */
    @Getter private ImageMetadata metadata;

    @Override
    public Dimensions dimensions() throws FeatureCalculationException {
        return metadata.getDimensions();
    }
}
