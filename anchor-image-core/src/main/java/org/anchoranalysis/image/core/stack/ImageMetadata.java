/*-
 * #%L
 * anchor-image-core
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.core.stack;

import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.core.dimensions.Dimensions;

/**
 * Information about an image, but not about the intensity or content of image voxels.
 *
 * <p>The metadata always assumes a single batch of identically-sized images. If more than one
 * series of images exist, these are considered as having separate metadata.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ImageMetadata {

    /** The dimensions of the image. */
    @Getter private Dimensions dimensions;

    /** The number of channels in the image. */
    @Getter private int numberChannels;

    /**
     * The number of frames (separate images representing different points in a time-series) in the
     * image.
     */
    @Getter private int numberFrames;

    /**
     * The number of separate images stored in the file.
     *
     * <p>Pyramid image files should ideally attempt to present themselves as a single element in
     * the series, but this depends on the driver.
     */
    @Getter private int numberSeries;

    /** Whether the image is RGB or not. */
    @Getter private boolean rgb;

    /** The number of bits in memory to describe each voxel's intensity, per channel. */
    @Getter private int bitDepthPerChannel;

    /** Attributes associated with the file on the file-system. */
    @Getter private ImageFileAttributes fileAttributes;

    /**
     * A timestamp, if available, of when the image was first physically created by the
     * camera/device.
     */
    @Getter private Optional<ZonedDateTime> acquisitionTime;

    /** Metadata describing the geographic location where the image was captured. */
    @Getter private Optional<ImageLocation> location;

    /**
     * Metadata to describe an image pyramid, if it exists.
     *
     * <p>If multiple pyramids exist, the metadata items are extracted for the pyramid considered
     * most important.
     */
    @Getter private Optional<ImagePyramidMetadata> pyramid;
}
