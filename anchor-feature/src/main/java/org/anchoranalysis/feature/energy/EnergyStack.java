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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.stack.Stack;

// An energy-stack with associated parameters
public class EnergyStack {

    @Getter private final EnergyStackWithoutParams energyStack;
    @Getter @Setter private KeyValueParams params;

    public EnergyStack(Channel channel) {
        super();
        this.energyStack = new EnergyStackWithoutParams(channel);
        this.params = new KeyValueParams();
    }

    public EnergyStack(EnergyStackWithoutParams energyStack) {
        super();
        this.energyStack = energyStack;
        this.params = new KeyValueParams();
    }

    public EnergyStack(EnergyStackWithoutParams energyStack, KeyValueParams params) {
        super();
        this.energyStack = energyStack;
        this.params = params;
    }

    public EnergyStack(Stack stackIn, KeyValueParams params) {
        this.energyStack = new EnergyStackWithoutParams(stackIn);
        this.params = params;
    }

    public EnergyStack(Stack stackIn) {
        this.energyStack = new EnergyStackWithoutParams(stackIn);
        this.params = new KeyValueParams();
    }

    public EnergyStack(Dimensions dimensions) {
        this.energyStack = new EnergyStackWithoutParams(dimensions);
        this.params = new KeyValueParams();
    }

    public EnergyStack extractSlice(int z) throws OperationFailedException {
        return new EnergyStack(energyStack.extractSlice(z), params);
    }

    public Dimensions dimensions() {
        return energyStack.dimensions();
    }
    
    public Resolution resolution() {
        return dimensions().resolution();
    }

    public EnergyStack copyChangeParams(KeyValueParams paramsToAssign) {
        return new EnergyStack(energyStack, paramsToAssign);
    }

    public Channel getChannel(int index) {
        return energyStack.getChannel(index);
    }

    public Stack asStack() {
        return energyStack.asStack();
    }

    public Extent extent() {
        return energyStack.extent();
    }
}
