/*-
 * #%L
 * anchor-io-generator
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
package org.anchoranalysis.io.generator;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.writer.ElementOutputter;

/**
 * A {@link Generator} that eventually writes only a single file to the filesystem.
 *
 * @author Owen Feehan
 * @param <T> iteration-type
 * @param <S> type after any necessary preprocessing
 */
public abstract class SingleFileTypeGenerator<T, S> implements TransformingGenerator<T, S> {

    /**
     * Write generated content for {code element} to the file {@code filePath}.
     *
     * <p>This function deliberately leaves ambiguity over what occurs if a file already exists at
     * {@code filePath}.
     *
     * @param element element to be assigned and then transformed.
     * @param settings settings for outputting.
     * @param filePath the path to the file to write to.
     * @throws OutputWriteFailedException if the content cannot be written successfully.
     */
    public abstract void writeToFile(T element, OutputWriteSettings settings, Path filePath)
            throws OutputWriteFailedException;

    /**
     * Selects the file/extension to be used for outputting the file.
     *
     * @param settings settings for outputting.
     * @param logger logger for warning for information messages when outputting.
     * @return the file extension (without leading period) to be used for outputting.
     * @throws OperationFailedException if unable to select a file-extension.
     */
    public abstract String selectFileExtension(
            OutputWriteSettings settings, Optional<Logger> logger) throws OperationFailedException;

    @Override
    public void write(T element, OutputNameStyle outputNameStyle, ElementOutputter outputter)
            throws OutputWriteFailedException {
        writeInternal(
                element,
                outputNameStyle.filenameWithoutExtension(),
                outputNameStyle.getOutputName(),
                outputter);
    }

    @Override
    public void writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            ElementOutputter outputter)
            throws OutputWriteFailedException {
        writeInternal(
                element,
                Optional.of(outputNameStyle.filenameWithoutExtension(index)),
                outputNameStyle.getOutputName(),
                outputter);
    }

    private void writeInternal(
            T element,
            Optional<String> filenameWithoutExtension,
            String outputName,
            ElementOutputter outputter)
            throws OutputWriteFailedException {

        try {
            OutputWriteSettings settings = outputter.getSettings();

            String fileExtension = selectFileExtension(settings, outputter.logger());

            Path pathToWriteTo =
                    outputter.makeOutputPath(filenameWithoutExtension, fileExtension, outputName);

            // First write to the file system, and then write to the operation-recorder.
            writeToFile(element, settings, pathToWriteTo);

        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
