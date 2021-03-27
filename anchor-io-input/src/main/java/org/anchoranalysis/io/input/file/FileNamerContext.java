package org.anchoranalysis.io.input.file;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.log.Logger;

/**
 * Provides useful additional objects when assigning a name to a file.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class FileNamerContext {

    private static final String DEFAULT_ELSE_NAME = "unknownName";

    /**
     * A directory associated with the inputs, which if defined, is guaranteed to be a parent of
     * them all.
     */
    private Optional<Path> inputDirectory;

    /**
     * If true, the namer should prefer to derive file-names relative to the directory, rather than
     * only the varying elements in the file-names.
     */
    private boolean relativeToDirectory;

    /** A fallback name, if a failure occurs when naming. */
    private String elseName;

    /** Logs information messages. */
    private Logger logger;

    public FileNamerContext(Logger logger) {
        this(Optional.empty(), false, logger);
    }

    public FileNamerContext(
            Optional<Path> inputDirectory, boolean relativeToDirectory, Logger logger) {
        this(inputDirectory, relativeToDirectory, DEFAULT_ELSE_NAME, logger);
    }

    public FileNamerContext(String elseName, Logger logger) {
        this(Optional.empty(), false, elseName, logger);
    }
}
