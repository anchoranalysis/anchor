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

package org.anchoranalysis.image.io.bean.stack.provider;

import java.nio.ShortBuffer;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.provider.Provider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.converter.ChannelConverter;
import org.anchoranalysis.image.channel.converter.ChannelConverterToUnsignedShort;
import org.anchoranalysis.image.channel.converter.ConversionPolicy;
import org.anchoranalysis.image.channel.converter.voxels.ConvertToShortScaleByType;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.generator.raster.StringRasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GenerateString extends StackProvider {

    // START BEANS
    @BeanField @Getter @Setter private StringRasterGenerator stringRasterGenerator;

    @BeanField @Getter @Setter private boolean createShort;

    /* The string is the maximum-value of the image */
    @BeanField @Getter @Setter private Provider<Stack> intensityProvider;

    /** Repeats the generated (2D) string in z, so it's the same z-extent as repeatZProvider */
    @BeanField @Getter @Setter private Provider<Stack> repeatZProvider;
    // END BEANS

    @Override
    public Stack create() throws CreateException {

        Stack label2D = create2D();

        if (repeatZProvider != null) {

            Stack repeatZ = repeatZProvider.create();
            int zHeight = repeatZ.dimensions().z();

            Stack out = new Stack();
            for (Channel channel : label2D) {
                try {
                    out.addChannel(createExpandedChannel(channel, zHeight));
                } catch (IncorrectImageSizeException e) {
                    throw new CreateException(e);
                }
            }
            return out;

        } else {
            return label2D;
        }
    }

    private static int maxValueFromStack(Stack stack) {
        int max = 0;
        for (Channel channel : stack) {
            int channelVal = channel.extract().voxelWithMaxIntensity();
            if (channelVal > max) {
                max = channelVal;
            }
        }
        return max;
    }

    private Stack create2D() throws CreateException {
        try {
            Stack stack = stringRasterGenerator.generateStack();

            if (createShort) {
                ChannelConverter<ShortBuffer> cc =
                        new ChannelConverterToUnsignedShort(new ConvertToShortScaleByType());

                stack = cc.convert(stack, ConversionPolicy.CHANGE_EXISTING_CHANNEL);
            }

            if (intensityProvider != null) {
                int maxTypeValue =
                        createShort
                                ? UnsignedShortVoxelType.MAX_VALUE_INT
                                : UnsignedByteVoxelType.MAX_VALUE_INT;

                Stack stackIntensity = intensityProvider.create();
                double maxValue = maxValueFromStack(stackIntensity);
                double mult = maxValue / maxTypeValue;

                for (Channel channel : stack) {
                    channel.arithmetic().multiplyBy(mult);
                }
            }

            return stack;
        } catch (OutputWriteFailedException e) {
            throw new CreateException(e);
        }
    }

    private Channel createExpandedChannel(Channel channel, int zHeight) {
        assert (channel.dimensions().z() == 1);

        BoundingBox boxSrc = new BoundingBox(channel.dimensions());
        BoundingBox boxDest = boxSrc;

        Channel channelNew = emptyChannelWithChangedZ(channel, zHeight);
        for (int z = 0; z < zHeight; z++) {

            // Adjust dfestination box
            boxDest = boxDest.shiftToZ(z);

            channel.voxels().copyVoxelsTo(boxSrc, channelNew.voxels(), boxDest);
        }
        return channelNew;
    }

    private Channel emptyChannelWithChangedZ(Channel channel, int zToAssign) {
        Dimensions dimensionsChangedZ = channel.dimensions().duplicateChangeZ(zToAssign);
        return ChannelFactory.instance().create(dimensionsChangedZ, channel.getVoxelDataType());
    }
}