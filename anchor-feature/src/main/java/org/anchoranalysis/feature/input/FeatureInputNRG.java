/* (C)2020 */
package org.anchoranalysis.feature.input;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;

@AllArgsConstructor
@EqualsAndHashCode
public abstract class FeatureInputNRG implements FeatureInputParams {

    private Optional<NRGStackWithParams> nrgStack;

    public FeatureInputNRG() {
        this(Optional.empty());
    }

    @Override
    public Optional<ImageResolution> getResOptional() {
        return nrgStack.map(NRGStackWithParams::getDimensions).map(ImageDimensions::getRes);
    }

    @Override
    public Optional<KeyValueParams> getParamsOptional() {
        return nrgStack.map(NRGStackWithParams::getParams);
    }

    public ImageDimensions getDimensionsRequired() throws FeatureCalcException {
        return getDimensionsOptional()
                .orElseThrow(
                        () ->
                                new FeatureCalcException(
                                        "Dimensions are required in the input for this operation"));
    }

    public Optional<ImageDimensions> getDimensionsOptional() {
        return nrgStack.map(NRGStackWithParams::getDimensions);
    }

    /**
     * Returns the nrg-stack or throws an exception if it's not set as it's required.
     *
     * @throws FeatureCalcException if the nrg-stack isn't present
     */
    public NRGStackWithParams getNrgStackRequired() throws FeatureCalcException {
        return nrgStack.orElseThrow(
                () ->
                        new FeatureCalcException(
                                "An NRG-stack is required in the input for this operation"));
    }

    /** Returns the nrg-stack which may or not be present */
    public Optional<NRGStackWithParams> getNrgStackOptional() {
        return nrgStack;
    }

    public void setNrgStack(NRGStackWithParams nrgStack) {
        this.nrgStack = Optional.of(nrgStack);
    }

    public void setNrgStack(Optional<NRGStackWithParams> nrgStack) {
        this.nrgStack = nrgStack;
    }
}
