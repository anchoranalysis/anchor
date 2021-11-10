package org.anchoranalysis.io.bioformats.metadata;

import java.io.IOException;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.stack.ImageFileAttributes;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.input.ImageTimestampsAttributes;

/**
 * Derives {@link ImageTimestampsAttributes} from a file, additionally checking for EXIF metadata.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageTimestampsAttributesFactory {

    /**
     * Reads {@link ImageTimestampsAttributes} from a path, trying to infer the acquisition-date
     * from metadata headers.
     *
     * <p>For JPEG or TIFF files, EXIF data is searched for to find an acquisition-date.
     *
     * @param path the path.
     * @return newly created {@link ImageFileAttributes}.
     * @throws ImageIOException if {@link ImageTimestampsAttributes} cannot be established.
     */
    public static ImageTimestampsAttributes fromPath(Path path) throws ImageIOException {
        try {
            return new ImageTimestampsAttributes(
                    ImageFileAttributes.fromPath(path),
                    AcquisitionDateReader.readAcquisitionDate(path));
        } catch (IOException e) {
            throw new ImageIOException("Failed to read file-attributes for " + path, e);
        }
    }
}
