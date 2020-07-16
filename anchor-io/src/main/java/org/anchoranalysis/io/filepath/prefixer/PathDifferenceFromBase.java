/* (C)2020 */
package org.anchoranalysis.io.filepath.prefixer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.FilePathToUnixStyleConverter;

/**
 * Calculates the "difference" between a path and a base
 *
 * <p>i.e. if a base is <code>c:\root\somePrefix_</code> and a file is <code>
 * c:\root\somePrefix_someFile.xml</code> then the difference is <code>_someFile.xml</code>
 *
 * <p>The difference is recorded separately as folder and filename components
 *
 * <p>Internally, both paths are converted to absolute paths and URIs.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PathDifferenceFromBase {

    @Getter private final String filename;

    @Getter private final Optional<Path> folder;

    /**
     * Finds the difference between a path and a base
     *
     * @param baseFolderPath path to a base folder
     * @param filePath the path to resolve
     * @throws AnchorIOException if the canonical file cannot be found
     */
    public static PathDifferenceFromBase differenceFrom(Path baseFolderPath, Path filePath)
            throws AnchorIOException {

        try {
            String base = baseFolderPath.toFile().getCanonicalFile().toURI().getPath();
            String all = filePath.toFile().getCanonicalFile().toURI().getPath();

            // As we've converted to URIs the seperator is always a forward slash
            return calcDiff(base, all);
        } catch (IOException e) {
            throw new AnchorIOException("Cannot fully resolve paths");
        }
    }

    /**
     * Performs the difference
     *
     * <p>Assumes base is a folder. Relies on this.
     *
     * @param base the base-folder as a string
     * @param all the entire path as a string
     */
    private static PathDifferenceFromBase calcDiff(String baseFolderPath, String entirePath) {

        // Convert the base, and all to forward slashes only
        String base = FilePathToUnixStyleConverter.toStringUnixStyle(baseFolderPath);
        String all = FilePathToUnixStyleConverter.toStringUnixStyle(entirePath);

        // if base is non-empty, but doesn't end in a directory seperator we add one
        //  (we use a forward slash due to the previous step converting it into a URL style)
        if (!base.isEmpty() && !base.endsWith("/")) {
            base = base.concat("/");
        }

        // We cannot match the base against the entire string
        if (!all.startsWith(base)) {
            throw new IllegalArgumentException(
                    String.format("Cannot match base '%s' against '%s'", base, all));
        }

        // Remainder path
        String remainder = all.substring(base.length());

        File remainderFile = new File(remainder);

        return new PathDifferenceFromBase(
                remainderFile.getName(),
                Optional.ofNullable(remainderFile.getParentFile()).map(File::toPath));
    }

    /**
     * The folder (if it exists) and filename combned.
     *
     * @return the combined-path
     */
    public Path combined() {
        if (folder.isPresent()) {
            return folder.get().resolve(getFilename());
        } else {
            return Paths.get(getFilename());
        }
    }
}
