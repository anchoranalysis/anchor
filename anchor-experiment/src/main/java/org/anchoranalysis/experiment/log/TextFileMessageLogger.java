/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.log;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Supplier;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.io.generator.text.TextFileOutput;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Logs messages to a text-file.
 *
 * <p>Both the path of text-file can be determined in different ways by the constructor, and whether
 * it is written at all.
 *
 * @author Owen Feehan
 */
public class TextFileMessageLogger implements StatefulMessageLogger {

    // START REQUIRED ARGUMENTS
    private final Supplier<Optional<TextFileOutput>> fileOutputSupplier;
    private final ErrorReporter errorReporter;
    // END REQUIRED ARGUMENTS

    private Optional<TextFileOutput> fileOutput = Optional.empty();
    private Optional<PrintWriter> printWriter = Optional.empty();

    /** For synchronizing against the writer. */
    private final Object lockWriter = new Object();
    
    /**
     * Constructs a logger that (always) writes messages to a text-file with a specific path.
     *
     * @param filePath path to write the log to
     * @param errorReporter error-reporter an error is outputted here if the log cannot be created,
     *     and no further logging occurs.
     */
    public TextFileMessageLogger(String filePath, ErrorReporter errorReporter) {
        this.fileOutputSupplier = () -> Optional.of(new TextFileOutput(filePath));
        this.errorReporter = errorReporter;
    }

    /**
     * Constructs a logger that (maybe) writes messages to a text-file, with a path based upon an
     * <i>output name</i> applied to a {@link OutputterChecked}.
     *
     * <p>The message-log will only be outputted, if allowed by the {@link OutputterChecked}.
     *
     * @param outputName output-name
     * @param outputter output-manager
     * @param errorReporter error-reporter an error is outputted here if the log cannot be created,
     *     and no further logging occurs.
     */
    public TextFileMessageLogger(
            String outputName, OutputterChecked outputter, ErrorReporter errorReporter) {
        this.fileOutputSupplier = () -> TextFileLogHelper.createOutput(outputter, outputName);
        this.errorReporter = errorReporter;
    }

    @Override
    public void logFormatted(String formatString, Object... args) {
        log(String.format(formatString, args));
    }

    @Override
    public void start() {
        try {
            fileOutput = fileOutputSupplier.get();
            printWriter =
                    OptionalUtilities.map(
                            fileOutput,
                            output -> {
                                output.start();
                                return output.getWriter();
                            });
        } catch (Exception e) {
            errorReporter.recordError(MessageLogger.class, e);
        }
    }

    @Override
    public void log(String message) {
        printWriter.ifPresent(
                writer -> {
                    synchronized (lockWriter) {
                        writer.print(message);
                        writer.println();
                        writer.flush();
                    }
                });
    }

    @Override
    public void close(boolean successful) {
        fileOutput.ifPresent(TextFileOutput::end);
    }
}
