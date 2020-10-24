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
package org.anchoranalysis.image.io.stack;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Options describing stack which may determine which writer is used.
 *
 * <p>This class is <i>immutable</i>.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class StackWriteOptions {

    /** True the output is guaranteed to only ever 2D i.e. maximally one z-slice? */
    private boolean always2D;

    /** The number of channels is guaranteed to be 1 or 3 in the output. */
    private boolean alwaysOneOrThreeChannels;

    /***
     * Whether it's an RGB image when it has three channels (the three channels visualized jointly, rather than independently)
     *
     * <p>This flag should only be set when {@code alwaysOneOrThreChannels} is true.
     *
     * <p>This flag is ignored, when the number of channels is not three.
     */
    private boolean rgb;

    /**
     * Derives a {@link StackWriteOptions} that will always be 2D, but is otherwise unchanged.
     *
     * @return a newly created {@link StackWriteOptions} derived from the existing object.
     */
    public StackWriteOptions always2D() {
        return new StackWriteOptions(true, alwaysOneOrThreeChannels, rgb);
    }

    /**
     * Derives a {@link StackWriteOptions} that will be RGB, but is otherwise unchanged.
     *
     * @return a newly created {@link StackWriteOptions} derived from the existing object.
     */
    public StackWriteOptions rgb() {
        return new StackWriteOptions(always2D, true, true);
    }

    /**
     * Combines with another {@link StackWriteOptions} by performing a logical <i>and</i> on each
     * field.
     *
     * @param other the other {@link StackWriteOptions} to combine with.
     * @return a newly created {@link StackWriteOptions} where each field is the logical <i>and</i>
     *     of the two inputs
     */
    public StackWriteOptions and(StackWriteOptions other) {
        return new StackWriteOptions(
                always2D && other.always2D,
                alwaysOneOrThreeChannels && other.alwaysOneOrThreeChannels,
                rgb && other.rgb);
    }

    /**
     * Combines with another {@link StackWriteOptions} by performing a logical <i>or</i> on each
     * field.
     *
     * @param other the other {@link StackWriteOptions} to combine with.
     * @return a newly created {@link StackWriteOptions} where each field is the logical <i>or</i>
     *     of the two inputs
     */
    public StackWriteOptions or(StackWriteOptions other) {
        return new StackWriteOptions(
                always2D || other.always2D,
                alwaysOneOrThreeChannels || other.alwaysOneOrThreeChannels,
                rgb || other.rgb);
    }

    /**
     * Whether to write a stack in RGB mode?
     *
     * @param numberChannels the number of channels in a stack.
     * @return true if the stack should be written as RGB, false otherwise.
     */
    public boolean writeAsRGB(int numberChannels) {
        return rgb && numberChannels == 3;
    }
}
