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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
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
public abstract class SingleFileTypeGenerator<T, S> implements Generator<T> {

    /** The manifest-description to use if none other is defined. */
    private static final ManifestDescription UNDEFINED_MANIFEST_DESCRIPTION = new ManifestDescription("undefined", "undefined");
    
    /**
     * Assigns a new element, and then calls {@link #transform()}.
     *
     * @param element element to be assigned and then transformed
     * @return the transformed element after necessary preprocessing.
     * @throws OutputWriteFailedException
     */
    public S transform(T element) throws OutputWriteFailedException {
        try {
            assignElement(element);
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
        return transform();
    }

    /**
     * Applies any necessary preprocessing, to create an element suitable for writing to the
     * filesystem.
     *
     * @return the transformed element after necessary preprocessing.
     * @throws OutputWriteFailedException if anything goes wrong
     */
    public abstract S transform() throws OutputWriteFailedException;

    public abstract void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException;

    public abstract String getFileExtension(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException;

    public abstract Optional<ManifestDescription> createManifestDescription();

    // We delegate to a much simpler method, for single file generators
    @Override
    public void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        writeInternal(
                outputNameStyle.getFilenameWithoutExtension(), outputNameStyle.getOutputName(), "", outputter);
    }

    /** As only a single-file is involved, this methods delegates to a simpler virtual method. */
    @Override
    public int writeWithIndex(
            IndexableOutputNameStyle outputNameStyle, String index, OutputterChecked outputter)
            throws OutputWriteFailedException {

        writeInternal(
                outputNameStyle.getFilenameWithoutExtension(index),
                outputNameStyle.getOutputName(),
                index,
                outputter);

        return 1;
    }

    // We create a single file type
    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return Optional.of( createFileTypeArray(createManifestDescription(), outputWriteSettings) );
    }
    
    private FileType[] createFileTypeArray(Optional<ManifestDescription> description, OutputWriteSettings outputWriteSettings) throws OperationFailedException {
        ManifestDescription selectedDescription = description.orElse(UNDEFINED_MANIFEST_DESCRIPTION);
        return new FileType[] {new FileType(selectedDescription, getFileExtension(outputWriteSettings))};
    }

    private void writeInternal(
            String filenameWithoutExtension,
            String outputName,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        try {
            Path pathToWriteTo =
                    outputter.makeOutputPath(
                            filenameWithoutExtension,
                            getFileExtension(outputter.getSettings()));

            // First write to the file system, and then write to the operation-recorder. Thi
            writeToFile(outputter.getSettings(), pathToWriteTo);

            createManifestDescription().ifPresent(
                    manifestDescription ->
                            outputter.writeFileToOperationRecorder(
                                    outputName, pathToWriteTo, manifestDescription, index));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
