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

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/**
 * Converts an {@link ImagePlus} into a channel or voxels.
 *
 * <p>Voxel buffers are always copied from the ImagePlus not reused. This creates a performance
 * penalty.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertFromImagePlus {

    /**
     * Converts an {@link ImagePlus} to a {@link Channel}.
     *
     * @param imagePlus the image-plus to convert
     * @param resolution image-resolution
     * @return a newly created channel containing a newly created buffer (copied from image-plus)
     */
    public static Channel toChannel(ImagePlus imagePlus, Resolution resolution) {
        return new Channel(toVoxels(imagePlus).any(), resolution);
    }

    /**
     * Converts an {@link ImagePlus} to a {@link VoxelsWrapper}.
     *
     * @param image the image-plus to convert
     * @return newly created voxels-buffer (copied from image-plus)
     */
    public static VoxelsWrapper toVoxels(ImagePlus image) {

        if (image.getType() == ImagePlus.GRAY8) {
            return deriveCopiedVoxels(image, VoxelsFactory.getUnsignedByte(), ConvertToVoxelBuffer::asByte);
        } else if (image.getType() == ImagePlus.GRAY16) {
            return deriveCopiedVoxels(
                    image, VoxelsFactory.getUnsignedShort(), ConvertToVoxelBuffer::asShort);
        } else {
            throw new IncorrectVoxelTypeException("Only unsigned-8 and unsigned 16bit supported");
        }
    }

    private static <T> VoxelsWrapper deriveCopiedVoxels(
            ImagePlus image,
            VoxelsFactoryTypeBound<T> factory,
            Function<ImageProcessor, VoxelBuffer<T>> convertProcessor) {
        Voxels<T> voxels = factory.createInitialized(deriveExtent(image));
        copyStackIntoVoxels(image.getImageStack(), voxels, convertProcessor);
        return new VoxelsWrapper(voxels);
    }

    private static <T> void copyStackIntoVoxels(
            ImageStack source,
            Voxels<T> destination,
            Function<ImageProcessor, VoxelBuffer<T>> convertProcessor) {
        destination
                .extent()
                .iterateOverZ(
                        z -> {
                            ImageProcessor processor = source.getProcessor(z + 1);
                            destination.replaceSlice(z, convertProcessor.apply(processor));
                        });
    }

    private static Extent deriveExtent(ImagePlus image) {
        return new Extent(image.getWidth(), image.getHeight(), image.getStackSize());
    }
}
