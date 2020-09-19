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
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * A {@link Generator} that eventually writes only a single-file to the filesystem.
 * 
 * @author Owen Feehan
 *
 * @param <T> iteration-type
 * @param <S> type after any necessary preprocessing 
 */
public abstract class SingleFileTypeGenerator<T,S> implements Generator, IterableSingleFileTypeGenerator<T, S> {

    /**
     * Applies any necessary preprocessing, to create an element suitable for writing to the filesystem.
     * 
     * @return the iteration-type after necessary preprocessing.
     * 
     * @throws OutputWriteFailedException if anything goes wrong
     */
    public abstract S transform() throws OutputWriteFailedException;
    
    // We delegate to a much simpler method, for single file generators
    @Override
    public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException {
        writeInternal(
                outputNameStyle.getPhysicalName(),
                outputNameStyle.getOutputName(),
                "",
                outputManager);
    }

    // We delegate to a much simpler method, for single file generators
    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        writeInternal(
                outputNameStyle.getPhysicalName(index),
                outputNameStyle.getOutputName(),
                index,
                outputManager);

        return 1;
    }

    // We create a single file type
    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) throws OperationFailedException {
        Optional<ManifestDescription> manifestDescription = createManifestDescription();
        return OptionalUtilities.map( manifestDescription,
                md -> new FileType[] {new FileType(md, getFileExtension(outputWriteSettings))});
    }

    public abstract void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException;

    public abstract String getFileExtension(OutputWriteSettings outputWriteSettings) throws OperationFailedException;

    public abstract Optional<ManifestDescription> createManifestDescription();

    private void writeInternal(
            String filePhysicalNameWithoutExtension,
            String outputName,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        assert (outputManager.getOutputWriteSettings() != null);
        try {
            Path outFilePath =
                    outputManager.outFilePath(
                            filePhysicalNameWithoutExtension
                                    + "."
                                    + getFileExtension(outputManager.getOutputWriteSettings()));
    
            // First write to the file system, and then write to the operation-recorder. Thi
            writeToFile(outputManager.getOutputWriteSettings(), outFilePath);
    
            Optional<ManifestDescription> manifestDescription = createManifestDescription();
            manifestDescription.ifPresent(
                    md ->
                            outputManager.writeFileToOperationRecorder(
                                    outputName, outFilePath, md, index));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
