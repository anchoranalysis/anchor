/* (C)2020 */
package org.anchoranalysis.io.input;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.error.AnchorIOException;

public interface InputFromManager {

    String descriptiveName();

    Optional<Path> pathForBinding();

    default Path pathForBindingRequired() throws AnchorIOException {
        return pathForBinding()
                .orElseThrow(
                        () ->
                                new AnchorIOException(
                                        "A binding path is required to be associated with each input for this algorithm, but is not"));
    }

    /**
     * Performs all tidying up, file-closing etc. after we are finished using the {@link
     * InputFromManager}
     */
    default void close(ErrorReporter errorReporter) {}
}
