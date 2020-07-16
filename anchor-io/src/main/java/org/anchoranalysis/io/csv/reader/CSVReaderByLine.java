/* (C)2020 */
package org.anchoranalysis.io.csv.reader;

import java.nio.file.Path;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Reads a CSV file line-by-line
 *
 * <p>This is a helpful class that quickly processes a CSV file without having to directly interact
 * with CSVReader
 *
 * @author Owen Feehan
 */
public class CSVReaderByLine {

    @FunctionalInterface
    public static interface ProcessCSVLine {
        void processLine(String[] line, boolean firstLine) throws OperationFailedException;
    }

    public static interface ReadByLine extends AutoCloseable {

        /**
         * The headers of the CSV file
         *
         * @throws CSVReaderException
         * @return a string or NULL if the headers don't exist
         */
        String[] headers() throws CSVReaderException;

        /**
         * Reads a CSV-file iterating through each row and passing it to lineProcessor
         *
         * <p>This will automatically close any opened-files
         *
         * @param lineProcessor called one for each row incrementally
         * @return the number of lines read
         * @throws CSVReaderException
         */
        int read(ProcessCSVLine lineProcessor) throws CSVReaderException;

        /**
         * Closes any opened-files
         *
         * @throws CSVReaderException
         */
        void close() throws CSVReaderException;
    }

    private CSVReaderByLine() {}

    /** Default reader, using comma and first-line headers */
    public static ReadByLine open(Path filePath) {
        return new ReadByLineImpl(filePath, new CSVReader(",", true));
    }

    /** Default reader, using comma and first-line headers */
    public static ReadByLine open(Path filePath, String regExSeperator, boolean firstLineHeaders) {
        return new ReadByLineImpl(filePath, new CSVReader(regExSeperator, firstLineHeaders));
    }

    /** Default reader, using comma and first-line headers */
    public static ReadByLine open(
            Path filePath, String regExSeperator, boolean firstLineHeaders, boolean quotedStrings) {
        return new ReadByLineImpl(
                filePath, new CSVReader(regExSeperator, firstLineHeaders, quotedStrings));
    }
}
