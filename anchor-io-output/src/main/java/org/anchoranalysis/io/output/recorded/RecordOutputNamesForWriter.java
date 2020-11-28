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
package org.anchoranalysis.io.output.recorded;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.SubdirectoryBase;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.writer.ElementSupplier;
import org.anchoranalysis.io.output.writer.ElementWriterSupplier;
import org.anchoranalysis.io.output.writer.Writer;

/**
 * Delegates to another writer while recording output-names passed to functions.
 *
 * <p>Outputs are recorded differently based upon if they were or were not allowed.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
class RecordOutputNamesForWriter implements Writer {

    // START REQUIRED ARGUMENTS
    /** The writer to delegate to. */
    private final Writer writer;

    /** What all outputs that this write processes are added to. */
    private final RecordedOutputs recordedOutputs;

    /**
     * Whether to record the output-names used in indexable outputs (usually the contents of
     * sub-directories)/
     */
    private final boolean includeIndexableOutputs;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<OutputterChecked> createSubdirectory(
            String outputName,
            ManifestDirectoryDescription manifestDescription,
            Optional<SubdirectoryBase> manifestDirectory,
            boolean inheritOutputRulesAndRecording)
            throws OutputWriteFailedException {
        Optional<OutputterChecked> outputter =
                writer.createSubdirectory(
                        outputName,
                        manifestDescription,
                        manifestDirectory,
                        inheritOutputRulesAndRecording);
        recordedOutputs.add(outputName, outputter.isPresent());
        return outputter;
    }

    @Override
    public <T> boolean write(
            String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element)
            throws OutputWriteFailedException {
        boolean allowed = writer.write(outputName, elementWriter, element);
        recordedOutputs.add(outputName, allowed);
        return allowed;
    }

    @Override
    public <T> Optional<FileType[]> writeWithIndex(
            IndexableOutputNameStyle outputNameStyle,
            ElementWriterSupplier<T> elementWriter,
            ElementSupplier<T> element,
            String index)
            throws OutputWriteFailedException {

        Optional<FileType[]> fileTypesWritten =
                writer.writeWithIndex(outputNameStyle, elementWriter, element, index);

        if (includeIndexableOutputs) {
            recordedOutputs.add(outputNameStyle.getOutputName(), fileTypesWritten.isPresent());
        }

        return fileTypesWritten;
    }

    @Override
    public <T> boolean writeWithoutName(
            String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element)
            throws OutputWriteFailedException {
        boolean allowed = writer.writeWithoutName(outputName, elementWriter, element);
        recordedOutputs.add(outputName, allowed);
        return allowed;
    }

    @Override
    public Optional<Path> createFilenameForWriting(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {
        Optional<Path> filename =
                writer.createFilenameForWriting(outputName, extension, manifestDescription);
        recordedOutputs.add(outputName, filename.isPresent());
        return filename;
    }
}
