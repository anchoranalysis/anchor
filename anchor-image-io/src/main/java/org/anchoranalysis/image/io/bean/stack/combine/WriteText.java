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

package org.anchoranalysis.image.io.bean.stack.combine;

import java.awt.image.BufferedImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.spatial.SizeXY;
import org.anchoranalysis.image.core.bufferedimage.CreateStackFromBufferedImage;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.ChannelConverter;
import org.anchoranalysis.image.core.channel.convert.ConversionPolicy;
import org.anchoranalysis.image.core.channel.convert.ToUnsignedShort;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.image.io.stack.output.generator.RasterGeneratorSelectFormat;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.convert.ToShortScaleByType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;

@NoArgsConstructor
public class WriteText extends StackProvider {

    // START BEAN PROPERTIES
    /** Text to draw on an image. */
    @BeanField @Getter @Setter private String text = "text";

    @BeanField @Getter @Setter private TextStyle style;

    /** Explicit size of the image the string is draw on. */
    @BeanField @Getter @Setter private SizeXY size;

    @BeanField @Getter @Setter private boolean createShort;

    /** The string is printed using the maximum-value intensity-value of the image. */
    @BeanField @Getter @Setter private Provider<Stack> intensityProvider;

    /**
     * Repeats the generated (2D) string in z, so it's the same z-size as {@code intensityProvider}
     */
    @BeanField @Getter @Setter private Provider<Stack> repeatZProvider;
    // END BEAN PROPERITES

    public WriteText(String text) {
        this.text = text;
    }

    @Override
    public Stack get() throws ProvisionFailedException {

        Stack label2D = create2D();

        if (repeatZProvider != null) {

            Stack repeatZ = repeatZProvider.get();
            int zHeight = repeatZ.dimensions().z();

            Stack out = new Stack();
            for (Channel channel : label2D) {
                try {
                    out.addChannel(createExpandedChannel(channel, zHeight));
                } catch (IncorrectImageSizeException e) {
                    throw new ProvisionFailedException(e);
                }
            }
            return out;

        } else {
            return label2D;
        }
    }

    private static long maxValueFromStack(Stack stack) {
        long max = 0;
        for (Channel channel : stack) {
            long channelVal = channel.extract().voxelWithMaxIntensity();
            if (channelVal > max) {
                max = channelVal;
            }
        }
        return max;
    }

    private Stack create2D() throws ProvisionFailedException {
        try {
            Stack stack = new RasterizedTextGenerator(size, style).transform(text);

            if (createShort) {
                ChannelConverter<UnsignedShortBuffer> conveter =
                        new ToUnsignedShort(new ToShortScaleByType());

                stack = conveter.convert(stack, ConversionPolicy.CHANGE_EXISTING_CHANNEL);
            }

            if (intensityProvider != null) {
                int maxTypeValue =
                        createShort
                                ? UnsignedShortVoxelType.MAX_VALUE_INT
                                : UnsignedByteVoxelType.MAX_VALUE_INT;

                Stack stackIntensity = intensityProvider.get();
                double maxValue = maxValueFromStack(stackIntensity);
                double mult = maxValue / maxTypeValue;

                for (Channel channel : stack) {
                    channel.arithmetic().multiplyBy(mult);
                }
            }

            return stack;
        } catch (OutputWriteFailedException e) {
            throw new ProvisionFailedException(e);
        }
    }

    private Channel createExpandedChannel(Channel channel, int zHeight) {
        assert (channel.dimensions().z() == 1);

        BoundingBox boxSrc = new BoundingBox(channel.extent());
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

    /**
     * Creates an image with text matching the style/size specified in this bean.
     *
     * @author Owen Feehan
     */
    @AllArgsConstructor
    private static class RasterizedTextGenerator extends RasterGeneratorSelectFormat<String> {

        private final SizeXY size;

        private final TextStyle style;

        @Override
        public Stack transform(String element) throws OutputWriteFailedException {

            BufferedImage bufferedImage =
                    new BufferedImage(
                            size.getWidth(), size.getHeight(), BufferedImage.TYPE_INT_RGB);

            style.drawText(element, bufferedImage, size.asExtent());

            try {
                return CreateStackFromBufferedImage.createFrom(bufferedImage);
            } catch (OperationFailedException e) {
                throw new OutputWriteFailedException(e);
            }
        }

        @Override
        public StackWriteAttributes guaranteedImageAttributes() {
            return StackWriteAttributesFactory.rgbMaybe3D(false);
        }
    }
}
