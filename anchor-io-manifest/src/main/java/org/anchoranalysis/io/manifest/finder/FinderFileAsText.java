/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.finder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
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

    public String get() throws OperationFailedException {
        assert (exists());
        if (!text.isPresent()) {
            try {
                text = Optional.of(readFileFromFileWrite(getFoundFile()));
            } catch (IOException e) {
                throw new OperationFailedException(e);
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
