/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.bean.filepath.prefixer.NamedPath;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.FolderWritePhysical;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.writer.RecordedOutputs;
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
     * @param subdirectoryName the subdirectory-name
     * @param manifestDescription manifest-description
     * @return the new output manager
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
            NamedPath path,
            String expIdentifier,
            Optional<ManifestRecorder> manifestRecorder,
            Optional<ManifestRecorder> experimentalManifestRecorder,
            FilePathPrefixerParams context)
            throws BindFailedException {
        return delegate.deriveFromInput(
                path, expIdentifier, manifestRecorder, experimentalManifestRecorder, context);
    }

    public WriterRouterErrors getWriterAlwaysAllowed() {
        return new WriterRouterErrors(delegate.getWriters().alwaysAllowed(), errorReporter);
    }

    public WriterRouterErrors getWriterCheckIfAllowed() {
        return new WriterRouterErrors(delegate.getWriters().checkIfAllowed(), errorReporter);
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

    public Path getOutputFolderPath() {
        return delegate.getOutputFolderPath();
    }

    public Path outFilePath(String filePathRelative) {
        return delegate.outFilePath(filePathRelative);
    }

    public String toString() {
        return delegate.toString();
    }

    public RecordedOutputs recordedOutputs() {
        return delegate.recordedOutputs();
    }

    public OutputEnabledRules outputsEnabled() {
        return delegate.outputsEnabled();
    }
}
