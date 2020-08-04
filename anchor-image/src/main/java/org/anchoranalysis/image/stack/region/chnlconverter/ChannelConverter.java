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

package org.anchoranalysis.image.stack.region.chnlconverter;

import java.nio.Buffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Converts a channel from one type to another
 *
 * @author Owen Feehan
 * @param <T> type to convert to (destination-type)
 */
@AllArgsConstructor
public abstract class ChannelConverter<T extends Buffer> {

    private VoxelDataType dataTypeTarget;
    @Getter private VoxelBoxConverter<T> voxelsConverter;
    private VoxelBoxFactoryTypeBound<T> voxelBoxFactory;

    public Stack convert(Stack stackIn, ConversionPolicy changeExisting) {
        Stack stackOut = new Stack();

        for (Channel chnl : stackIn) {
            try {
                stackOut.addChannel(convert(chnl, changeExisting));
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
    public Channel convert(Channel chnlIn, ConversionPolicy changeExisting) {

        // Nothing to do
        if (chnlIn.getVoxelDataType().equals(dataTypeTarget)
                && changeExisting != ConversionPolicy.ALWAYS_NEW) {
            return chnlIn;
        }

        Channel chnlOut;
        VoxelBox<T> voxelsOut;

        if (changeExisting == ConversionPolicy.CHANGE_EXISTING_CHANNEL) {
            chnlOut = chnlIn;
            // We need to create a new voxel buffer
            voxelsOut = voxelBoxFactory.create(chnlIn.getDimensions().getExtent());
        } else {
            chnlOut =
                    ChannelFactory.instance()
                            .createEmptyUninitialised(chnlIn.getDimensions(), dataTypeTarget);
            voxelsOut = (VoxelBox<T>) chnlOut.voxels().match(dataTypeTarget);
        }

        voxelsConverter.convertFrom(chnlIn.voxels(), voxelsOut);

        if (changeExisting == ConversionPolicy.CHANGE_EXISTING_CHANNEL) {
            VoxelBoxWrapper wrapper = new VoxelBoxWrapper(voxelsOut);
            chnlOut.replaceVoxelBox(wrapper);
        }

        return chnlOut;
    }
}
