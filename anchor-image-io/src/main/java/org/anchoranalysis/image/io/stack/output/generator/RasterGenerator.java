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
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.image.io.stack.output.StackWriteOptionsFactory;
import org.anchoranalysis.io.generator.TransformingGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Transfroms an entity to a {@link Stack} and writes it to the file-system.
 *
 * @author Owen Feehan
 */
public abstract class RasterGenerator<T> implements TransformingGenerator<T, Stack> {

    // A fallback manifest-description if none is supplied by the generator
    private static final ManifestDescription MANIFEST_DESCRIPTION_FALLBACK =
            new ManifestDescription("raster", "unknown");

    @Override
    public FileType[] write(T element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return writeInternal(
                element,
                outputNameStyle.getFilenameWithoutExtension(),
                outputNameStyle.getOutputName(),
                "",
                outputter);
    }

    /** As only a single-file is involved, this methods delegates to a simpler virtual method. */
    @Override
    public FileType[] writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter)
            throws OutputWriteFailedException {
        return writeInternal(
                element,
                outputNameStyle.getFilenameWithoutExtension(index),
                outputNameStyle.getOutputName(),
                index,
                outputter);
    }

    /**
     * Guarantees on the attributes of all images created by the generator.
     *
     * @return options that are guaranteed to be true of all images by the generator.
     */
    public abstract StackWriteOptions guaranteedImageAttributes();

    public abstract Optional<ManifestDescription> createManifestDescription();

    /**
     * Selects the file-extension to use for a particular stack.
     *
     * @param stack the stack to select a file-extension for
     * @param options options that describe how {@code stack} should be written
     * @param settings general settings for writing output
     * @return the file extension without any leading period
     * @throws OperationFailedException
     */
    protected abstract String selectFileExtension(
            Stack stack, StackWriteOptions options, OutputWriteSettings settings)
            throws OperationFailedException;

    /**
     * Writes a raster to the file-system.
     *
     * @param untransformedElement the element for the generator <i>before</i> transforming to a
     *     {@link Stack}
     * @param transformedElement the {@link Stack} that {@code element} was transformed into
     * @param options options that describe how {@code stack} should be written
     * @param settings general settings for writing output.
     * @param filePath the file-path to write too including the extension.
     * @throws OutputWriteFailedException
     */
    protected abstract void writeToFile(
            T untransformedElement,
            Stack transformedElement,
            StackWriteOptions options,
            OutputWriteSettings settings,
            Path filePath)
            throws OutputWriteFailedException;

    private FileType[] writeInternal(
            T elementUntransformed,
            String filenameWithoutExtension,
            String outputName,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        try {
            Stack transformedElement = transform(elementUntransformed);

            StackWriteOptions options = writeOptions(transformedElement);

            String extension =
                    selectFileExtension(transformedElement, options, outputter.getSettings());

            Path pathToWriteTo = outputter.makeOutputPath(filenameWithoutExtension, extension);

            // First write to the file system, and then write to the operation-recorder.
            writeToFile(
                    elementUntransformed,
                    transformedElement,
                    options,
                    outputter.getSettings(),
                    pathToWriteTo);

            return writeToManifest(outputName, index, outputter, pathToWriteTo, extension);

        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    /**
     * Forms write-options to use for this particular stack by combining the general guarantees for
     * the generator with the specific image-attributes of this particular stack.
     *
     * @param stack the stack to determine {@link StackWriteOptions} for.
     * @return specific options for {@code stack}.
     */
    private StackWriteOptions writeOptions(Stack stack) {
        return StackWriteOptionsFactory.from(stack).or(guaranteedImageAttributes());
    }

    /** Writes to the manifest, and creates an array of the file-types written. */
    private FileType[] writeToManifest(
            String outputName,
            String index,
            OutputterChecked outputter,
            Path pathToWriteTo,
            String extension) {
        Optional<ManifestDescription> manifestDescription = createManifestDescription();

        manifestDescription.ifPresent(
                description ->
                        outputter.writeFileToOperationRecorder(
                                outputName, pathToWriteTo, description, index));

        return createFileTypeArray(
                manifestDescription.orElse(MANIFEST_DESCRIPTION_FALLBACK), extension);
    }

    /** Creates a new {@link FileType} wrapped in a single-item array. */
    private static FileType[] createFileTypeArray(
            ManifestDescription description, String extension) {
        return new FileType[] {new FileType(description, extension)};
    }
}