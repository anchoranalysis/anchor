/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;

/**
 * Certain parameters that are exposed after any file-system binding for inputs and outputs has
 * occurred.
 *
 * @author Owen Feehan
 */
public interface BoundIOContext {

    Path getModelDirectory();

    BoundOutputManagerRouteErrors getOutputManager();

    boolean isDebugEnabled();

    Logger getLogger();

    default CommonContext common() {
        return new CommonContext(getLogger(), getModelDirectory());
    }

    default ErrorReporter getErrorReporter() {
        return getLogger().errorReporter();
    }

    default MessageLogger getLogReporter() {
        return getLogger().messageLogger();
    }

    /**
     * Creates a new context that writes instead to a sub-directory
     *
     * @param subDirectoryName subdirectory name
     * @return newly created context
     */
    default BoundIOContext subdirectory(
            String subDirectoryName, ManifestFolderDescription manifestFolderDescription) {
        return new RedirectIntoSubdirectory(this, subDirectoryName, manifestFolderDescription);
    }

    /**
     * Optionally creates a new context like with {@link subdirectory} but only if a directory-name
     * is defined
     *
     * @param subDirectoryName if defined, a new context is created that writes into a sub-directory
     *     of this name
     * @return either a newly created context, or the existing context
     */
    default BoundIOContext maybeSubdirectory(
            Optional<String> subDirectoryName,
            ManifestFolderDescription manifestFolderDescription) {
        return subDirectoryName
                .map(name -> subdirectory(name, manifestFolderDescription))
                .orElse(this);
    }
}
