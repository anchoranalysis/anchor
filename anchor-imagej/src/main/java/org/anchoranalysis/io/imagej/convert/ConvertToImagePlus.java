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

import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import com.google.common.base.Preconditions;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Converts a channel or voxels into a {@link ImagePlus}.
 * 
 * @author Owen Feehan
 *
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
     */
    public static ImagePlus from(VoxelsWrapper voxels) {
        Dimensions dimensions = new Dimensions(voxels.any().extent(), new Resolution());
        ImageStack stack = ImageStackFactory.createSingleChannel(voxels);
        return createImagePlus(
                stack, dimensions, 1, 1, false);
    }

    /**
     * Creates an {@link ImagePlus} from a {@link Channel}.
     * 
     * @param channel the channel to be converted
     * @return a newly created image-plus, reusing the input channels's buffer without copying.
     */
    public static ImagePlus from(Channel channel) {
        return from(channel.voxels());
    }

    /**
     * Creates an {@link ImagePlus} from a {@link Stack}.
     * 
     * @param stack the stack of channels to be converted
     * @param makeRGB if true, the stack is assumed to have respectively red, green, blue channels) and outputted as a RGB-type image, otherwise an interleaved image-stack is created.
     * @return a newly created image-plus, reusing the input channels's buffer without copying.
     */
    public static ImagePlus from(Stack stack, boolean makeRGB) {

        // If we're making an RGB then we need to convert our stack
        ImageStack stackNew = ImageStackFactory.createFromStack(stack, makeRGB);

        ImagePlus imagePlus =
                createImagePlus(stackNew, stack.dimensions(), stack.getNumberChannels(), 1, !makeRGB);

        maybeCorrectComposite(stack, imagePlus);

        Preconditions.checkArgument(imagePlus.getNSlices() == stack.extent().z());
        return imagePlus;
    }
    
    /**
     * Creates an {@link ImagePlus} from <i>one slice<</i> of a {@code Voxels<ByteBuffer> voxels}.
     * @param voxels 
     * @param sliceIndex slice-index (z coordinate) to extract
     * @param name the name to use in the image-plus
     * @return a newly created image-plus, reusing the input channels's buffer without copying.
     */
    public static ImagePlus fromSlice( Voxels<ByteBuffer> voxels, int sliceIndex, String name ) {
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
        configureCalibration(image.getCalibration(), dimensions.resolution());

        checkNumberSlices(image, dimensions);

        return image;
    }
    
    private static void configureCalibration( Calibration calibration, Resolution resolution ) {
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
