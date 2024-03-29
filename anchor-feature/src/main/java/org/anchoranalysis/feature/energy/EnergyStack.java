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
import org.anchoranalysis.spatial.box.Extent;

/**
 * A {@link EnergyStackWithoutParameters} with associated parameters in a {@link Dictionary}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class EnergyStack {

    private final EnergyStackWithoutParameters delegate;

    /** The associated parameters. */
    @Getter @Setter private Dictionary parameters;

    /**
     * Create from a single {@link Channel}.
     *
     * @param channel the channel.
     */
    public EnergyStack(Channel channel) {
        this(new EnergyStackWithoutParameters(channel));
    }

    /**
     * Create from a {@link EnergyStackWithoutParameters} without any additional parameters.
     *
     * @param energyStack the energy-stack.
     */
    public EnergyStack(EnergyStackWithoutParameters energyStack) {
        this(energyStack, new Dictionary());
    }

    /**
     * Create from a {@link Stack} with associated parameters in a {@link Dictionary}.
     *
     * @param stack the stack.
     * @param dictionary the associated parameters.
     */
    public EnergyStack(Stack stack, Dictionary dictionary) {
        this(new EnergyStackWithoutParameters(stack), dictionary);
    }

    /**
     * Create from a {@link EnergyStack} without any additional parameters.
     *
     * @param stack the stack.
     */
    public EnergyStack(Stack stack) {
        this(new EnergyStackWithoutParameters(stack));
    }

    /**
     * Create a new stack of {@code Dimensions} with zero-voxel values, without any additional
     * parameters.
     *
     * @param dimensions the dimensions.
     */
    public EnergyStack(Dimensions dimensions) {
        this(new EnergyStackWithoutParameters(dimensions));
    }

    /**
     * Extract a particular z-slice from the {@link EnergyStack} as a new stack.
     *
     * @param z the index in the Z-dimension of the slice to extract.
     * @return the extracted slice, as a new {@link EnergyStack} but reusing the existing voxels.
     * @throws OperationFailedException if no channels exist in the energy-stack.
     */
    public EnergyStack extractSlice(int z) throws OperationFailedException {
        return new EnergyStack(delegate.extractSlice(z), parameters);
    }

    /**
     * Does exactly one z-slice exist in the energy stack?
     *
     * @return true iff the number of z-slices is 1.
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
     * The image-resolution associated with the energy-stack.
     *
     * @return the image-resolution.
     */
    public Optional<Resolution> resolution() {
        return dimensions().resolution();
    }

    /**
     * Makes a copy of the {@link EnergyStack} but assigns a new {@link Dictionary}.
     *
     * @param dictionaryToAssign the dictionary.
     * @return a copy of the existing instance, that is otherwise identical, but contains {@code
     *     dictionaryToAssign}.
     */
    public EnergyStack copyChangeDictionary(Dictionary dictionaryToAssign) {
        return new EnergyStack(delegate, dictionaryToAssign);
    }

    /**
     * Returns the channel at a particular position in the stack.
     *
     * @param index the index (zero-indexed).
     * @return the respective channel.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >=
     *     size()}).
     */
    public Channel getChannel(int index) {
        return delegate.getChannel(index);
    }

    /**
     * Derive a {@link Stack} representation containing the identical channels to the current
     * instance.
     *
     * @return a newly created {@link Stack}, but reusing the current {@link Channel}s.
     */
    public Stack asStack() {
        return delegate.asStack();
    }

    /**
     * The width and height and depth of all {@link Channel}s in the stack.
     *
     * @return the size, in three dimensions.
     */
    public Extent extent() {
        return delegate.extent();
    }

    /**
     * The energy-stack without associated parameters.
     *
     * @return a representation of the energy-stack without parameters (not newly created).
     */
    public EnergyStackWithoutParameters withoutParameters() {
        return delegate;
    }

    /**
     * The number of channels in the stack.
     *
     * @return the number of channels.
     */
    public final int getNumberChannels() {
        return delegate.getNumberChannels();
    }
}
