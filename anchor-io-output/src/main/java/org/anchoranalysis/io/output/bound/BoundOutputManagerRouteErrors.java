/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.bean.filepath.prefixer.PathWithDescription;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.FolderWritePhysical;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.writer.WriterRouterErrors;

@RequiredArgsConstructor
public class BoundOutputManagerRouteErrors {

    @Getter private final BoundOutputManager delegate;

    @Getter private final ErrorReporter errorReporter;

    /**
     * Creates a new outputManager by appending a relative folder-path to the current {@link
     * BoundOutputManagerRouteErrors}
     *
     * @see BoundOutputManager#deriveSubdirectory
     * @param subdirectoryName
     * @param manifestDescription
     * @param manifestFolder
     * @return
     */
    public BoundOutputManagerRouteErrors deriveSubdirectory(
            String subdirectoryName, ManifestFolderDescription manifestDescription) {
        return new BoundOutputManagerRouteErrors(
                delegate.deriveSubdirectory(
                        subdirectoryName,
                        manifestDescription,
                        Optional.of(new FolderWritePhysical())),
                errorReporter);
    }

    public void addOperationRecorder(WriteOperationRecorder toAdd) {
        delegate.addOperationRecorder(toAdd);
    }

    public BoundOutputManager deriveFromInput(
            PathWithDescription input,
            String expIdentifier,
            Optional<ManifestRecorder> manifestRecorder,
            Optional<ManifestRecorder> experimentalManifestRecorder,
            FilePathPrefixerParams context)
            throws BindFailedException {
        return delegate.deriveFromInput(
                input, expIdentifier, manifestRecorder, experimentalManifestRecorder, context);
    }

    public WriterRouterErrors getWriterAlwaysAllowed() {
        return new WriterRouterErrors(delegate.getWriterAlwaysAllowed(), errorReporter);
    }

    public WriterRouterErrors getWriterCheckIfAllowed() {
        return new WriterRouterErrors(delegate.getWriterCheckIfAllowed(), errorReporter);
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public OutputWriteSettings getOutputWriteSettings() {
        return delegate.getOutputWriteSettings();
    }

    public boolean isOutputAllowed(String outputName) {
        return delegate.isOutputAllowed(outputName);
    }

    public OutputAllowed outputAllowedSecondLevel(String key) {
        return delegate.outputAllowedSecondLevel(key);
    }

    public Path getOutputFolderPath() {
        return delegate.getOutputFolderPath();
    }

    public Path outFilePath(String filePathRelative) {
        return delegate.outFilePath(filePathRelative);
    }

    public String toString() {
        return delegate.toString();
    }
}
