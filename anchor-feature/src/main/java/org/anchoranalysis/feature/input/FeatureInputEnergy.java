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
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.dimensions.UnitConverter;

/**
 * A {@link FeatureInputDimensions} that has an optional {@link EnergyStack} associated with it.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class FeatureInputEnergy extends FeatureInputDimensions
        implements FeatureInputDictionary {

    private Optional<EnergyStack> energyStack;

    /** Creates without an associated energy-stack. */
    protected FeatureInputEnergy() {
        this(Optional.empty());
    }

    /**
     * Converts voxelized measurements to/from physical units.
     *
     * @return a converter that will perform conversions using current resolution, if it exists.
     */
    public Optional<UnitConverter> getUnitConverterOptional() {
        return getResolutionOptional().map(Resolution::unitConvert);
    }

    @Override
    public Optional<Resolution> getResolutionOptional() {
        return energyStack.map(EnergyStack::dimensions).flatMap(Dimensions::resolution);
    }

    @Override
    public Optional<Dictionary> getDictionaryOptional() {
        return energyStack.map(EnergyStack::getParameters);
    }

    @Override
    public Dimensions dimensions() throws FeatureCalculationException {
        return dimensionsRequired();
    }

    /**
     * The image-dimensions associated with the energy-stack, or an exception if no energy-stack
     * exists.
     *
     * @return the image-dimensions.
     * @throws FeatureCalculationException if the dimensions are not known.
     */
    public Dimensions dimensionsRequired() throws FeatureCalculationException {
        return dimensionsOptional()
                .orElseThrow(
                        () ->
                                new FeatureCalculationException(
                                        "Dimensions are required in the input for this operation"));
    }

    /**
     * The image-dimensions associated with the energy-stack, if it exists.
     *
     * @return the image-dimensions, if it exists.
     */
    public Optional<Dimensions> dimensionsOptional() {
        return energyStack.map(EnergyStack::dimensions);
    }

    /**
     * The associated energy-stack or throws an exception if it isn't present.
     *
     * @return the associated energy-stack.
     * @throws FeatureCalculationException if the energy-stack isn't present
     */
    public EnergyStack getEnergyStackRequired() throws FeatureCalculationException {
        return energyStack.orElseThrow(
                () ->
                        new FeatureCalculationException(
                                "An energy-stack is required in the input for this operation"));
    }

    /**
     * The associated energy-stack.
     *
     * @return the energy-stack, if present.
     */
    public Optional<EnergyStack> getEnergyStackOptional() {
        return energyStack;
    }

    /**
     * Assigns an {@link EnergyStack} to be associated with the input.
     *
     * <p>Any existing energy-stack is replaced.
     *
     * @param energyStack the energy-stack to assign.
     */
    public void setEnergyStack(EnergyStack energyStack) {
        this.energyStack = Optional.of(energyStack);
    }

    /**
     * Assigns an optional {@link EnergyStack} to be associated with the input.
     *
     * <p>Any existing energy-stack state is replaced.
     *
     * @param energyStack the optional energy-stack to assign.
     */
    public void setEnergyStack(Optional<EnergyStack> energyStack) {
        this.energyStack = energyStack;
    }
}
