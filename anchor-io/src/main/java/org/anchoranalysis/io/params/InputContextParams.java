/* (C)2020 */
package org.anchoranalysis.io.params;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.functional.OptionalUtilities;

/**
 * Additional paramaters that provide context for many beans that provide input-functions
 *
 * @author Owen Feehan
 */
public class InputContextParams {

    /** A list of paths referring to specific inputs; */
    @Getter @Setter private Optional<List<Path>> inputPaths;

    /** If defined, a directory which can be used by beans to find input */
    @Getter private Optional<Path> inputDir;

    /** A glob that can be used by beans to filter input */
    @Getter @Setter private String inputFilterGlob = "*";

    /** A list of extensions that can be used filter inputs */
    @Getter @Setter private Set<String> inputFilterExtensions = fallBackFilterExtensions();

    /** Parameters for debug-mode (only defined if we are in debug mode) */
    @Getter @Setter private Optional<DebugModeParams> debugModeParams;

    // This should always be ab absolute path, never a relative one
    public void setInputDir(Optional<Path> inputDir) throws IOException {
        OptionalUtilities.ifPresent(inputDir, InputContextParams::checkAbsolutePath);
        this.inputDir = inputDir;
    }

    private static void checkAbsolutePath(Path inputDir) throws IOException {
        if (!inputDir.isAbsolute()) {
            throw new IOException(
                    String.format(
                            "An non-absolute path was passed to setInputDir() of %s", inputDir));
        }
    }

    // If no filter extensions are provided from anywhere else, this is a convenient set of defaults
    private Set<String> fallBackFilterExtensions() {
        return new HashSet<>(Arrays.asList("jpg", "png", "tif", "tiff", "gif", "bmp"));
    }
}
