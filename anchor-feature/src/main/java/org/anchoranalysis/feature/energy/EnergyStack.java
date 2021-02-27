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

package org.anchoranalysis.feature.energy;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.spatial.Extent;

/**
 * A {@link EnergyStackWithoutParams} with associated {@link Dictionary}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class EnergyStack {

    private final EnergyStackWithoutParams delegate;
    @Getter @Setter private Dictionary dictionary;

    public EnergyStack(Channel channel) {
        this(new EnergyStackWithoutParams(channel));
    }

    public EnergyStack(EnergyStackWithoutParams energyStack) {
        this(energyStack, new Dictionary());
    }

    public EnergyStack(Stack stack, Dictionary dictionary) {
        this(new EnergyStackWithoutParams(stack), dictionary);
    }

    public EnergyStack(Stack stack) {
        this(new EnergyStackWithoutParams(stack));
    }

    public EnergyStack(Dimensions dimensions) {
        this(new EnergyStackWithoutParams(dimensions));
    }

    public EnergyStack extractSlice(int z) throws OperationFailedException {
        return new EnergyStack(delegate.extractSlice(z), dictionary);
    }

    /**
     * Does exactly one z-slice exist in the energy stack?
     *
     * @return true iff the number of z-slices is 1
     */
    public boolean hasOneSlice() {
        return dimensions().extent().z() == 1;
    }

    /**
     * The image-dimensions associated with the energy-stack.
     *
     * @return the image-dimensions.
     */
    public Dimensions dimensions() {
        return delegate.dimensions();
    }

    /**
     * The image-resolution asssociated with the energy-stack.
     *
     * @return the image-resolution.
     */
    public Optional<Resolution> resolution() {
        return dimensions().resolution();
    }

    public EnergyStack copyChangeParams(Dictionary dictionaryToAssign) {
        return new EnergyStack(delegate, dictionaryToAssign);
    }

    public Channel getChannel(int index) {
        return delegate.getChannel(index);
    }

    public Stack asStack() {
        return delegate.asStack();
    }

    public Extent extent() {
        return delegate.extent();
    }

    /**
     * The energy-stack without associated parameters.
     *
     * @return a representation of the energy-stack without params (not newly created).
     */
    public EnergyStackWithoutParams withoutParams() {
        return delegate;
    }
}
