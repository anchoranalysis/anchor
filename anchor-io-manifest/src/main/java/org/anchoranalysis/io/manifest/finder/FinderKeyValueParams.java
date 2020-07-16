/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;

public class FinderKeyValueParams extends FinderSingleFile {

    private String manifestFunction;

    public FinderKeyValueParams(String manifestFunction, ErrorReporter errorReporter) {
        super(errorReporter);
        this.manifestFunction = manifestFunction;
    }

    public KeyValueParams get() throws GetOperationFailedException {
        assert (exists());

        try {
            return KeyValueParams.readFromFile(getFoundFile().calcPath());
        } catch (IOException e) {
            throw new GetOperationFailedException(e);
        }
    }

    @Override
    protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException {
        List<FileWrite> files =
                FinderUtilities.findListFile(
                        manifestRecorder,
                        new FileWriteManifestMatch(
                                new ManifestDescriptionFunctionMatch(manifestFunction)));

        if (files.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(files.get(0));
    }
}
