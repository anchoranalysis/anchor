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

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.OperationFailedRuntimeException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.Stack;


/**
 * A stack of channels used as context to calculate features (or calculating <i>energy</i> more broadly).
 * <p>
 * The stack can have 0 channels, in which case, explict dimensions must be set.
 *  
 * @author Owen Feehan
 *
 */
public class NRGStack {

    // START UNION: EITHER/OR
    // Always one of the following two variables should be defined and the other should be empty.
    
    /** A delegate-stack if it exists (should NOT be set if {@code dimensions} are defined) */
    private final Optional<Stack> stack;
    
    /** Dimensions if needed (should only be set if {@code stack} is empty) */
    private final Optional<ImageDimensions> dimensions;
    // END UNION: EITHER/OR

    /**
     * Create a nrg-stack comprised of a single channel
     * 
     * @param channel
     */
    public NRGStack(Channel channel) {
        super();
        this.dimensions = Optional.empty();
        this.stack = Optional.of( new Stack(channel) );
    }

    /**
     * Create a nrg-stack comprised of all channels from a stack
     * 
     * @param stackIn
     */
    public NRGStack(Stack stackIn) {
        this.dimensions = Optional.empty();
        this.stack = Optional.of(stackIn);
    }

    /**
     * Create a nrg-stack with no channels - but with dimensions associated
     * @param dimensions
     */
    public NRGStack(ImageDimensions dimensions) {
        this.dimensions = Optional.of(dimensions);
        this.stack = Optional.empty();
    }

    
    public final int getNumberChannels() {
        return stack.map(Stack::getNumberChannels).orElse(0);
    }

    public ImageDimensions getDimensions() {
        return stack.map(Stack::getDimensions).orElseGet( dimensions::get ); // NOSONAR
    }

    public final Channel getChannel(int index) {

        if (!stack.isPresent()) {
            throwInvalidIndexException(0, index);
        }

        if (index >= stack.get().getNumberChannels()) {
            throwInvalidIndexException(stack.get().getNumberChannels(), index);
        }

        return stack.get().getChannel(index);
    }

    public Stack asStack() {
        return stack.orElse( new Stack() );
    }

    public NRGStack extractSlice(int z) throws OperationFailedException {
        
        if (!stack.isPresent()) {
            throw new OperationFailedException("No slice can be extracted, as no channels existing in the nrg-stack");
        }
        
        return new NRGStack(stack.get().extractSlice(z));
    }
    
    private void throwInvalidIndexException(int numberChannels, int index) {
        throw new OperationFailedRuntimeException(
                String.format(
                        "There are %d channels in the nrg-stack. Cannot access index %d.",
                        numberChannels, index));
    }
}
