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
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.SimpleOutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Allows every output, irrespective of whether the {@link OutputterChecked} allows the output-name.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class AlwaysAllowed implements Writer {

    // START REQUIRED ARGUMENTS
    /** Bound output manager */
    private final OutputterChecked outputter;

    /** Execute before every operation if defined. */
    private final Optional<WriterExecuteBeforeEveryOperation> preop;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<OutputterChecked> createSubdirectory(
            String outputName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> manifestFolder,
            boolean inheritOutputRulesAndRecording)
            throws OutputWriteFailedException {

        maybeExecutePreop();
        return Optional.of(
                outputter.deriveSubdirectory(
                        outputName,
                        manifestDescription,
                        manifestFolder,
                        inheritOutputRulesAndRecording));
    }
    
    // Write a file without checking if the outputName is allowed
    @Override
    public <T> boolean write(String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element)
            throws OutputWriteFailedException {
        maybeExecutePreop();
        elementWriter.get().write(element, new SimpleOutputNameStyle(outputName), outputter);
        return true;
    }
    
    @Override
    public <T> int writeWithIndex(
            IndexableOutputNameStyle outputNameStyle,
            ElementWriterSupplier<T> elementWriter,
            ElementSupplier<T> element,
            String index)
            throws OutputWriteFailedException {

        maybeExecutePreop();
        return elementWriter.get().write(element, outputNameStyle, index, outputter);
    }

    // A non-generator way of creating outputs, that are still included in the manifest
    // Returns null if output is not allowed
    @Override
    public Optional<Path> createFilenameForWriting(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {

        maybeExecutePreop();

        Path outPath = outputter.makeOutputPath(outputName, extension);

        manifestDescription.ifPresent(
                md -> outputter.writeFileToOperationRecorder(outputName, outPath, md, ""));
        return Optional.of(outPath);
    }

    private void maybeExecutePreop() {
        preop.ifPresent(WriterExecuteBeforeEveryOperation::execute);
    }
}
