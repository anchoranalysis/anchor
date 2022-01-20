/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.core.format;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * File extensions for various formats.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormatExtensions {

    /**
     * The extensions of all image file formats as enumerated in {@link ImageFileFormat},
     * lazily-created as needed.
     */
    private static String[] allImageExtensions;

    /**
     * The extensions of all image file formats as enumerated in {@link ImageFileFormat}.
     *
     * <p>Note that some formats may have more than one extension e.g. {@code tif} and {@code tiff}.
     *
     * @return an array of extensions for all the image file formats.
     */
    public static String[] allImageExtensions() {
        if (allImageExtensions == null) {
            allImageExtensions = createExtensionsArray(ImageFileFormat.values());
        }
        return allImageExtensions;
    }

    /**
     * Does a filePath match an extension?
     *
     * @param filePath file-path to match, case irrelevant
     * @param extensionWithoutLeadingPeriod the extension to match (in lower-case, without a leading
     *     period)
     * @return true if the filePath ends with the expected extension
     */
    public static boolean matches(String filePath, String extensionWithoutLeadingPeriod) {
        return FormatExtensions.normalizeToLowerCase(filePath)
                .endsWith("." + extensionWithoutLeadingPeriod);
    }

    /**
     * Change the extension in a path.
     *
     * @param path the path to change if the extension matches
     * @param formatToChangeFrom the format to change from (a match occurs against the associated
     *     extension)
     * @param formatToAssign the format to assign (the default extension is assigned)
     * @return a path with the extension changed
     * @throws OperationFailedException if the unchanged path does not match {@code
     *     formatToChangeFrom}.
     */
    public static Path changeExtension(
            Path path, NonImageFileFormat formatToChangeFrom, FileFormat formatToAssign)
            throws OperationFailedException {

        if (!formatToChangeFrom.matches(path)) {
            throw new OperationFailedException(
                    "Files must have have an extension associated with the format: "
                            + formatToChangeFrom.descriptiveIdentifier());
        }

        // Change old extension into new extension
        return Paths.get(
                changeExtension(
                        path.toString(),
                        formatToChangeFrom.extensionWithoutPeriod(),
                        formatToAssign.getDefaultExtension()));
    }

    /**
     * Removes a single leading period from a string, if one exists.
     *
     * @param str a string that may or may not have a leading period.
     * @return a string with the leading period remove, if it exists.
     */
    public static String removeAnyLeadingPeriod(String str) {
        if (!str.isEmpty() && str.charAt(0) == '.') {
            return str.substring(1);
        } else {
            return str;
        }
    }

    /**
     * Normalize an extension to lower-case.
     *
     * <p>This function exists to achieve normalization in a consistent way across many calls.
     *
     * @param extension the extension to normalize.
     * @return a lower-case version of {@code extension}.
     */
    public static String normalizeToLowerCase(String extension) {
        return extension.toLowerCase();
    }

    private static String changeExtension(
            String path, String extensionToChange, String extensionToAssign) {
        path = path.substring(0, path.length() - extensionToChange.length());
        path = path.concat(extensionToAssign);
        return path;
    }

    private static String[] createExtensionsArray(ImageFileFormat[] formats) {
        return Arrays.stream(formats)
                .flatMap(ImageFileFormat::allExtensions)
                .toArray(String[]::new);
    }
}
