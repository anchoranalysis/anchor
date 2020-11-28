package org.anchoranalysis.core.format;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
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
        return normalizeToLowerCase(filePath).endsWith("." + extensionWithoutLeadingPeriod);
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
     * Creates a set of extensions with the leading period.
     *
     * @param extensionsWithPeriod an array with extensions that each have a leading period
     * @return a set with each element in the array but with the leading period removed.
     */
    public static Set<String> removeLeadingPeriod(String[] extensionsWithPeriod) {
        return Arrays.stream(extensionsWithPeriod)
                .map(extension -> extension.substring(1))
                .collect(Collectors.toSet());
    }

    static String removeAnyLeadingPeriod(String identifier) {
        if (!identifier.isEmpty() && identifier.charAt(0) == '.') {
            return identifier.substring(1);
        } else {
            return identifier;
        }
    }

    static String normalizeToLowerCase(String input) {
        return input.toLowerCase();
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
