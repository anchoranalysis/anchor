/* (C)2020 */
package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class WriterRouterErrors {

    private Writer delegate;
    private ErrorReporter errorReporter;

    public WriterRouterErrors(Writer delegate, ErrorReporter errorReporter) {
        super();
        this.delegate = delegate;
        this.errorReporter = errorReporter;
    }

    public Optional<BoundOutputManagerRouteErrors> bindAsSubdirectory(
            String outputName, ManifestFolderDescription manifestDescription) {
        try {
            return delegate.bindAsSubdirectory(outputName, manifestDescription, Optional.empty())
                    .map(output -> new BoundOutputManagerRouteErrors(output, errorReporter));
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(BoundOutputManagerRouteErrors.class, e);
            return Optional.empty();
        }
    }

    public void writeSubfolder(
            String outputName,
            Operation<WritableItem, OutputWriteFailedException> collectionGenerator) {
        try {
            delegate.writeSubfolder(outputName, collectionGenerator);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(BoundOutputManagerRouteErrors.class, e);
        }
    }

    public int write(
            IndexableOutputNameStyle outputNameStyle,
            Operation<WritableItem, OutputWriteFailedException> generator,
            String index) {
        try {
            return delegate.write(outputNameStyle, generator, index);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(BoundOutputManagerRouteErrors.class, e);
            return -1;
        }
    }

    public int write(
            IndexableOutputNameStyle outputNameStyle,
            Operation<WritableItem, OutputWriteFailedException> generator,
            int index) {
        try {
            return delegate.write(outputNameStyle, generator, index);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(BoundOutputManagerRouteErrors.class, e);
            return -1;
        }
    }

    public void write(
            OutputNameStyle outputNameStyle,
            Operation<WritableItem, OutputWriteFailedException> generator) {
        try {
            delegate.write(outputNameStyle, generator);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(BoundOutputManagerRouteErrors.class, e);
        }
    }

    public void write(
            String outputName, Operation<WritableItem, OutputWriteFailedException> generator) {
        try {
            delegate.write(outputName, generator);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(BoundOutputManagerRouteErrors.class, e);
        }
    }

    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {
        return writeGenerateFilename(outputName, extension, manifestDescription, "", "", "");
    }

    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription,
            String outputNamePrefix,
            String outputNameSuffix,
            String index) {
        return delegate.writeGenerateFilename(
                outputName,
                extension,
                manifestDescription,
                outputNamePrefix,
                outputNameSuffix,
                index);
    }
}
