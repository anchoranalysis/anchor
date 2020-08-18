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

package org.anchoranalysis.image.convert;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByte;
import org.anchoranalysis.image.voxel.datatype.UnsignedShort;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.pixelsforslice.PixelsForSlice;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IJWrap {

    private static final String IMAGEJ_UNIT_MICRON = "micron";
    private static final String IMAGEJ_IMAGE_NAME = "imagename";

    /** A multiplication-factor to convert microns to meters */
    private static final int MICRONS_TO_METERS = 1000000;

    private static final VoxelDataType DATA_TYPE_BYTE = UnsignedByte.INSTANCE;
    private static final VoxelDataType DATA_TYPE_SHORT = UnsignedShort.INSTANCE;

    public static Channel chnlFromImageStackByte(
            ImageStack imageStack, ImageResolution res, ChannelFactorySingleType factory) {

        ImageDimensions dimensions =
                new ImageDimensions(
                        new Extent(
                                imageStack.getWidth(),
                                imageStack.getHeight(),
                                imageStack.getSize()),
                        res);

        Channel chnlOut = factory.createEmptyUninitialised(dimensions);

        Voxels<ByteBuffer> voxelsOut = chnlOut.voxels().asByte();
        copyImageStackIntoVoxelsByte(imageStack, voxelsOut);
        return chnlOut;
    }

    public static Channel chnlFromImagePlus(ImagePlus imagePlus, ImageResolution res) {

        ChannelFactory factory = ChannelFactory.instance();

        ImageDimensions dimensions =
                new ImageDimensions(
                        new Extent(
                                imagePlus.getWidth(),
                                imagePlus.getHeight(),
                                imagePlus.getStackSize()),
                        res);

        if (imagePlus.getType() == ImagePlus.GRAY8) {
            return chnlFromImagePlusByte(
                    imagePlus, dimensions, factory.get(UnsignedByte.INSTANCE));
        } else if (imagePlus.getType() == ImagePlus.GRAY16) {
            return chnlFromImagePlusShort(
                    imagePlus, dimensions, factory.get(UnsignedShort.INSTANCE));
        } else {
            throw new IncorrectVoxelDataTypeException(
                    "Only unsigned-8 and unsigned 16bit supported");
        }
    }

    public static VoxelsWrapper voxelsFromImagePlus(ImagePlus imagePlus) {

        if (imagePlus.getType() == ImagePlus.GRAY8) {
            return new VoxelsWrapper(voxelsFromImagePlusByte(imagePlus));
        } else if (imagePlus.getType() == ImagePlus.GRAY16) {
            return new VoxelsWrapper(voxelsFromImagePlusShort(imagePlus));
        } else {
            throw new IncorrectVoxelDataTypeException(
                    "Only unsigned-8 and unsigned 16bit supported");
        }
    }

    public static Voxels<ByteBuffer> voxelsFromImagePlusByte(ImagePlus imagePlus) {
        Voxels<ByteBuffer> voxelsOut =
                VoxelsFactory.getByte()
                        .createInitialized(
                                new Extent(
                                        imagePlus.getWidth(),
                                        imagePlus.getHeight(),
                                        imagePlus.getZ()));
        copyImageStackIntoVoxelsByte(imagePlus.getImageStack(), voxelsOut);
        return voxelsOut;
    }

    public static Voxels<ShortBuffer> voxelsFromImagePlusShort(ImagePlus imagePlus) {
        Voxels<ShortBuffer> voxelsOut =
                VoxelsFactory.getShort()
                        .createInitialized(
                                new Extent(
                                        imagePlus.getWidth(),
                                        imagePlus.getHeight(),
                                        imagePlus.getZ()));
        copyImageStackIntoVoxelsShort(imagePlus.getImageStack(), voxelsOut);
        return voxelsOut;
    }

    public static ImageProcessor imageProcessor(VoxelsWrapper voxels, int z) {

        if (voxels.any().extent().volumeXY() != voxels.any().sliceBuffer(z).capacity()) {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "Extent volume (%d) and buffer-capacity (%d) are not equal",
                            voxels.any().extent().volumeXY(),
                            voxels.any().sliceBuffer(z).capacity()));
        }

        if (voxels.getVoxelDataType().equals(DATA_TYPE_BYTE)) {
            return imageProcessorByte(voxels.asByte().slices(), z);
        } else if (voxels.getVoxelDataType().equals(DATA_TYPE_SHORT)) {
            return imageProcessorShort(voxels.asShort().slices(), z);
        } else {
            throw new IncorrectVoxelDataTypeException(
                    "Only byte or short data types are supported");
        }
    }

    public static ImageProcessor imageProcessorByte(PixelsForSlice<ByteBuffer> planeAccess, int z) {
        Extent e = planeAccess.extent();
        return new ByteProcessor(e.x(), e.y(), planeAccess.slice(z).buffer().array(), null);
    }

    public static ImageProcessor imageProcessorShort(
            PixelsForSlice<ShortBuffer> planeAccess, int z) {
        Extent extent = planeAccess.extent();
        return new ShortProcessor(
                extent.x(), extent.y(), planeAccess.slice(z).buffer().array(), null);
    }

    public static ImageProcessor imageProcessorByte(VoxelBuffer<ByteBuffer> voxels, Extent extent) {
        return new ByteProcessor(extent.x(), extent.y(), voxels.buffer().array(), null);
    }

    public static ImageProcessor imageProcessorShort(
            VoxelBuffer<ShortBuffer> voxels, Extent extent) {
        return new ShortProcessor(extent.x(), extent.y(), voxels.buffer().array(), null);
    }

    public static ImagePlus createImagePlus(VoxelsWrapper voxels) {

        ImageStack stackNew = createStackForVoxels(voxels);
        return createImagePlus(
                stackNew,
                new ImageDimensions(voxels.any().extent(), new ImageResolution()),
                1,
                1,
                false);
    }

    public static ImagePlus createImagePlus(Channel chnl) {
        Stack stack = new Stack(chnl);
        return createImagePlus(stack, false);
    }

    public static ImagePlus createImagePlus(Stack stack, boolean makeRGB) {

        ImageDimensions dimensions = stack.getChannel(0).dimensions();

        // If we're making an RGB then we need to convert our stack

        ImageStack stackNew = null;
        if (makeRGB) {
            stackNew = createColorProcessorStack(new RGBStack((Stack) stack));
        } else {
            stackNew = createInterleavedStack(dimensions.extent(), stack);
        }

        ImagePlus imp =
                createImagePlus(stackNew, dimensions, stack.getNumberChannels(), 1, !makeRGB);

        maybeCorrectComposite(stack, imp);

        assert (imp.getNSlices() == dimensions.z());
        return imp;
    }

    public static ImagePlus createImagePlus(
            ImageStack stack,
            ImageDimensions dimensions,
            int numberChannels,
            int numberFrames,
            boolean makeComposite) {

        // If we're making an RGB then we need to convert our stack
        ImagePlus imp = null;
        if (makeComposite) {
            imp =
                    createCompositeImagePlus(
                            stack, numberChannels, dimensions.z(), numberFrames, IMAGEJ_IMAGE_NAME);
        } else {
            imp =
                    createNonCompositeImagePlus(
                            stack, 1, dimensions.z(), numberFrames, IMAGEJ_IMAGE_NAME);
        }

        imp.getCalibration().setXUnit(IMAGEJ_UNIT_MICRON);
        imp.getCalibration().setYUnit(IMAGEJ_UNIT_MICRON);
        imp.getCalibration().setZUnit(IMAGEJ_UNIT_MICRON);
        imp.getCalibration().pixelWidth = dimensions.resolution().x() * MICRONS_TO_METERS;
        imp.getCalibration().pixelHeight = dimensions.resolution().y() * MICRONS_TO_METERS;
        imp.getCalibration().pixelDepth = dimensions.resolution().z() * MICRONS_TO_METERS;

        if (imp.getNSlices() != dimensions.z()) {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "Number of slices in imagePlus (%d) is not equal to z-slices in scene (%d)",
                            imp.getNSlices(), dimensions.z()));
        }

        return imp;
    }

    // Creates an ImageJ colour processor stack (RGB in one processor) from an existing stack of
    // three
    // separate RGB channels
    public static ImageStack createColorProcessorStack(RGBStack stack) {

        ImageDimensions dimensions = stack.channelAt(0).dimensions();

        ImageStack stackNew = new ImageStack(dimensions.x(), dimensions.y());

        int srcSliceNum = 0;

        Voxels<ByteBuffer> voxelsRed = extractSliceAsByte(stack, srcSliceNum++);
        Voxels<ByteBuffer> voxelsGreen = extractSliceAsByte(stack, srcSliceNum++);
        Voxels<ByteBuffer> voxelsBlue = extractSliceAsByte(stack, srcSliceNum);

        for (int z = 0; z < dimensions.z(); z++) {
            ColorProcessor cp = new ColorProcessor(dimensions.x(), dimensions.y());

            byte[] redPixels = extractSliceAsArray(voxelsRed, z);
            byte[] greenPixels = extractSliceAsArray(voxelsGreen, z);
            byte[] bluePixels = extractSliceAsArray(voxelsBlue, z);

            cp.setRGB(redPixels, greenPixels, bluePixels);
            stackNew.addSlice(String.valueOf(z), cp);
        }

        return stackNew;
    }

    public static VoxelBuffer<ByteBuffer> voxelBufferFromImageProcessorByte(ImageProcessor ip) {
        byte[] arr = (byte[]) ip.getPixels();
        return VoxelBufferByte.wrap(arr);
    }

    public static VoxelBuffer<ShortBuffer> voxelBufferFromImageProcessorShort(ImageProcessor ip) {
        short[] arr = (short[]) ip.getPixels();
        return VoxelBufferShort.wrap(arr);
    }

    private static Channel chnlFromImagePlusByte(
            ImagePlus imagePlus, ImageDimensions dimensions, ChannelFactorySingleType factory) {

        Channel chnlOut = factory.createEmptyUninitialised(dimensions);
        Voxels<ByteBuffer> voxelsOut = chnlOut.voxels().asByte();

        for (int z = 0; z < chnlOut.dimensions().z(); z++) {

            ImageProcessor ip = imagePlus.getImageStack().getProcessor(z + 1);
            byte[] arr = (byte[]) ip.getPixels();
            voxelsOut.replaceSlice(z, VoxelBufferByte.wrap(arr));
        }
        return chnlOut;
    }

    private static void maybeCorrectComposite(Stack stack, ImagePlus imp) {

        // Avoids IMP being set to composite mode, if it's a single channel stack
        if (stack.getNumberChannels() == 1 && imp instanceof CompositeImage) {
            ((CompositeImage) imp).setMode(IJ.GRAYSCALE);
        }
    }

    private static Channel chnlFromImagePlusShort(
            ImagePlus imagePlus, ImageDimensions dimensions, ChannelFactorySingleType factory) {

        Channel chnlOut = factory.createEmptyUninitialised(dimensions);

        Voxels<ShortBuffer> voxelsOut = chnlOut.voxels().asShort();

        for (int z = 0; z < dimensions.z(); z++) {

            ImageProcessor ip = imagePlus.getImageStack().getProcessor(z + 1);
            short[] arr = (short[]) ip.getPixels();
            voxelsOut.replaceSlice(z, VoxelBufferShort.wrap(arr));
        }
        return chnlOut;
    }

    private static ImagePlus createCompositeImagePlus(
            ImageStack stackNew, int numChnl, int numSlices, int numFrames, String imageName) {
        ImagePlus impNC =
                createNonCompositeImagePlus(stackNew, numChnl, numSlices, numFrames, imageName);
        assert (impNC.getNSlices() == numSlices);
        ImagePlus impOut = new CompositeImage(impNC, CompositeImage.COLOR);

        // The Composite image sometimes sets these wrong, so we force the correct dimensionality
        impOut.setDimensions(numChnl, numSlices, numFrames);
        return impOut;
    }

    // Create an interleaved stack of images
    private static ImageStack createInterleavedStack(Extent e, Stack stack) {

        ImageStack stackNew = new ImageStack(e.x(), e.y());

        for (int z = 0; z < e.z(); z++) {

            for (int c = 0; c < stack.getNumberChannels(); c++) {
                Channel chnl = stack.getChannel(c);
                VoxelsWrapper voxels = chnl.voxels();

                ImageProcessor ip = IJWrap.imageProcessor(voxels, z);
                stackNew.addSlice(String.valueOf(z), ip);
            }
        }

        return stackNew;
    }

    private static void copyImageStackIntoVoxelsByte(
            ImageStack stack, Voxels<ByteBuffer> voxelsOut) {
        for (int z = 0; z < voxelsOut.extent().z(); z++) {
            ImageProcessor ip = stack.getProcessor(z + 1);
            voxelsOut.replaceSlice(z, voxelBufferFromImageProcessorByte(ip));
        }
    }

    private static void copyImageStackIntoVoxelsShort(
            ImageStack stack, Voxels<ShortBuffer> voxelsOut) {
        for (int z = 0; z < voxelsOut.extent().z(); z++) {
            ImageProcessor processor = stack.getProcessor(z + 1);
            voxelsOut.replaceSlice(z, voxelBufferFromImageProcessorShort(processor));
        }
    }

    // Create a stack composed entirely of a single channel
    private static ImageStack createStackForVoxels(VoxelsWrapper voxels) {

        Extent e = voxels.any().extent();
        ImageStack stackNew = new ImageStack(e.x(), e.y());
        for (int z = 0; z < e.z(); z++) {

            ImageProcessor ip = imageProcessor(voxels, z);
            stackNew.addSlice(String.valueOf(z), ip);
        }
        return stackNew;
    }

    private static ImagePlus createNonCompositeImagePlus(
            ImageStack stackNew, int numChnl, int numSlices, int numFrames, String imageName) {
        ImagePlus imp = new ImagePlus();
        imp.setStack(stackNew, numChnl, numSlices, numFrames);
        imp.setTitle(imageName);
        return imp;
    }

    private static Voxels<ByteBuffer> extractSliceAsByte(RGBStack stack, int channelIndex) {
        return stack.channelAt(channelIndex).voxels().asByte();
    }

    private static byte[] extractSliceAsArray(Voxels<ByteBuffer> voxels, int z) {
        return voxels.sliceBuffer(z).array();
    }
}
