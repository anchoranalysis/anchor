package org.anchoranalysis.io.input.csv;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.input.csv.CSVReaderByLine.ProcessCSVLine;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReadByLine implements AutoCloseable {
    
    // START REQUIRED ARGUMENTS
    private final Path filePath;
    private final CSVReader csvReader;
    // END REQUIRED ARGUMENTS

    private CSVReader.OpenedCSVFile openedFile = null;

    /**
     * The headers of the CSV file
     *
     * @throws CSVReaderException
     * @return a string or null if the headers don't exist
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
     * @throws CSVReaderException
     */
    public int read(ProcessCSVLine lineProcessor) throws CSVReaderException {

        try {
            openIfNecessary();

            Optional<String[]> line;
            boolean firstLine = true;

            int cnt = 0;

            while ((line = openedFile.readLine()).isPresent()) {
                lineProcessor.processLine(line.get(), firstLine);
                firstLine = false;
                cnt++;
            }

            return cnt;

        } catch (IOException | OperationFailedException e) {
            throw new CSVReaderException(e);
        } finally {
            close();
        }
    }

    private void openIfNecessary() throws CSVReaderException {
        if (openedFile == null) {
            openedFile = csvReader.read(filePath);
        }
    }

    /**
     * Closes any opened-files
     *
     * @throws CSVReaderException
     */
    @Override
    public void close() throws CSVReaderException {
        if (openedFile != null) {
            openedFile.close();
            openedFile = null;
        }
    }
}