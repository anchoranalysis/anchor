package org.anchoranalysis.image.core.stack;

import lombok.Value;

/**
 * Metadata to describe a <a
 * href="https://en.wikipedia.org/wiki/Pyramid_(image_processing)">pyramid-representation</a> of
 * images, if at least one exists in a file.
 *
 * @author Owen Feehan
 */
@Value
public class ImagePyramidMetadata {

    /** The number of distinct resolution-levels that exist in the pyramid. */
    int resolutionCount;
}
