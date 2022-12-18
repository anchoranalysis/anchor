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
	/** A regular-expression in Java syntax for splitting a line by separator. */
    private final String regularExpressionSeperator;
    
    /** Whether the first line of the CSV file contains headers or not. */
    private final boolean firstLineHeaders;
    
    /** Whether strings should be quoted or not in the CSV file. */
    private final boolean quotedStrings;
    // END REQUIRED ARGUMENTS

    private FileReader fileReader;
    private BufferedReader bufferedReader;

    private String[] headers;

    /**
     * A CSV file that has been opened for reading from the file-system.
     * 
     * @author Owen File
     */
    public class OpenedCSVFile implements AutoCloseable {

    	/**
    	 * Sets the number of columns, if it is already known from elsewhere.
    	 * 
    	 * <p>If no number has been assigned (or -1) it is automatically determined when reading the CSV file.
    	 */
        @Setter private int numberColumns = -1;

        /**
         * Reads the next-line from the open CSV file.
         * 
         * @return the line or {@link Optional#empty} if there are no more lines to read.
         * @throws IOException if any problem occurs access the file.
         */
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

        /**
         * Does the first line of the CSV file specify headers rather than values?
         * 
         * @return true iff the first line specifies headers.
         */
        public boolean hasHeaders() {
            return firstLineHeaders;
        }

        /** 
         * The header-names of the CSV file if available.
         * 
         * @return an array, corresponding to each header, or {@code null} if no headers are available.
         */
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

    /**
     * Create, expecting unquoted strings.
     * 
     * @param regularExpressionSeperator a regular-expression in Java syntax for splitting a line by separator.
     * @param firstLineHeaders whether the first line of the CSV file contains headers or not.
     */
    public CSVReader(String regularExpressionSeperator, boolean firstLineHeaders) {
        this(regularExpressionSeperator, firstLineHeaders, false);
    }

    /**
     * Opens a CSV for reading.
     *
     * @param filePath path to file
     * @return the opened-file (that must eventually be closed)
     * @throws CSVReaderException if any error occurs while reading the CSV file.
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
