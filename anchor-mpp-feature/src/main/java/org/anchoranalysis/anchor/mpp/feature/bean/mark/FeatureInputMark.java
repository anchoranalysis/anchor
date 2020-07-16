/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInputParams;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;

@EqualsAndHashCode
public class FeatureInputMark implements FeatureInputParams {

    private final Mark mark;
    private final Optional<ImageDimensions> dimensions;
    private final Optional<KeyValueParams> params;

    public FeatureInputMark(Mark mark, Optional<ImageDimensions> dimensions) {
        this.mark = mark;
        this.dimensions = dimensions;
        this.params = Optional.empty();
    }

    public FeatureInputMark(Mark mark, ImageDimensions dimensions, KeyValueParams params) {
        this(mark, Optional.of(dimensions), Optional.of(params));
    }

    public FeatureInputMark(
            Mark mark, Optional<ImageDimensions> dim, Optional<KeyValueParams> params) {
        super();
        this.mark = mark;
        this.dimensions = dim;
        this.params = params;
    }

    public Mark getMark() {
        return mark;
    }

    @Override
    public Optional<ImageResolution> getResOptional() {
        return dimensions.map(ImageDimensions::getRes);
    }

    @Override
    public Optional<KeyValueParams> getParamsOptional() {
        return params;
    }

    public Optional<ImageDimensions> getDimensionsOptional() {
        return dimensions;
    }

    public ImageDimensions getDimensionsRequired() throws FeatureCalcException {
        return dimensions.orElseThrow(
                () ->
                        new FeatureCalcException(
                                "Dimensions are required in the input for this operation"));
    }
}
