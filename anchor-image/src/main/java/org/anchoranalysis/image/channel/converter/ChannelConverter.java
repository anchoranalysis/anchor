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

package org.anchoranalysis.image.channel.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.converter.voxels.VoxelsConverter;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/**
 * Converts a channel from one type to another specific type.
 *
 * @author Owen Feehan
 * @param <T> type to convert to (destination-type)
 */
@AllArgsConstructor
public abstract class ChannelConverter<T> {

    private VoxelDataType dataTypeTarget;
    @Getter private VoxelsConverter<T> voxelsConverter;
    private VoxelsFactoryTypeBound<T> voxelsFactory;

    public Stack convert(Stack stackIn, ConversionPolicy changeExisting) {
        Stack stackOut = new Stack();

        for (Channel channel : stackIn) {
            try {
                stackOut.addChannel(convert(channel, changeExisting));
            } catch (IncorrectImageSizeException e) {
                // Should never happen as sizes are correct in the incoming stack
                assert false;
            }
        }
        return stackOut;
    }

    // If changeExisting is true, the contents of the existing channel will be changed
    // If changeExisting is false, a new channel will be created
    @SuppressWarnings("unchecked")
    public Channel convert(Channel channelIn, ConversionPolicy changeExisting) {

        // Nothing to do
        if (channelIn.getVoxelDataType().equals(dataTypeTarget)
                && changeExisting != ConversionPolicy.ALWAYS_NEW) {
            return channelIn;
        }

        Channel channelOut;
        Voxels<T> voxelsOut;

        if (changeExisting == ConversionPolicy.CHANGE_EXISTING_CHANNEL) {
            channelOut = channelIn;
            // We need to create a new voxel buffer
            voxelsOut = voxelsFactory.createInitialized(channelIn.dimensions().extent());
        } else {
            channelOut =
                    ChannelFactory.instance()
                            .createUninitialised(channelIn.dimensions(), dataTypeTarget);
            voxelsOut = (Voxels<T>) channelOut.voxels().match(dataTypeTarget);
        }

        voxelsConverter.convertFrom(channelIn.voxels(), voxelsOut);

        if (changeExisting == ConversionPolicy.CHANGE_EXISTING_CHANNEL) {
            try {
                channelOut.replaceVoxels(voxelsOut);
            } catch (IncorrectImageSizeException e) {
                throw new AnchorImpossibleSituationException();
            }
        }

        return channelOut;
    }
}
