package org.anchoranalysis.image.core.stack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.system.path.ExtensionUtilities;

/**
 * Timestamps and other metadata associated with an image file-path, but not with the file's
 * contents.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ImageFileAttributes {

    /** The path on the file-system. */
    private Path path;

    /** The <i>creation</i> timestamp on the file the image was loaded from. */
    @Getter private Date creationTime;

    /** The <i>last modified</i> timestamp on the file the image was loaded from. */
    @Getter private Date modificationTime;

    /**
     * Reads {@link ImageFileAttributes} from a path.
     *
     * @param path the path.
     * @return newly created {@link ImageFileAttributes}.
     * @throws IOException if the timestamps cannot be read.
     */
    public static ImageFileAttributes fromPath(Path path) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
        return new ImageFileAttributes(
                path,
                convertToDate(attributes.creationTime()),
                convertToDate(attributes.lastModifiedTime()));
    }

    /**
     * The file extension of the file-path.
     *
     * @return the extension, always in lower-case.
     */
    public Optional<String> extension() {
        return ExtensionUtilities.extractExtension(path).map(String::toLowerCase);
    }

    /** Converts from a {@link FileTime} to a {@link Date}. */
    private static Date convertToDate(FileTime time) {
        return new Date(time.toMillis());
    }
}
