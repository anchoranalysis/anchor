/* (C)2020 */
package org.anchoranalysis.io.output.csv;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.file.FileOutput;
import org.anchoranalysis.io.output.file.FileOutputFromManager;

// Can be called by different threads, so synchronization is important
public class CSVWriter implements AutoCloseable {

    private String seperator = ",";
    private String doubleQuotes = "\"";

    private PrintWriter writer;
    private FileOutput output;

    private boolean writtenHeaders = false;

    /**
     * Creates and starts a CSVWriter if it's allowed, otherwise returns null
     *
     * @param outputName output-name
     * @param outputManager output-manager
     * @return the csv-writer if it's allowed, or empty if it's not.
     * @throws AnchorIOException
     */
    public static Optional<CSVWriter> createFromOutputManager(
            String outputName, BoundOutputManager outputManager) throws AnchorIOException {

        if (!outputManager.isOutputAllowed(outputName)) {
            return Optional.empty();
        }

        Optional<FileOutput> output =
                FileOutputFromManager.create("csv", Optional.empty(), outputManager, outputName);

        OptionalUtilities.ifPresent(output, FileOutput::start);
        return output.map(CSVWriter::new);
    }

    /**
     * Creates and starts a CSVWriter (it's always allowed, so will never return NULL)
     *
     * @param path path to write the CSV to
     * @return the csv-writer
     * @throws AnchorIOException
     */
    public static CSVWriter create(Path path) throws AnchorIOException {
        FileOutput output = new FileOutput(path.toString());
        output.start();
        return new CSVWriter(output);
    }

    private CSVWriter(FileOutput output) {
        this.output = output;
        this.writer = output.getWriter();
    }

    public synchronized boolean hasWrittenHeaders() {
        return writtenHeaders;
    }

    public boolean isOutputEnabled() {
        return this.writer != null;
    }

    public synchronized void writeHeaders(List<String> headerNames) {

        if (writtenHeaders) {
            return;
        }

        for (int i = 0; i < headerNames.size(); i++) {
            writeQuotes(headerNames.get(i));
            // If it's not the last item
            if (i != (headerNames.size() - 1)) {
                writer.print(seperator);
            }
        }
        writer.println();

        writtenHeaders = true;
    }

    private void writeQuotes(String text) {
        writer.print(doubleQuotes);
        writer.print(text);
        writer.print(doubleQuotes);
    }

    public synchronized void writeRow(List<TypedValue> elements) {

        for (int i = 0; i < elements.size(); i++) {

            TypedValue element = elements.get(i);

            if (element.isNumeric()) {
                writer.print(elements.get(i).getValue());
            } else {
                writeQuotes(elements.get(i).getValue());
            }

            if (i != (elements.size() - 1)) {
                writer.print(seperator);
            }
        }
        writer.println();
    }

    private synchronized void end() {
        output.end();
    }

    @Override
    public void close() {
        end();
    }
}
