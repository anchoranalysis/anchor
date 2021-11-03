package org.anchoranalysis.image.feature.bean;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.image.feature.input.FeatureInputImageMetadata;

/** A base class for a feature that accepts a {@link FeatureInputImageMetadata} as input. */
public abstract class FeatureImageMetadata extends Feature<FeatureInputImageMetadata> {

    @Override
    public double calculate(SessionInput<FeatureInputImageMetadata> input)
            throws FeatureCalculationException {
        return calculate(input.get().getMetadata());
    }

    /**
     * Calculates the feature-value for a given {@link ImageMetadata}.
     *
     * @param metadata the metadata.
     * @return the calculated feature-value.
     * @throws FeatureCalculationException if the feature-value cannot be calculated.
     */
    public abstract double calculate(ImageMetadata metadata) throws FeatureCalculationException;

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputImageMetadata.class;
    }
}
