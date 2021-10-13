/*-
 * #%L
 * anchor-imagej
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
package org.anchoranalysis.io.imagej.convert;

import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.functional.checked.CheckedIntFunction;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Routines for creating a {@link ImageStack}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ImageStackFactory {

    /**
     * Converts a {@link Stack} (as used in Anchor) into a {@link ImageStack} (for ImageJ).
     *
     * @param stack the stack of channels to be converted into an {@link ImageStack} for ImageJ
     * @param makeRGB if true, the stack is assumed to have respectively red, green, blue channels)
     *     and outputted as a RGB-type image, otherwise an interleaved image-stack is created.
     * @return a newly created {@link ImageStack}
     * @throws ImageJConversionException if any RGB channel is not unsigned 8-bit
     */
    public static ImageStack createFromStack(Stack stack, boolean makeRGB)
            throws ImageJConversionException {
        if (makeRGB) {
            return createRGB(new RGBStack(stack));
        } else {
            return createInterleaved(stack);
        }
    }

    /**
     * Creates an ImageJ-RGB stack (i.e. using {@link ColorProcessor}.
     *
     * @param stack an rgb-stack to convert
     * @return a newly created {@link ImageStack}
     * @throws ImageJConversionException if any RGB channel is not unsigned 8-bit
     */
    public static ImageStack createRGB(RGBStack stack) throws ImageJConversionException {
        if (!stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE)) {
            throw new ImageJConversionException(
                    "Only unsigned 8-bit channels are supported for an ImageJ RGB image");
        }
        Extent extent = stack.channelAt(0).extent();

        int channelIndex = 0;

        RGBVoxels voxels =
                new RGBVoxels(
                        extractChannel(stack, channelIndex++),
                        extractChannel(stack, channelIndex++),
                        extractChannel(stack, channelIndex));

        return createFromProcessorsStream(
                extent, z -> Stream.of(voxels.createColorProcessor(extent, z)));
    }

    /**
     * Create a {@link ImageStack} with interleaved channels from a {@link Stack}
     *
     * @param stack the channels that will be interleaved
     * @return a newly created {@link ImageStack}
     * @throws ImageJConversionException if the voxels are neither unsigned byte nor unsigned short
     *     (the only two supported types)
     */
    public static ImageStack createInterleaved(Stack stack) throws ImageJConversionException {
        return createFromVoxelsStream(
                stack.extent(),
                z ->
                        IntStream.range(0, stack.getNumberChannels())
                                .mapToObj(index -> stack.getChannel(index).voxels()));
    }

    /**
     * Create an {@link ImageStack} composed entirely of a single channel
     *
     * @param voxels the voxels corresponding to the single-channel
     * @return a newly created {@link ImageStack}
     * @throws ImageJConversionException if the voxels are neither unsigned byte nor unsigned short
     *     (the only two supported types)
     */
    public static ImageStack createSingleChannel(VoxelsWrapper voxels)
            throws ImageJConversionException {
        return createFromVoxelsStream(voxels.extent(), z -> Stream.of(voxels));
    }

    /**
     * Creates a new {@link ImageStack} of a certain size with a function that creates one or more
     * {@link VoxelsWrapper} for each slice-index
     *
     * @param extent the extent of the stack to create
     * @param createSlice creates one or more {@link VoxelsWrapper} to place into the {@link
     *     ImageStack} for a given slice-index
     * @return a newly created {@link ImageStack} with slices constructed from {@code createSlice}
     *     applied to all slice-indices
     * @throws ImageJConversionException if the voxels are neither unsigned byte nor unsigned short
     *     (the only two supported types)
     */
    private static ImageStack createFromVoxelsStream(
            Extent extent, IntFunction<Stream<VoxelsWrapper>> createSlice)
            throws ImageJConversionException {
        return createFromProcessorsStream(
                extent,
                z ->
                        CheckedStream.map(
                                createSlice.apply(z),
                                ImageJConversionException.class,
                                voxels -> ConvertToImageProcessor.from(voxels, z)));
    }

    /**
     * Creates a new {@link ImageStack} of a certain size with a function that creates one or more
     * {@link ImageProcessor} for each slice-index
     *
     * @param extent the extent of the stack to create
     * @param createSlice creates one or more {@link ImageProcessor} to place into the {@link
     *     ImageStack} for a given slice-index
     * @return a newly created {@link ImageStack} with slices constructed from {@code createSlice}
     *     applied to all slice-indices
     * @throws ImageJConversionException if the voxels are neither unsigned byte nor unsigned short
     *     (the only two supported types)
     */
    private static ImageStack createFromProcessorsStream(
            Extent extent,
            CheckedIntFunction<Stream<ImageProcessor>, ImageJConversionException> createSlice)
            throws ImageJConversionException {
        ImageStack stack = new ImageStack(extent.x(), extent.y());
        extent.iterateOverZ(z -> addSlices(stack, z, createSlice.apply(z)));
        return stack;
    }

    private static void addSlices(ImageStack stack, int z, Stream<ImageProcessor> slices)
            throws ImageJConversionException {
        try {
            slices.forEach(slice -> stack.addSlice(String.valueOf(z), slice));
        } catch (Exception e) {
            throw new ImageJConversionException(e);
        }
    }

    private static Voxels<UnsignedByteBuffer> extractChannel(RGBStack stack, int channelIndex) {
        return stack.channelAt(channelIndex).voxels().asByte();
    }
}
