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

import com.google.common.base.Functions;
import io.vavr.control.Either;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.OperationFailedRuntimeException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A stack of channels used as context to calculate features (or calculating <i>energy</i> more
 * broadly).
 *
 * <p>The stack can have 0 channels, in which case, explicit dimensions must be set.
 *
 * @author Owen Feehan
 */
public class EnergyStackWithoutParameters {

    /** Either stack to delegate or dimensions (as they cannot be inferred from a stack) */
    private final Either<Dimensions, Stack> container;

    /**
     * Create a energy-stack comprised of a single channel
     *
     * @param channel
     */
    public EnergyStackWithoutParameters(Channel channel) {
        this.container = Either.right(new Stack(channel));
    }

    /**
     * Create a energy-stack comprised of all channels from a stack
     *
     * @param stack the stack which is reused as the energy-stack (i.e. it is not duplicated)
     */
    public EnergyStackWithoutParameters(Stack stack) {
        this.container = Either.right(stack);
    }

    /**
     * Create a energy-stack with no channels - but with associated dimensions.
     *
     * @param dimensions the dimensions.
     */
    public EnergyStackWithoutParameters(Dimensions dimensions) {
        this.container = Either.left(dimensions);
    }

    /**
     * The number of channels in the stack.
     *
     * @return the number of channels.
     */
    public final int getNumberChannels() {
        return container.map(Stack::getNumberChannels).getOrElseGet(dimensions -> 0);
    }

    /**
     * The dimensions of all channels in the stack.
     *
     * @return the dimensions.
     */
    public Dimensions dimensions() {
        return container.map(Stack::dimensions).getOrElseGet(Functions.identity());
    }

    /**
     * The width and height and depth of all {@link Channel}s in the stack.
     *
     * @return the size, in three dimensions.
     */
    public Extent extent() {
        return dimensions().extent();
    }

    /**
     * Returns the channel at a particular position in the stack.
     *
     * @param index the index (zero-indexed).
     * @return the respective channel.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >=
     *     size()})
     */
    public final Channel getChannel(int index) {

        if (container.isLeft()) {
            throwInvalidIndexException(0, index);
        }

        if (index >= container.get().getNumberChannels()) {
            throwInvalidIndexException(container.get().getNumberChannels(), index);
        }

        return container.get().getChannel(index);
    }

    /**
     * Derive a {@link Stack} representation containing the identical channels to the current
     * instance.
     *
     * @return a newly created {@link Stack}, but reusing the current {@link Channel}s.
     */
    public Stack asStack() {
        return container.getOrElse(Stack::new);
    }

    /**
     * Extract a particular z-slice from the {@link EnergyStackWithoutParameters} as a new stack.
     *
     * @param z the index in the Z-dimension of the slice to extract.
     * @return the extracted slice, as a new {@link EnergyStackWithoutParameters} but reusing the
     *     existing voxels.
     * @throws OperationFailedException if no channels exist in the energy-stack.
     */
    public EnergyStackWithoutParameters extractSlice(int z) throws OperationFailedException {

        if (container.isLeft()) {
            throw new OperationFailedException(
                    "No slice can be extracted, as no channels existing in the energy-stack");
        }

        return new EnergyStackWithoutParameters(container.get().extractSlice(z));
    }

    private void throwInvalidIndexException(int numberChannels, int index) {
        throw new OperationFailedRuntimeException(
                String.format(
                        "There are %d channels in the energy-stack. Cannot access index %d.",
                        numberChannels, index));
    }
}
