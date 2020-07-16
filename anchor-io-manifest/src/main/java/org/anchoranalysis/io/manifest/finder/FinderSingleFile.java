/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;

public abstract class FinderSingleFile implements Finder {

    private Optional<FileWrite> foundFile = Optional.empty();

    private ErrorReporter errorReporter;

    // A simple method to override in each finder that is based upon finding a single file
    protected abstract Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException;

    public FinderSingleFile(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @Override
    public final boolean doFind(ManifestRecorder manifestRecorder) {

        if (manifestRecorder == null) {
            return false;
        }

        try {
            foundFile = findFile(manifestRecorder);
            return exists();
        } catch (MultipleFilesException e) {
            if (errorReporter != null) {
                errorReporter.recordError(FinderSingleFile.class, e);
            }
            return false;
        }
    }

    @Override
    public final boolean exists() {
        return foundFile.isPresent();
    }

    protected FileWrite getFoundFile() {
        return foundFile.get();
    }
}
