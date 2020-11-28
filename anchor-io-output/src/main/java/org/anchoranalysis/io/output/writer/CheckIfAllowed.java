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
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.SubdirectoryBase;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Only allows outputs, if the output-name is allowed in the {@link OutputterChecked}.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class CheckIfAllowed implements Writer {

    public static final int NUMBER_ELEMENTS_WRITTEN_NOT_ALLOWED = -2;

    // START REQUIRED ARGUMENTS
    /** Whether a particular output is enabled or not? */
    private final SingleLevelOutputEnabled outputEnabled;

    /** If defined, execute before every operation */
    private final Optional<WriterExecuteBeforeEveryOperation> preop;

    /** The writer. */
    private final Writer writer;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<OutputterChecked> createSubdirectory(
            String outputName,
            ManifestDirectoryDescription manifestDescription,
            Optional<SubdirectoryBase> manifestDirectory,
            boolean inheritOutputRulesAndRecording)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return Optional.empty();
        }

        maybeExecutePreop();

        return writer.createSubdirectory(
                outputName, manifestDescription, manifestDirectory, inheritOutputRulesAndRecording);
    }

    @Override
    public <T> boolean write(
            String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return false;
        }

        maybeExecutePreop();

        writer.write(outputName, elementWriter, element);

        return true;
    }

    @Override
    public <T> Optional<FileType[]> writeWithIndex(
            IndexableOutputNameStyle outputNameStyle,
            ElementWriterSupplier<T> elementWriter,
            ElementSupplier<T> element,
            String index)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputNameStyle.getOutputName())) {
            return Optional.empty();
        }

        maybeExecutePreop();

        return writer.writeWithIndex(outputNameStyle, elementWriter, element, index);
    }

    @Override
    public <T> boolean writeWithoutName(
            String outputName, ElementWriterSupplier<T> elementWriter, ElementSupplier<T> element)
            throws OutputWriteFailedException {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return false;
        }

        maybeExecutePreop();

        return writer.writeWithoutName(outputName, elementWriter, element);
    }

    @Override
    public Optional<Path> createFilenameForWriting(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {

        if (!outputEnabled.isOutputEnabled(outputName)) {
            return Optional.empty();
        }

        maybeExecutePreop();

        return writer.createFilenameForWriting(outputName, extension, manifestDescription);
    }

    private void maybeExecutePreop() {
        preop.ifPresent(WriterExecuteBeforeEveryOperation::execute);
    }
}
