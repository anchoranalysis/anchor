/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.system.path;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
 * <p>However, some image file formats (e.g. OME-XML and OME-TIFF) have double periods, as specified
 * in {@link #EXCEPTED_DOUBLE_EXTENSIONS}.
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
     * Particular extensions with a double period, that are exceptionally checked for, and treated
     * as a single extension.
     *
     * <p>Otherwise, in the presence of two periods, only the characters after the final period are
     * considered the extension.
     *
     * <p>An explanation of many of the extensions can be found <a
     * href="https://imagej.net/Bio-Formats#Bio-Formats_Exporter">on imagej.net</a>
     *
     * <p>Importantly, note <b>the leading period</b> in each entry in this list.
     */
    public static final List<String> EXCEPTED_DOUBLE_EXTENSIONS =
            Arrays.asList(
                    ".ome.tif",
                    ".ome.tiff",
                    ".ome.xml",
                    ".ome.tf2",
                    ".ome.tf8",
                    ".ome.btf"); // NOSONAR

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
        Optional<String> extension = extractExtension(path);
        if (extension.isPresent()) {
            String baseName = removeSuffix(path, extension.get());
            return new FilenameSplitExtension(baseName, extension);
        } else {
            return new FilenameSplitExtension(path, Optional.empty());
        }
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

        Optional<String> matchesSpecial = endsWithDoubleExtension(filename);
        return OptionalUtilities.orElseGetFlat(
                matchesSpecial,
                () -> OptionalUtilities.create(FilenameUtils.getExtension(filename)));
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
     * Removes an extension from a filename or path.
     *
     * @param filename the filename or path to remove the extension from
     * @return {@code filename} without the extension and any leading period to the extension
     */
    public static String removeExtension(String filename) {

        Optional<String> matchesSpecial = endsWithDoubleExtension(filename);
        if (matchesSpecial.isPresent()) {
            return removeSuffix(filename, matchesSpecial.get());
        } else {
            return FilenameUtils.removeExtension(filename);
        }
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
     * Retrieves filename from a {@link File} but without any extension.
     *
     * <p>Note any directory components are ignored! Only the name of the file is returned.
     *
     * @param file a file to remove the extension from
     * @return the name of {@code file} without the extension and any leading period to the
     *     extension..
     */
    public static String filenameWithoutExtension(File file) {
        return removeExtension(file.getName());
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
     * Checks if a path ends with any one of a list of special double-extensions.
     *
     * <p>The check occurs in a case-insensitive manner.
     *
     * @param path the path to check if it ends with any of the extensions
     * @return the the matching extension (without the leading period), if it exists.
     */
    private static Optional<String> endsWithDoubleExtension(String path) {
        String pathLowercase = path.toLowerCase();
        for (String extension : EXCEPTED_DOUBLE_EXTENSIONS) {
            if (pathLowercase.endsWith(extension)) {
                return Optional.of(extension.substring(1));
            }
        }
        return Optional.empty();
    }

    /** Removes a suffix from the end of a string, immutably. */
    private static String removeSuffix(String toRemoveFrom, String suffixToRemove) {
        int numberCharactersToKeep = toRemoveFrom.length() - suffixToRemove.length() - 1;
        return toRemoveFrom.substring(0, numberCharactersToKeep);
    }
}
