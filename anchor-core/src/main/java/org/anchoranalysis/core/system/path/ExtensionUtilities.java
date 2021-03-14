package org.anchoranalysis.core.system.path;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.apache.commons.io.FilenameUtils;

/**
 * Extracting a file-extension from a path according to rules.
 *
 * <p>When a path contains only a single period, it is easy to reliably infer the extension.
 *
 * <p>However, some image file formats (e.g. OME-XML and OME-TIFF) have double periods e.g. ome.tiff
 * and ome.xml
 *
 * <p>This class will ordinarily assume an extension is what follows the final period in a filename.
 * Any preceding second period will be treated as part of the file-name.
 *
 * <p>Some hard-coded exceptions exist for the OME file-types, and in these cases the double-period
 * extensions are extracted.
 *
 * <p>This class is intended as a singular centralized method for extracting extensions from
 * filenames in Anchor, so identical rules can be applied in all situations.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtensionUtilities {

    /**
     * Splits the file-name of a path into a base part and an extension part.
     *
     * <p>The rules for how this split occurs are described in the class-level documentation.
     *
     * @param path the path containing the filename to split (the directory components of the path
     *     are ignored)
     * @return a newly-created class describing the split filename
     */
    public static FilenameSplitExtension splitFilename(String path) {
        String baseName = FilenameUtils.getBaseName(path);
        Optional<String> extension = extractExtension(path);
        return new FilenameSplitExtension(baseName, extension);
    }

    /**
     * Extracts the extension from a filename.
     *
     * <p>The rules for what is considered an extension are described in the class-level
     * documentation.
     *
     * @param filename the filename from which an extension will be extracted, or a path (in which
     *     case the directory components are ignored)
     * @return the extension (excluding any leading period), if it exists.
     */
    public static Optional<String> extractExtension(String filename) {
        return OptionalUtilities.create(FilenameUtils.getExtension(filename));
    }

    /**
     * Like {@link #extractExtension(String)} but accepts a {@link Path} instead of a {@link
     * String}.
     *
     * @param path the path to extract an extension for
     * @return the extension (excluding any leading period), if it exists.
     */
    public static Optional<String> extractExtension(Path path) {
        return extractExtension(path.toString());
    }

    /**
     * Appends an (optional) extension to a string, adding in the leading period if the extension is
     * defined.
     *
     * @param pathWithoutExtension a filename or path <b>without an extension and without any
     *     tailing period</b>
     * @param extension an extension, if it exists
     * @return a string that combines {@code pathWithoutExtension} and the {@code extension}
     *     inserting a leading period, if necessary.
     */
    public static String appendExtension(String pathWithoutExtension, Optional<String> extension) {
        if (extension.isPresent()) {
            return pathWithoutExtension + "." + extension.get();
        } else {
            return pathWithoutExtension;
        }
    }

    /**
     * Like {@link ExtensionUtilities#appendExtension(String, Optional)} but accepts and returns a
     * {@link Path}.
     *
     * @param pathWithoutExtension a filename or path <b>without an extension and without any
     *     tailing period</b>
     * @param extension an extension, if it exists
     * @return a string that combines {@code pathWithoutExtension} and the {@code extension}
     *     inserting a leading period, if necessary.
     */
    public static Path appendExtension(Path pathWithoutExtension, Optional<String> extension) {
        return Paths.get(appendExtension(pathWithoutExtension.toString(), extension));
    }

    /**
     * Removes an extension from a filename or path.
     *
     * @param filename the filename or path to remove the extension from
     * @return {@code filename} without the extension and any leading period to the extension
     */
    public static String removeExtension(String filename) {
        return FilenameUtils.removeExtension(filename);
    }

    /**
     * Like {@link #removeExtension(String)} but accepts and returns a {@link Path}.
     *
     * @param path the path to remove the extension from
     * @return {@code path} without the extension and any leading period to the extension
     */
    public static Path removeExtension(Path path) {
        return Paths.get(removeExtension(path.toString()));
    }

    /**
     * Like {@link #removeExtension(String)} but accepts a {@link File}.
     *
     * <p>Note any directory components are ignored! Only the name of the file is returned.
     *
     * @param file a file to remove the extension from
     * @return the name of {@code file} without the extension and any leading period to the
     *     extension..
     */
    public static String removeExtension(File file) {
        return removeExtension(file.getName());
    }
}
