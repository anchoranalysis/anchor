/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.input.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Reads a CSV File from the file-system.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class CSVReader {

    // START REQUIRED ARGUMENTS
    private final String regularExpressionSeperator;
    private final boolean firstLineHeaders;
    private final boolean quotedStrings;
    // END REQUIRED ARGUMENTS

    private FileReader fileReader;
    private BufferedReader bufferedReader;

    private String[] headers;

    public class OpenedCSVFile implements AutoCloseable {

        @Setter private int numberColumns = -1;

        // Returns null when finished
        public Optional<String[]> readLine() throws IOException {

            String line = bufferedReader.readLine();

            if (line == null) {
                return Optional.empty();
            }

            String[] tokenized = line.split(regularExpressionSeperator);

            if (numberColumns == -1) {
                numberColumns = tokenized.length;
            }

            if (tokenized.length != numberColumns) {
                throw new IOException("Incorrect number of columns for line");
            }

            maybeRemoveQuotes(tokenized);

            return Optional.of(tokenized);
        }

        @Override
        public void close() throws CSVReaderException {
            try {
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
            } catch (IOException e) {
                throw new CSVReaderException(e);
            }
        }

        public boolean hasHeaders() {
            return firstLineHeaders;
        }

        public String[] getHeaders() {
            return headers;
        }

        private void maybeRemoveQuotes(String[] arr) {

            if (!quotedStrings) {
                // Exit early if we don't supported quoted strings
                return;
            }

            for (int i = 0; i < arr.length; i++) {
                arr[i] = CSVReader.maybeRemoveQuotes(arr[i]);
            }
        }
    }

    public CSVReader(String regExSeperator, boolean firstLineHeaders) {
        this(regExSeperator, firstLineHeaders, false);
    }

    /**
     * Opens a CSV for reading.
     *
     * @param filePath path to file
     * @return the opened-file (that must eventually be closed)
     * @throws CSVReaderException
     */
    public OpenedCSVFile read(Path filePath) throws CSVReaderException {

        if (!filePath.toFile().exists()) {
            throw new CSVReaderException(
                    "Failed to read CSV file as no file exists at " + filePath);
        }

        try {
            fileReader = new FileReader(filePath.toFile());

            this.bufferedReader = new BufferedReader(fileReader);

            OpenedCSVFile out = new OpenedCSVFile(); // NOSONAR

            if (firstLineHeaders) {
                String line = bufferedReader.readLine();

                if (line == null) {
                    throw new CSVReaderException("No header line found");
                }

                headers = line.split(regularExpressionSeperator);
                out.setNumberColumns(headers.length);
            }

            return out;

        } catch (IOException e) {
            throw new CSVReaderException(e);
        }
    }

    private static String maybeRemoveQuotes(String string) { // NOSONAR
        if (string.length() <= 2) {
            return string;
        }

        if (string.startsWith("\"") && string.endsWith("\"")) {
            return string.substring(1, string.length() - 1);
        } else {
            return string;
        }
    }
}
