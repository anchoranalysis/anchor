/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.stack.output.generator;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.io.generator.TransformingGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.writer.ElementOutputter;

/**
 * Transforms an entity to a {@link Stack} and writes it to the file-system.
 *
 * @param <T> the type of entity that is transformed to a {@link Stack}
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class RasterGenerator<T> implements TransformingGenerator<T, Stack> {

    @Override
    public void write(T element, OutputNameStyle outputNameStyle, ElementOutputter outputter)
            throws OutputWriteFailedException {
        writeInternal(
                element,
                outputNameStyle.filenameWithoutExtension(),
                outputNameStyle.getOutputName(),
                outputter);
    }

    /** As only a single-file is involved, this methods delegates to a simpler virtual method. */
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

    /**
     * Guarantees on the attributes of all images created by the generator.
     *
     * @return options that are guaranteed to be true of all images by the generator.
     */
    public abstract StackWriteAttributes guaranteedImageAttributes();

    /**
     * Selects the file-extension to use for a particular stack.
     *
     * @param stack the stack to select a file-extension for
     * @param options options that describe how {@code stack} should be written
     * @param settings general settings for writing output
     * @param logger logger for information messages or warnings associated with writing outputs
     * @return the file extension without any leading period
     * @throws OperationFailedException if the operation could not be successfully completed.
     */
    protected abstract String selectFileExtension(
            Stack stack,
            StackWriteOptions options,
            OutputWriteSettings settings,
            Optional<Logger> logger)
            throws OperationFailedException;

    /**
     * Writes a stack to the file-system.
     *
     * @param untransformedElement the element for the generator <i>before</i> transforming to a
     *     {@link Stack}
     * @param transformedElement the {@link Stack} that {@code element} was transformed into
     * @param options options that describe how {@code stack} should be written
     * @param settings general settings for writing output.
     * @param filePath the file-path to write too including the extension.
     * @throws OutputWriteFailedException if the image could not be be successfully written to the file-system.
     */
    protected abstract void writeToFile(
            T untransformedElement,
            Stack transformedElement,
            StackWriteOptions options,
            OutputWriteSettings settings,
            Path filePath)
            throws OutputWriteFailedException;

    /** Writes an untransformed element to the file-system, after transforming it to a {@link Stack}. */
    private void writeInternal(
            T elementUntransformed,
            Optional<String> filenameWithoutExtension,
            String outputName,
            ElementOutputter outputter)
            throws OutputWriteFailedException {

        Stack transformedElement =
                outputter
                        .getExecutionTimeRecorder()
                        .recordExecutionTime(
                                String.format("Preparing a raster to write (%s)", outputName),
                                () -> transform(elementUntransformed));

        StackWriteOptions options =
                new StackWriteOptions(
                        writeAttributes(transformedElement),
                        outputter.getSuggestedFormatToWrite());
        
        writeToFilesystem(elementUntransformed, transformedElement, filenameWithoutExtension, options, outputName, outputter);
     }
    
    /** Writes the transformed element (i.e. the image) to the file-system */
    private void writeToFilesystem(T elementUntransformed, Stack transformedElement, Optional<String> filenameWithoutExtension, StackWriteOptions options, String outputName, ElementOutputter outputter) throws OutputWriteFailedException {
    	try {
	        String extension =
	                selectFileExtension(transformedElement, options, outputter.getSettings(), outputter.logger());
	
	        Path pathToWriteTo =
	                outputter.makeOutputPath(filenameWithoutExtension, extension, outputName);
	
	        outputter
	                .getExecutionTimeRecorder()
	                .recordExecutionTime(
	                        String.format("Writing raster to file-system (%s)", outputName),
	                        () ->
	                                // First write to the file system, and then write to the
	                                // operation-recorder.
	                                writeToFile(
	                                        elementUntransformed,
	                                        transformedElement,
	                                        options,
	                                        outputter.getSettings(),
	                                        pathToWriteTo));
        
	     } catch (OperationFailedException e) {
	         throw new OutputWriteFailedException(e);
	     }

    }
    
    /**
     * Forms write-options to use for this particular stack by combining the general guarantees for
     * the generator with the specific image-attributes of this particular stack.
     *
     * @param stack the stack to determine {@link StackWriteAttributes} for.
     * @return specific options for {@code stack}.
     */
    private StackWriteAttributes writeAttributes(Stack stack) {
        return StackWriteAttributesFactory.from(stack).or(guaranteedImageAttributes());
    }
}
