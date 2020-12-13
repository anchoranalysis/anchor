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
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * A {@link Generator} that eventually writes only a single file to the filesystem.
 *
 * @author Owen Feehan
 * @param <T> iteration-type
 * @param <S> type after any necessary preprocessing
 */
public abstract class SingleFileTypeGenerator<T, S> implements TransformingGenerator<T, S> {

    /** The manifest-description to use if none other is defined. */
    private static final ManifestDescription UNDEFINED_MANIFEST_DESCRIPTION =
            new ManifestDescription("undefined", "undefined");

    public abstract void writeToFile(
            T element, OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException;

    public abstract String selectFileExtension(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException;

    public abstract Optional<ManifestDescription> createManifestDescription();

    /**
     * Lazy creation of the array of file-types created. This is cached here so it can be reused.
     */
    private FileType[] fileTypes;

    @Override
    public FileType[] write(T element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return writeInternal(
                element,
                outputNameStyle.filenameWithoutExtension(),
                outputNameStyle.getOutputName(),
                "",
                outputter);
    }

    @Override
    public FileType[] writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter)
            throws OutputWriteFailedException {
        return writeInternal(
                element,
                Optional.of(outputNameStyle.filenameWithoutExtension(index)),
                outputNameStyle.getOutputName(),
                index,
                outputter);
    }

    private FileType[] writeInternal(
            T element,
            Optional<String> filenameWithoutExtension,
            String outputName,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        try {
            String fileExtension = selectFileExtension(outputter.getSettings());

            Path pathToWriteTo =
                    outputter.makeOutputPath(filenameWithoutExtension, fileExtension, outputName);

            // First write to the file system, and then write to the operation-recorder.
            writeToFile(element, outputter.getSettings(), pathToWriteTo);

            return writeToManifest(outputName, index, outputter, pathToWriteTo);

        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    /**
     * Writes to the manifest, and creates an array of the file-types written.
     *
     * @throws OperationFailedException
     */
    private FileType[] writeToManifest(
            String outputName, String index, OutputterChecked outputter, Path pathToWriteTo)
            throws OperationFailedException {

        Optional<ManifestDescription> manifestDescription = createManifestDescription();

        manifestDescription.ifPresent(
                description ->
                        outputter.writeFileToOperationRecorder(
                                outputName, pathToWriteTo, description, index));

        if (fileTypes == null) {
            fileTypes = buildFileTypeArray(manifestDescription, outputter.getSettings());
        }
        return fileTypes;
    }

    /**
     * The types of files the generator writes to the filesystem.
     *
     * @param outputWriteSettings general settings for outputting
     * @return an array of all file-types written, if any exist
     * @throws OperationFailedException if anything goes wrong
     */
    private FileType[] buildFileTypeArray(
            Optional<ManifestDescription> manifestDescription,
            OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        ManifestDescription selectedDescription =
                manifestDescription.orElse(UNDEFINED_MANIFEST_DESCRIPTION);
        return new FileType[] {
            new FileType(selectedDescription, selectFileExtension(outputWriteSettings))
        };
    }
}
