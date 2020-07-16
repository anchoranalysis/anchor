/* (C)2020 */
package org.anchoranalysis.io.csv.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

// Reads a CSV File
public class CSVReader {

    private FileReader fileReader;
    private BufferedReader bufferedReader;

    private String[] headers;

    private String regExSeperator;

    private boolean firstLineHeaders;

    private boolean quotedStrings;

    public class OpenedCSVFile implements AutoCloseable {

        private int numCols = -1;

        public boolean hasHeaders() {
            return firstLineHeaders;
        }

        public String[] getHeaders() {
            return headers;
        }

        // Returns null when finished
        public Optional<String[]> readLine() throws IOException {

            String line = bufferedReader.readLine();

            if (line == null) {
                return Optional.empty();
            }

            String[] tokenized = line.split(regExSeperator);

            if (numCols == -1) {
                numCols = tokenized.length;
            }

            if (tokenized.length != numCols) {
                throw new IOException("Incorrect number of columns for line");
            }

            maybeRemoveQuotes(tokenized);

            return Optional.of(tokenized);
        }

        public void setNumCols(int numCols) {
            this.numCols = numCols;
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

    public CSVReader(String regExSeperator, boolean firstLineHeaders, boolean quotedStrings) {
        this.firstLineHeaders = firstLineHeaders;
        this.regExSeperator = regExSeperator;
        this.quotedStrings = quotedStrings;
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

            OpenedCSVFile fileOut = new OpenedCSVFile();

            if (firstLineHeaders) {
                String line = bufferedReader.readLine();

                if (line == null) {
                    throw new CSVReaderException("No header line found");
                }

                headers = line.split(regExSeperator);
                fileOut.setNumCols(headers.length);
            }

            return fileOut;

        } catch (IOException e) {
            throw new CSVReaderException(e);
        }
    }

    private static String maybeRemoveQuotes(String s) { // NOSONAR
        if (s.length() <= 2) {
            return s;
        }

        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        } else {
            return s;
        }
    }
}
