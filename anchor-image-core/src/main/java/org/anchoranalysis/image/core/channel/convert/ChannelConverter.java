/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.channel.convert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.convert.VoxelsConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/**
 * Base class to convert {@link Channel}s from one type to another specific type.
 *
 * @author Owen Feehan
 * @param <T> type to convert to (destination-type)
 */
@AllArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class ChannelConverter<T> {

    /** The voxel data-type to convert each {@link Channel}'s voxels to. */
    private VoxelDataType targetDataType;
    
    /** A converter used to change the {@link Voxels} to {@code targetDataType}. */
    @Getter private VoxelsConverter<T> voxelsConverter;
    
    /** The factory used to create new {@link Voxels}. */
    private VoxelsFactoryTypeBound<T> voxelsFactory;

    /**
     * Like {@link #convert(Channel, ConversionPolicy)} but converts every channel in a {@link Stack}.
     * 
     * @param stack the stack whose channels will be  converted.
     * @param changeExisting if true, the existing channels will be changed in-place, otherwise a new channel will be created.
     * @return a newly created {@link Stack} containing converted versions of each respective channel in {@code stack}.
     */
    public Stack convert(Stack stack, ConversionPolicy changeExisting) {
        Stack out = new Stack();

        for (Channel channel : stack) {
            try {
                out.addChannel(convert(channel, changeExisting));
            } catch (IncorrectImageSizeException e) {
                // Should never happen as sizes are correct in the incoming stack
                assert false;
            }
        }
        return out;
    }

    /**
     * Converts {@code channel} to have voxels with data-type {@code T}.
     *
     * <p>This can occur by either replacing the existing voxels in the channel, or creating a new
     * channel entirely.
     *
     * @param channel channel whose voxels will be converted.
     * @param changeExisting if true, the existing channels will be changed in-place, otherwise a new channel will be created.
     * @return the converted channel, either the existing channel, or a newly-created one, as per above.
     */
    public Channel convert(Channel channel, ConversionPolicy changeExisting) {

        // Nothing to do
        if (channel.getVoxelDataType().equals(targetDataType)
                && changeExisting != ConversionPolicy.ALWAYS_NEW) {
            return channel;
        }

        if (changeExisting == ConversionPolicy.CHANGE_EXISTING_CHANNEL) {
            return convertExisting(channel);
        } else {
            return convertCreateNew(channel);
        }
    }

    /** Convert in-place. */
    private Channel convertExisting(Channel channel) {
        try {
            // We need to create a new voxel buffer
            Voxels<T> voxels = voxelsFactory.createInitialized(channel.dimensions().extent());
            voxelsConverter.copyFrom(channel.voxels(), voxels);
            channel.replaceVoxels(voxels);
        } catch (IncorrectImageSizeException | OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
        return channel;
    }

    /** Convert, creating a new {@link Channel}. */
    private Channel convertCreateNew(Channel channel) {
        Channel out = ChannelFactory.instance().create(channel.dimensions(), targetDataType);
        @SuppressWarnings("unchecked")
        Voxels<T> voxels = (Voxels<T>) out.voxels().checkIdenticalDataType(targetDataType);
        try {
            voxelsConverter.copyFrom(channel.voxels(), voxels);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
        return out;
    }
}
