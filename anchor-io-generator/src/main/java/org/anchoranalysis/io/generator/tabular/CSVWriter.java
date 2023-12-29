/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.generator.tabular;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.io.generator.text.TextFileOutput;
import org.anchoranalysis.io.generator.text.TextFileOutputter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Writes a CSV file to the file-system.
 *
 * <p>It can be disabled, whereby no content is written to the file-system but method calls
 * otherwise work as usual.
 *
 * <p>As it can be called by different threads, public methods are synchronized.
 */
public class CSVWriter implements AutoCloseable {

    private String seperator = ",";
    private String doubleQuotes = "\"";

    /** How and to where content is written to a text-file. */
    private TextFileOutput output;

    private boolean writtenHeaders = false;

    /**
     * Like {@link #createFromOutputter(String, OutputterChecked)} but suppresses any exceptions
     * into an error log - and writes headers.
     *
     * @param outputName unique name identifying the output which is used to construct a file-path
     *     to write to.
     * @param outputter how and whether outputs are written.
     * @param headerNames header-names for the CSV file
     * @param errorReporter used to reporter an error if the output cannot be created.
     * @return the csv-writer if it's allowed, or empty if it's not, or if an error occurs.
     */
    public static Optional<CSVWriter> createFromOutputterWithHeaders(
            String outputName,
            OutputterChecked outputter,
            Supplier<List<String>> headerNames,
            ErrorReporter errorReporter) {
        try {
            Optional<CSVWriter> writer = createFromOutputter(outputName, outputter);
            if (writer.isPresent()) {
                writer.get().writeHeaders(headerNames.get());
            }
            return writer;
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(CSVWriter.class, e);
            return Optional.empty();
        }
    }

    /**
     * Creates and starts a CSVWriter if it's allowed, otherwise returns {@link Optional#empty}.
     *
     * @param outputName unique name identifying the output which is used to construct a file-path
     *     to write to.
     * @param outputter how and whether outputs are written.
     * @return the csv-writer if it's allowed, or empty if it's not.
     * @throws OutputWriteFailedException if the CSV file cannot be created successfully.
     */
    public static Optional<CSVWriter> createFromOutputter(
            String outputName, OutputterChecked outputter) throws OutputWriteFailedException {

        if (!outputter.getOutputsEnabled().isOutputEnabled(outputName)) {
            return Optional.empty();
        }

        Optional<TextFileOutput> output =
                TextFileOutputter.create(
                        NonImageFileFormat.CSV.extensionWithoutPeriod(), outputter, outputName);

        if (!output.isPresent()) {
            return Optional.empty();
        }

        output.get().start();
        return Optional.of(new CSVWriter(output.get()));
    }

    /**
     * Creates and starts a CSVWriter (it's always allowed, so will never return null)
     *
     * @param path path to write the CSV to
     * @return the csv-writer
     * @throws OutputWriteFailedException if the CSV file cannot be created successfully.
     */
    public static CSVWriter create(Path path) throws OutputWriteFailedException {
        TextFileOutput output = new TextFileOutput(path.toString());
        output.start();
        return new CSVWriter(output);
    }

    /**
     * Private constructor called by static methods.
     *
     * @param output how and to where content is written to a text-file.
     * @throws OutputWriteFailedException if the CSV file cannot be created successfully.
     */
    private CSVWriter(TextFileOutput output) throws OutputWriteFailedException {
        this.output = output;
    }

    /**
     * Whether the CSV file writes header-names as the first line.
     *
     * @return true iff the CSV file writes headers.
     */
    public synchronized boolean hasWrittenHeaders() {
        return writtenHeaders;
    }

    /**
     * Whether the output is enabled or not?
     *
     * <p>When the output is disabled, no CSV file is written to the file-system, but the public
     * methods work as usual.
     *
     * @return true iff the output is enabled.
     */
    public boolean isOutputEnabled() {
        return output.getWriter().isPresent();
    }

    /**
     * Writes a line with the header-names of the columns.
     *
     * <p>This is called zero or one times before calling {@link #writeRow(List)} but the class does
     * not enforce this order.
     *
     * <p>It should not be called more than once.
     *
     * @param headerNames a name for each respective column. This should have an identical number of
     *     elements as {@code elements} in subsequent calls to {@link #writeRow(List)}.
     */
    public synchronized void writeHeaders(List<String> headerNames) {

        if (writtenHeaders) {
            return;
        }

        if (!output.getWriter().isPresent()) {
            return;
        }

        PrintWriter writer = output.getWriter().get();

        int index = 0;
        for (String name : headerNames) {
            writeQuotes(writer, name);
            // If it's not the last item
            if (!isFinalElement(index, headerNames)) {
                writer.print(seperator);
            }
            index++;
        }
        writer.println();

        writtenHeaders = true;
    }

    /**
     * Writes a line with values of a particular row
     *
     * <p>This is always called after optionally calling {@link #writeHeaders(List)} once but the
     * class does not enforce this order.
     *
     * @param elements a value in the row for each respective column. This should have an identical
     *     number of elements each time it is called, and be equal to the number of {@code elements}
     *     in any previous call to {@link #writeHeaders(List)}.
     */
    public synchronized void writeRow(List<TypedValue> elements) {

        if (!output.getWriter().isPresent()) {
            return;
        }

        PrintWriter writer = output.getWriter().get();

        int index = 0;
        for (TypedValue element : elements) {

            String value = element.getValue();

            if (element.isNumeric()) {
                writer.print(value);
            } else {
                writeQuotes(writer, value);
            }

            if (!isFinalElement(index, elements)) {
                writer.print(seperator);
            }

            index++;
        }
        writer.println();
    }

    @Override
    public synchronized void close() {
        output.end();
    }

    private void writeQuotes(PrintWriter writer, String text) {
        writer.print(doubleQuotes);
        writer.print(text);
        writer.print(doubleQuotes);
    }

    /** Does an index represent the final-most element in a list? */
    private static boolean isFinalElement(int index, List<?> list) {
        return index == (list.size() - 1);
    }
}
