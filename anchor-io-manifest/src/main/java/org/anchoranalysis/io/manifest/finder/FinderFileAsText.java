/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteOutputName;

public class FinderFileAsText extends FinderSingleFile {

    private Optional<String> text = Optional.empty();
    private String outputName;

    public FinderFileAsText(String outputName, ErrorReporter errorReporter) {
        super(errorReporter);
        this.outputName = outputName;
    }

    public static String readFile(Path path) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = createReader(path)) {
            String line = null;

            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        }

        return stringBuilder.toString();
    }

    private static BufferedReader createReader(Path path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path.toFile()));
    }

    private String readFileFromFileWrite(FileWrite fileWrite) throws IOException {
        return readFile(fileWrite.calcPath());
    }

    public String get() throws GetOperationFailedException {
        assert (exists());
        if (!text.isPresent()) {
            try {
                text = Optional.of(readFileFromFileWrite(getFoundFile()));
            } catch (IOException e) {
                throw new GetOperationFailedException(e);
            }
        }
        return text.get();
    }

    @Override
    protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException {
        List<FileWrite> files =
                FinderUtilities.findListFile(manifestRecorder, new FileWriteOutputName(outputName));

        if (files.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(files.get(0));
    }
}
