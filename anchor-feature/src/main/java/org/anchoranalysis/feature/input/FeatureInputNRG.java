/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.input;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalculationException;
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

    public ImageDimensions getDimensionsRequired() throws FeatureCalculationException {
        return getDimensionsOptional()
                .orElseThrow(
                        () ->
                                new FeatureCalculationException(
                                        "Dimensions are required in the input for this operation"));
    }

    public Optional<ImageDimensions> getDimensionsOptional() {
        return nrgStack.map(NRGStackWithParams::getDimensions);
    }

    /**
     * Returns the nrg-stack or throws an exception if it's not set as it's required.
     *
     * @throws FeatureCalculationException if the nrg-stack isn't present
     */
    public NRGStackWithParams getNrgStackRequired() throws FeatureCalculationException {
        return nrgStack.orElseThrow(
                () ->
                        new FeatureCalculationException(
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
