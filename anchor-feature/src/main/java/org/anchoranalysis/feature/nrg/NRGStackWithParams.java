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

package org.anchoranalysis.feature.nrg;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.stack.Stack;

// An NRG stack with associated parameters
public class NRGStackWithParams {

    @Getter private final NRGStack nrgStack;
    @Getter @Setter private KeyValueParams params;

    public NRGStackWithParams(Channel channel) {
        super();
        this.nrgStack = new NRGStack(channel);
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams(NRGStack nrgStack) {
        super();
        this.nrgStack = nrgStack;
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams(NRGStack nrgStack, KeyValueParams params) {
        super();
        this.nrgStack = nrgStack;
        this.params = params;
    }

    public NRGStackWithParams(Stack stackIn, KeyValueParams params) {
        this.nrgStack = new NRGStack(stackIn);
        this.params = params;
    }

    public NRGStackWithParams(Stack stackIn) {
        this.nrgStack = new NRGStack(stackIn);
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams(Dimensions dimensions) {
        this.nrgStack = new NRGStack(dimensions);
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams extractSlice(int z) throws OperationFailedException {
        return new NRGStackWithParams(nrgStack.extractSlice(z), params);
    }

    public Dimensions dimensions() {
        return nrgStack.dimensions();
    }

    public NRGStackWithParams copyChangeParams(KeyValueParams paramsToAssign) {
        return new NRGStackWithParams(nrgStack, paramsToAssign);
    }

    public Channel getChannel(int index) {
        return nrgStack.getChannel(index);
    }

    public Stack asStack() {
        return nrgStack.asStack();
    }

    public Extent extent() {
        return nrgStack.extent();
    }
}
