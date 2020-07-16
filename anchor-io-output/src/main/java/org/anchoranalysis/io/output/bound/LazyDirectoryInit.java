/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.output.error.OutputManagerAlreadyExistsException;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import org.apache.commons.io.FileUtils;

/**
 * Creates the output-directory lazily on the first occasion exec() is called
 *
 * <p>Depending on settings, the initialization routine involves:
 *
 * <ul>
 *   <li>checks if a directory already exists at the path, and throws an errror
 *   <li>deletes existing directory contents
 *   <li>creates the directory and any intermediate paths
 *   <li>first calls an initiation routine on parent initializer
 * </ul>
 */
@RequiredArgsConstructor
class LazyDirectoryInit implements WriterExecuteBeforeEveryOperation {

    // START REQUIRED ARGUMENTS
    /** The output-directory to be init */
    private final Path outputDirectory;

    /** Whether to delete the existing folder if it exists */
    private final boolean delExistingFolder;

    /** A parent whose exec() is called before our exec() is called (if empty(), ignored) */
    private final Optional<WriterExecuteBeforeEveryOperation> parent;
    // END REQUIRED ARGUMENTS

    private boolean needsInit = true;

    @Override
    public synchronized void exec() {
        if (needsInit) {

            parent.ifPresent(WriterExecuteBeforeEveryOperation::exec);

            if (outputDirectory.toFile().exists()) {
                if (delExistingFolder) {
                    FileUtils.deleteQuietly(outputDirectory.toFile());
                } else {
                    String line1 = "Output directory already exists.";
                    String line3 = "Consider enabling delExistingFolder=\"true\" in experiment.xml";
                    // Check if it exists already, and refuse to overwrite
                    throw new OutputManagerAlreadyExistsException(
                            String.format(
                                    "%s%nBefore proceeding, please delete: %s%n%s",
                                    line1, outputDirectory, line3));
                }
            }

            // We create any subdirectories as needed
            outputDirectory.toFile().mkdirs();
            needsInit = false;
        }
    }
}
