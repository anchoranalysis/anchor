package org.anchoranalysis.image.core.stack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.core.dimensions.Dimensions;

/**
 * Information about an image, but without image voxels.
 *
 * <p>The information should contain:
 *
 * <ol>
 *   <li>Size
 *   <li>Number of channels
 *   <li>Physical voxel size
 * </ol>
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

    /** Whether the image is RGB or not. */
    @Getter private boolean rgb;

    /** The number of bits in memory to describe each voxel's intensity. */
    @Getter private int bitDepth;
    
    /** Timestamps associated with the image. */
    @Getter private ImageFileTimestamps timestamps;
}
