package org.anchoranalysis.image.io.stack.input;

import java.util.Date;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.image.core.stack.ImageFileAttributes;
import org.anchoranalysis.image.core.stack.ImageMetadata;

/**
 * This combines {@link ImageFileAttributes} plus a timestamp for image-acqusition.
 *
 * <p>It is intended to encapsulate all the timestamps relevant for {@link ImageMetadata} plus any
 * additional metadata from {@link ImageFileAttributes} (e.g. the extension).
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class ImageTimestampsAttributes {

    /**
     * Timestamps and other metadata associated with an image file-path, but not with the file's
     * contents.
     */
    private final ImageFileAttributes attributes;

    /**
     * A timestamp, if available, of when the image was first physically created by the
     * camera/device.
     */
    private final Optional<Date> acqusitionTime;
}
