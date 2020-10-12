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
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.extent.UnitConverter;

@AllArgsConstructor
@EqualsAndHashCode
public abstract class FeatureInputEnergy implements FeatureInputParams {

    private Optional<EnergyStack> energyStack;

    public FeatureInputEnergy() {
        this(Optional.empty());
    }

    public Optional<UnitConverter> getUnitConverterOptional() {
        return getResolutionOptional().map(Resolution::unitConvert);
    }

    @Override
    public Optional<Resolution> getResolutionOptional() {
        return energyStack.map(EnergyStack::dimensions).flatMap(Dimensions::resolution);
    }

    @Override
    public Optional<KeyValueParams> getParamsOptional() {
        return energyStack.map(EnergyStack::getParams);
    }

    public Dimensions dimensionsRequired() throws FeatureCalculationException {
        return dimensionsOptional()
                .orElseThrow(
                        () ->
                                new FeatureCalculationException(
                                        "Dimensions are required in the input for this operation"));
    }

    public Optional<Dimensions> dimensionsOptional() {
        return energyStack.map(EnergyStack::dimensions);
    }

    /**
     * Returns the energy-stack or throws an exception if it's not set as it's required.
     *
     * @throws FeatureCalculationException if the energy-stack isn't present
     */
    public EnergyStack getEnergyStackRequired() throws FeatureCalculationException {
        return energyStack.orElseThrow(
                () ->
                        new FeatureCalculationException(
                                "An energy-stack is required in the input for this operation"));
    }

    /** Returns the energy-stack which may or not be present */
    public Optional<EnergyStack> getEnergyStackOptional() {
        return energyStack;
    }

    public void setEnergyStack(EnergyStack energyStack) {
        this.energyStack = Optional.of(energyStack);
    }

    public void setEnergyStack(Optional<EnergyStack> energyStack) {
        this.energyStack = energyStack;
    }
}
