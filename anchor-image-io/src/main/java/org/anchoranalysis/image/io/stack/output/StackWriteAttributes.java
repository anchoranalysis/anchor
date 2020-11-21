/*-
 * #%L
 * anchor-image-io
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
package org.anchoranalysis.image.io.stack.output;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Attributes describing stack which may determine which writer is used.
 *
 * <p>This class is <i>immutable</i>.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class StackWriteAttributes {

    /** True the output is guaranteed to only ever 2D i.e. maximally one z-slice? */
    private boolean always2D;

    /** The number of channels is guaranteed to be 1 in the output. */
    private boolean singleChannel;
    
    /** The number of channels is guaranteed to be 3 in the output. */
    private boolean threeChannels;

    /***
     * Whether it's an RGB image when it has three channels (the three channels visualized jointly, rather than independently)
     *
     * <p>This flag should only be set when {@code alwaysOneOrThreChannels} is true.
     *
     * <p>This flag is ignored, when the number of channels is not three.
     */
    private boolean rgb;
    
    /**
     * Whether all channels represent a binary image.
     * 
     * <p>This implies each channel has only two allowed states: the max intensity value and the minimum intensity value.
     */
    private boolean binary;

    /**
     * Derives a {@link StackWriteAttributes} that will always be 2D, but is otherwise unchanged.
     *
     * @param binary whether all channels are binary (only two allowed states: the max intensity value and the minimum intensity value.)
     * @return a newly created {@link StackWriteAttributes} derived from the existing object.
     */
    public StackWriteAttributes always2D(boolean binary) {
        return new StackWriteAttributes(true, singleChannel, threeChannels, rgb, binary);
    }

    /**
     * Derives a {@link StackWriteAttributes} that will be RGB, but is otherwise unchanged.
     *
     * @return a newly created {@link StackWriteAttributes} derived from the existing object.
     */
    public StackWriteAttributes rgb() {
        return new StackWriteAttributes(always2D, false, true, true, false);
    }

    /**
     * Combines with another {@link StackWriteAttributes} by performing a logical <i>and</i> on each
     * field.
     *
     * @param other the other {@link StackWriteAttributes} to combine with.
     * @return a newly created {@link StackWriteAttributes} where each field is the logical <i>and</i>
     *     of the two inputs
     */
    public StackWriteAttributes and(StackWriteAttributes other) {
        return new StackWriteAttributes(
                always2D && other.always2D,
                singleChannel && other.singleChannel,
                threeChannels && other.threeChannels,                
                rgb && other.rgb,
                binary && other.binary);
    }

    /**
     * Combines with another {@link StackWriteAttributes} by performing a logical <i>or</i> on each
     * field.
     *
     * @param other the other {@link StackWriteAttributes} to combine with.
     * @return a newly created {@link StackWriteAttributes} where each field is the logical <i>or</i>
     *     of the two inputs
     */
    public StackWriteAttributes or(StackWriteAttributes other) {
        return new StackWriteAttributes(
                always2D || other.always2D,
                singleChannel || other.singleChannel,
                threeChannels || other.threeChannels,
                rgb || other.rgb,
                binary || other.binary);
    }

    /**
     * Whether to write a stack in RGB mode?
     *
     * @param stack the stack to query whether it should be written in RGB mode.
     * @return true if the stack should be written as RGB, false otherwise.
     */
    public boolean writeAsRGB(Stack stack) {
        return rgb && stack.getNumberChannels() == 3 && stack.isRGB();
    }
}
