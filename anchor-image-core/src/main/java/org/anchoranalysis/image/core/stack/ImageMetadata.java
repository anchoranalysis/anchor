package org.anchoranalysis.image.core.stack;

import java.util.Date;
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

    /** Whether the image is RGB or not. */
    @Getter private boolean rgb;

    /** The number of bits in memory to describe each voxel's intensity, per channel. */
    @Getter private int bitDepthPerChannel;

    /** Attributes associated with the file on the filesystem. */
    @Getter private ImageFileAttributes fileAttributes;

    /**
     * A timestamp, if available, of when the image was first physically created by the
     * camera/device.
     */
    @Getter private Optional<Date> acqusitionTime;
}
