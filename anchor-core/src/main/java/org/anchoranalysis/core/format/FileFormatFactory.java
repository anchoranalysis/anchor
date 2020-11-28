package org.anchoranalysis.core.format;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates a {@link FileFormat} from a string.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FileFormatFactory {

    /**
     * Creates a {@link ImageFileFormat} from an identifier string if its equal to
     * any extension associated with the image file formats in {@link ImageFileFormat}.
     * 
     * <p>The case of the identifier is irrelevant.
     * 
     * @param identifier
     * @return
     */
    public static Optional<ImageFileFormat> createImageFormat(String identifier) {
        for(ImageFileFormat format : ImageFileFormat.values()) {
            if (format.matchesIdentifier(identifier)) {
                return Optional.of(format);
            }
        }
        return Optional.empty();
    }
}
