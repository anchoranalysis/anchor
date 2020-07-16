/* (C)2020 */
package org.anchoranalysis.io.filepath.prefixer;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.error.FilePathPrefixerException;

public class FilePathPrefixerParams {

    private boolean debugMode;

    /** A directory indicating where inputs can be located */
    private final Optional<Path> outputDirectory;

    public FilePathPrefixerParams(boolean debugMode, Optional<Path> outputDirectory)
            throws FilePathPrefixerException {
        super();
        this.debugMode = debugMode;
        this.outputDirectory = outputDirectory;
        checkAbsolutePath();
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public Optional<Path> getOutputDirectory() {
        return outputDirectory;
    }

    private void checkAbsolutePath() throws FilePathPrefixerException {
        if (outputDirectory.isPresent() && !outputDirectory.get().isAbsolute()) {
            throw new FilePathPrefixerException(
                    String.format(
                            "An non-absolute path was passed to FilePathPrefixerParams of %s",
                            outputDirectory.get()));
        }
    }
}
