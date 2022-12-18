/*-
 * #%L
 * anchor-io-input
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * An opened-CSV file that is read line-by-line and processed.
 * 
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class ReadByLine implements AutoCloseable {

    // START REQUIRED ARGUMENTS
	/** Path to the CSV file that will be read. */
    private final Path filePath;
    
    /** How the CSV will be read. */
    private final CSVReader csvReader;
    // END REQUIRED ARGUMENTS

    private CSVReader.OpenedCSVFile openedFile = null;

    /**
     * The headers of the CSV file.
     *
     * @throws CSVReaderException if the file cannot be opened successfully.
     * @return a string or null if the headers don't exist.
     */
    public String[] headers() throws CSVReaderException {
        openIfNecessary();
        return openedFile.getHeaders();
    }

    /**
     * Reads a CSV-file iterating through each row and passing it to lineProcessor
     *
     * <p>This will automatically close any opened-files
     *
     * @param lineProcessor called one for each row incrementally
     * @return the number of lines read
     * @throws CSVReaderException if any file-system I/O errors occur.
     */
    public int read(ProcessCSVLine lineProcessor) throws CSVReaderException {

        try {
            openIfNecessary();

            Optional<String[]> line;
            boolean firstLine = true;

            int count = 0;

            while ((line = openedFile.readLine()).isPresent()) {
                lineProcessor.processLine(line.get(), firstLine);
                firstLine = false;
                count++;
            }

            return count;

        } catch (IOException | OperationFailedException e) {
            throw new CSVReaderException(e);
        } finally {
            close();
        }
    }

    /**
     * Closes any opened-files
     *
     * @throws CSVReaderException if any file-system I/O errors occur.
     */
    @Override
    public void close() throws CSVReaderException {
        if (openedFile != null) {
            openedFile.close();
            openedFile = null;
        }
    }

    private void openIfNecessary() throws CSVReaderException {
        if (openedFile == null) {
            openedFile = csvReader.read(filePath);
        }
    }
}
