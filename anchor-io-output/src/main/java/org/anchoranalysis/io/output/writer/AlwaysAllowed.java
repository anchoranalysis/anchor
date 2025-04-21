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

package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.SimpleOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.WithoutOutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Allows every output, irrespective of whether the {@link OutputterChecked} allows the output-name.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class AlwaysAllowed implements Writer {

    // START REQUIRED ARGUMENTS
    /** Outputter for elements. */
    private final ElementOutputter outputter;

    /** Execute before every operation if defined. */
    private final Optional<WriterExecuteBeforeEveryOperation> preop;

    // END REQUIRED ARGUMENTS

    @Override
    public Optional<OutputterChecked> createSubdirectory(
            String outputName, boolean inheritOutputRulesAndRecording)
            throws OutputWriteFailedException {

        maybeExecutePreop();
        return Optional.of(
                outputter.deriveSubdirectory(outputName, inheritOutputRulesAndRecording));
    }

    // Write a file without checking if the outputName is allowed
    @Override
    public <T> boolean write(
            String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element)
            throws OutputWriteFailedException {
        outputter
                .getExecutionTimeRecorder()
                .recordExecutionTime(
                        outputNameForWriting(outputName),
                        () -> {
                            maybeExecutePreop();
                            elementWriter
                                    .get()
                                    .write(
                                            element.get(),
                                            new SimpleOutputNameStyle(outputName),
                                            outputter);
                        });
        return true;
    }

    @Override
    public <T> boolean writeWithIndex(
            IndexableOutputNameStyle outputNameStyle,
            ElementWriterSupplier<T> elementWriter,
            ElementSupplier<T> element,
            String index)
            throws OutputWriteFailedException {
        return outputter
                .getExecutionTimeRecorder()
                .recordExecutionTime(
                        outputNameForWriting(outputNameStyle.getOutputName()),
                        () -> {
                            maybeExecutePreop();
                            elementWriter
                                    .get()
                                    .writeWithIndex(
                                            element.get(), index, outputNameStyle, outputter);
                            return true;
                        });
    }

    @Override
    public <T> boolean writeWithoutName(
            String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element)
            throws OutputWriteFailedException {
        outputter
                .getExecutionTimeRecorder()
                .recordExecutionTime(
                        outputNameForWriting(outputName),
                        () -> {
                            maybeExecutePreop();
                            elementWriter
                                    .get()
                                    .write(
                                            element.get(),
                                            new WithoutOutputNameStyle(outputName),
                                            outputter);
                        });
        return true;
    }

    @Override
    public Optional<Path> createFilenameForWriting(String outputName, String extension) {

        maybeExecutePreop();

        Path path = outputter.makeOutputPath(Optional.of(outputName), extension, outputName);
        return Optional.of(path);
    }

    private void maybeExecutePreop() {
        preop.ifPresent(WriterExecuteBeforeEveryOperation::execute);
    }

    /** The name of {@code outputName} when recording the execution-time. */
    private static String outputNameForWriting(String outputName) {
        return "Writing output: " + outputName;
    }
}
