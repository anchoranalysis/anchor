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

import com.google.common.base.Preconditions;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Converts a channel or voxels into a {@link ImagePlus}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertToImagePlus {

    private static final String IMAGEJ_UNIT_MICRON = "micron";

    /** A multiplication-factor to convert microns to meters */
    private static final int MICRONS_TO_METERS = 1000000;

    /**
     * Creates an {@link ImagePlus} from a {@link VoxelsWrapper}.
     *
     * <p>The default image-resolution (see {@link Resolution#Resolution()} is employed.
     *
     * @param voxels the voxels to be converted
     * @return a newly created image-plus, reusing the input voxel's buffer without copying.
     * @throws ImageJConversionException if the voxels are neither unsigned byte nor unsigned short (the only two supported types)
     */
    public static ImagePlus from(VoxelsWrapper voxels) throws ImageJConversionException {
        Dimensions dimensions = new Dimensions(voxels.any().extent(), Optional.empty());
        ImageStack stack = ImageStackFactory.createSingleChannel(voxels);
        return createImagePlus(stack, dimensions, 1, 1, false);
    }

    /**
     * Creates an {@link ImagePlus} from a {@link Channel}.
     *
     * @param channel the channel to be converted
     * @return a newly created image-plus, reusing the input channels's buffer without copying.
     * @throws ImageJConversionException if the voxels are neither unsigned byte nor unsigned short (the only two supported types) 
     */
    public static ImagePlus from(Channel channel) throws ImageJConversionException {
        return from(channel.voxels());
    }

    /**
     * Creates an {@link ImagePlus} from a {@link Stack}.
     *
     * @param stack the stack of channels to be converted
     * @param makeRGB if true, the stack is assumed to have respectively red, green, blue channels)
     *     and outputted as a RGB-type image, otherwise an interleaved image-stack is created.
     * @return a newly created image-plus, reusing the input channels's buffer without copying.
     * @throws ImageJConversionException if any RGB channel is not unsigned 8-bit
     */
    public static ImagePlus from(Stack stack, boolean makeRGB) throws ImageJConversionException {

        // If we're making an RGB then we need to convert our stack
        ImageStack stackNew = ImageStackFactory.createFromStack(stack, makeRGB);

        boolean makeComposite = !makeRGB && stack.getNumberChannels() != 1;
        ImagePlus imagePlus =
                createImagePlus(
                        stackNew, stack.dimensions(), stack.getNumberChannels(), 1, makeComposite);

        maybeCorrectComposite(stack, imagePlus);

        Preconditions.checkArgument(imagePlus.getNSlices() == stack.extent().z());
        return imagePlus;
    }

    /**
     * Creates an {@link ImagePlus} from <i>one slice</i> of a {@code Voxels<UnsignedByteBuffer>
     * voxels}.
     *
     * @param voxels the voxels from which a slice will be extracted to be converted
     * @param sliceIndex slice-index (z coordinate) to extract
     * @param name the name to use in the image-plus
     * @return a newly created image-plus, reusing the input channels's buffer without copying.
     */
    public static ImagePlus fromSlice(
            Voxels<UnsignedByteBuffer> voxels, int sliceIndex, String name) {
        ImageProcessor processor = ConvertToImageProcessor.fromByte(voxels.slices(), sliceIndex);
        return new ImagePlus(name, processor);
    }

    private static ImagePlus createImagePlus(
            ImageStack stack,
            Dimensions dimensions,
            int numberChannels,
            int numberFrames,
            boolean makeComposite) {

        CompositeFactory composite = new CompositeFactory(stack, dimensions.z(), numberFrames);

        // If we're making an RGB then we need to convert our stack
        ImagePlus image = composite.create(numberChannels, makeComposite);
        dimensions
                .resolution()
                .ifPresent(resolution -> configureCalibration(image.getCalibration(), resolution));

        if (numberChannels == 1) {
            image.setDisplayMode(IJ.GRAYSCALE);
        }

        checkNumberSlices(image, dimensions);

        return image;
    }

    private static void configureCalibration(Calibration calibration, Resolution resolution) {
        calibration.setXUnit(IMAGEJ_UNIT_MICRON);
        calibration.setYUnit(IMAGEJ_UNIT_MICRON);
        calibration.setZUnit(IMAGEJ_UNIT_MICRON);
        calibration.pixelWidth = resolution.x() * MICRONS_TO_METERS;
        calibration.pixelHeight = resolution.y() * MICRONS_TO_METERS;
        calibration.pixelDepth = resolution.z() * MICRONS_TO_METERS;
    }

    /** Checks that the number of slices is what is expected from the dimensions */
    private static void checkNumberSlices(ImagePlus image, Dimensions dimensions) {
        if (image.getNSlices() != dimensions.z()) {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "Number of slices in imagePlus (%d) is not equal to z-slices in scene (%d)",
                            image.getNSlices(), dimensions.z()));
        }
    }

    /** Avoids IMP being set to composite mode, if it's a single channel stack */
    private static void maybeCorrectComposite(Stack stack, ImagePlus imp) {
        if (stack.getNumberChannels() == 1 && imp instanceof CompositeImage) {
            ((CompositeImage) imp).setMode(IJ.GRAYSCALE);
        }
    }
}
